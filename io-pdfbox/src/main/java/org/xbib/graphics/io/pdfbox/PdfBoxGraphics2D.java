package org.xbib.graphics.io.pdfbox;

import org.xbib.graphics.io.pdfbox.draw.DefaultDrawControl;
import org.xbib.graphics.io.pdfbox.draw.DrawControl;
import org.xbib.graphics.io.pdfbox.draw.DrawControl.DrawControlEnvironment;
import org.xbib.graphics.io.pdfbox.color.ColorMapper;
import org.xbib.graphics.io.pdfbox.color.DefaultColorMapper;
import org.xbib.graphics.io.pdfbox.font.DefaultFontDrawer;
import org.xbib.graphics.io.pdfbox.font.FontDrawer;
import org.xbib.graphics.io.pdfbox.font.FontDrawer.FontDrawerEnvironment;
import org.xbib.graphics.io.pdfbox.image.ImageEncoder;
import org.xbib.graphics.io.pdfbox.image.LosslessImageEncoder;
import org.xbib.graphics.io.pdfbox.paint.DefaultPaintApplier;
import org.xbib.graphics.io.pdfbox.paint.PaintApplier;
import org.xbib.graphics.io.pdfbox.paint.PaintApplier.PaintApplierEnvironment;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graphics 2D Adapter for PDFBox.
 */
public class PdfBoxGraphics2D extends Graphics2D {

    private final Graphics2D calcGfx;

    private final PDFormXObject xFormObject;

    private final PDPageContentStream contentStream;

    private final BufferedImage calcImage;

    private final PDDocument document;

    private final AffineTransform baseTransform;

    private final CopyInfo copyInfo;

    private final List<CopyInfo> copyList;

    private final DefaultPaintApplierEnvironment paintEnv;

    private AffineTransform transform;

    private ImageEncoder imageEncoder;

    private ColorMapper colorMapper;

    private PaintApplier paintApplier;

    private FontDrawer fontDrawer;

    private DrawControl drawControl;

    private Paint paint;

    private Stroke stroke;

    private Color xorColor;

    private Font font;

    private Composite composite;

    private Shape clipShape;

    private Color backgroundColor;

    private int saveCounter;

    private boolean disposed;

    /**
     * Do we currently have an active path on the content stream, which has not been
     * closed?
     * We need this flag to avoid to clip twice if both the plaint applyer needs to
     * clip and we have some clipping. If at the end we try to clip with an empty
     * path, then Acrobat Reader does not like that and draws nothing.
     */
    private boolean hasPathOnStream;

    private Map<Key, Object> renderingHints;

    private final DrawControlEnvironment drawControlEnvironment = new DrawControlEnvironment() {
        @Override
        public Paint getPaint() {
            return paint;
        }

        @Override
        public PdfBoxGraphics2D getGraphics() {
            return PdfBoxGraphics2D.this;
        }
    };

    private final FontDrawerEnvironment fontDrawerEnv = new FontDrawerEnvironment() {
        @Override
        public PDDocument getDocument() {
            return document;
        }

        @Override
        public PDPageContentStream getContentStream() {
            return contentStream;
        }

        @Override
        public Font getFont() {
            return font;
        }

        @Override
        public Paint getPaint() {
            return paint;
        }

        @Override
        public void applyPaint(Paint paint, Shape shapeToDraw) throws IOException {
            PDShading pdShading = PdfBoxGraphics2D.this.applyPaint(paint, shapeToDraw);
            if (pdShading != null) {
                applyShadingAsColor(pdShading);
            }
        }

        @Override
        public FontRenderContext getFontRenderContext() {
            return PdfBoxGraphics2D.this.getFontRenderContext();
        }

        @Override
        public PDRectangle getGraphicsBBox() {
            return xFormObject.getBBox();
        }

        @Override
        public PDResources getResources() {
            return xFormObject.getResources();
        }

        @Override
        public Graphics2D getCalculationGraphics() {
            return calcGfx;
        }
    };

    /**
     * Create a PDfBox Graphics2D. This size is used for the BBox of the XForm. So
     * everything drawn outside the rectangle (0x0)-(pixelWidth,pixelHeight) will be
     * clipped.
     * Note: pixelWidth and pixelHeight only define the size of the coordinate space
     * within this Graphics2D. They do not affect how big the XForm is finally
     * displayed in the PDF.
     *
     * @param document    The document the graphics should be used to create a XForm in.
     * @param pixelWidth  the width in pixel of the drawing area.
     * @param pixelHeight the height in pixel of the drawing area.
     * @throws IOException if something goes wrong with writing into the content stream of
     *                     the {@link PDDocument}.
     */
    public PdfBoxGraphics2D(PDDocument document, int pixelWidth, int pixelHeight)
            throws IOException {
        this(document, new PDRectangle(pixelWidth, pixelHeight));
    }

    /**
     * Create a PDfBox Graphics2D. This size is used for the BBox of the XForm. So
     * everything drawn outside the rectangle (0x0)-(pixelWidth,pixelHeight) will be
     * clipped.
     * Note: pixelWidth and pixelHeight only define the size of the coordinate space
     * within this Graphics2D. They do not affect how big the XForm is finally
     * displayed in the PDF.
     *
     * @param document    The document the graphics should be used to create a XForm in.
     * @param pixelWidth  the width in pixel of the drawing area.
     * @param pixelHeight the height in pixel of the drawing area.
     * @throws IOException if something goes wrong with writing into the content stream of
     *                     the {@link PDDocument}.
     */
    public PdfBoxGraphics2D(PDDocument document, float pixelWidth, float pixelHeight)
            throws IOException {
        this(document, new PDRectangle(pixelWidth, pixelHeight));
    }

    /**
     * @param document The document the graphics should be used to create a XForm in.
     * @param bbox     Bounding Box of the graphics
     * @throws IOException when something goes wrong with writing into the content stream of
     *                     the {@link PDDocument}.
     */
    public PdfBoxGraphics2D(PDDocument document, PDRectangle bbox) throws IOException {
        this(document, bbox, null);
    }

    public PdfBoxGraphics2D(PDDocument document,
                            PDRectangle bbox,
                            PdfBoxGraphics2D parentGfx)
            throws IOException {
        this(document, createXObject(document, bbox), parentGfx);
    }

    public PdfBoxGraphics2D(PDDocument document,
                            PDFormXObject xFormObject,
                            PdfBoxGraphics2D parentGfx)
            throws IOException {
        this(document, xFormObject, new PDPageContentStream(document, xFormObject,
                xFormObject.getStream().createOutputStream(COSName.FLATE_DECODE)), parentGfx);
    }

    public PdfBoxGraphics2D(PDDocument document,
                            PDFormXObject xFormObject,
                            PDPageContentStream contentStream,
                            PdfBoxGraphics2D parentGfx)
            throws IOException {
        this.document = document;
        this.xFormObject = xFormObject;
        this.contentStream = contentStream;
        contentStreamSaveState();
        if (parentGfx != null) {
            this.colorMapper = parentGfx.colorMapper;
            this.fontDrawer = parentGfx.fontDrawer;
            this.imageEncoder = parentGfx.imageEncoder;
            this.paintApplier = parentGfx.paintApplier;
        }
        baseTransform = new AffineTransform();
        baseTransform.translate(0, xFormObject.getBBox().getHeight());
        baseTransform.scale(1, -1);
        calcImage = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
        calcGfx = calcImage.createGraphics();
        font = calcGfx.getFont();
        copyInfo = null;
        transform = new AffineTransform();
        imageEncoder = new LosslessImageEncoder();
        colorMapper = new DefaultColorMapper();
        paintApplier = new DefaultPaintApplier();
        fontDrawer = new DefaultFontDrawer();
        drawControl = DefaultDrawControl.INSTANCE;
        copyList = new ArrayList<>();
        paintEnv = new DefaultPaintApplierEnvironment();
        renderingHints = new HashMap<>();
    }

    private PdfBoxGraphics2D(PdfBoxGraphics2D pdfBoxGraphics2D) throws IOException {
        CopyInfo info = new CopyInfo();
        info.creatingContextInfo = null;
        info.copy = this;
        info.sourceGfx = pdfBoxGraphics2D;
        pdfBoxGraphics2D.copyList.add(info);
        this.copyInfo = info;
        this.hasPathOnStream = false;
        this.document = pdfBoxGraphics2D.document;
        this.xFormObject = pdfBoxGraphics2D.xFormObject;
        this.contentStream = pdfBoxGraphics2D.contentStream;
        this.baseTransform = pdfBoxGraphics2D.baseTransform;
        this.transform = (AffineTransform) pdfBoxGraphics2D.transform.clone();
        this.calcGfx = pdfBoxGraphics2D.calcGfx;
        this.calcImage = pdfBoxGraphics2D.calcImage;
        this.font = pdfBoxGraphics2D.font;
        this.stroke = pdfBoxGraphics2D.stroke;
        this.paint = pdfBoxGraphics2D.paint;
        this.clipShape = pdfBoxGraphics2D.clipShape;
        this.backgroundColor = pdfBoxGraphics2D.backgroundColor;
        this.colorMapper = pdfBoxGraphics2D.colorMapper;
        this.fontDrawer = pdfBoxGraphics2D.fontDrawer;
        this.imageEncoder = pdfBoxGraphics2D.imageEncoder;
        this.paintApplier = pdfBoxGraphics2D.paintApplier;
        this.drawControl = pdfBoxGraphics2D.drawControl;
        this.composite = pdfBoxGraphics2D.composite;
        this.renderingHints = new HashMap<>(pdfBoxGraphics2D.renderingHints);
        this.xorColor = pdfBoxGraphics2D.xorColor;
        this.saveCounter = 0;
        this.copyList = new ArrayList<>();
        this.disposed = false;
        this.paintEnv = pdfBoxGraphics2D.paintEnv;
        this.hasPathOnStream = pdfBoxGraphics2D.hasPathOnStream;
        this.renderingHints = pdfBoxGraphics2D.renderingHints;
        contentStreamSaveState();
    }

    @Override
    public void dispose() {
        if (copyInfo != null) {
            copyInfo.sourceGfx.copyList.remove(copyInfo);
            try {
                contentStreamRestoreState();
            } catch (IOException e) {
                throw new PdfBoxGraphics2dException(e);
            }
            if (this.saveCounter != 0)
                throw new IllegalStateException("Copy - SaveCounter should be 0, but is " + this.saveCounter);
            return;
        }
        if (copyList.size() > 0)
            throw new IllegalStateException("Not all PdfGraphics2D copies were destroyed! Please ensure that all create() calls get a matching dispose() on the returned copies. Also consider using disposeDanglingChildGraphics()");
        try {
            contentStreamRestoreState();
            contentStream.close();
        } catch (IOException e) {
            throw new PdfBoxGraphics2dException(e);
        }
        if (this.saveCounter != 0) {
            throw new IllegalStateException("SaveCounter should be 0, but is " + this.saveCounter);
        }
        calcGfx.dispose();
        calcImage.flush();
        disposed = true;
    }

    @Override
    public void draw(Shape s) {
        checkNoCopyActive();
        if (paint == null) {
            return;
        }
        try {
            contentStreamSaveState();
            Shape shapeToDraw = drawControl.transformShapeBeforeDraw(s, drawControlEnvironment);
            if (shapeToDraw != null) {
                walkShape(shapeToDraw);
                PDShading pdShading = applyPaint(shapeToDraw);
                if (pdShading != null) {
                    applyShadingAsColor(pdShading);
                }
                if (stroke instanceof BasicStroke) {
                    BasicStroke basicStroke = (BasicStroke) this.stroke;
                    contentStream.setLineCapStyle(basicStroke.getEndCap());
                    contentStream.setLineJoinStyle(basicStroke.getLineJoin());
                    if (basicStroke.getMiterLimit() > 0) {
                        contentStream.setMiterLimit(basicStroke.getMiterLimit());
                    }
                    AffineTransform tf = new AffineTransform();
                    tf.concatenate(baseTransform);
                    tf.concatenate(transform);
                    double scaleX = tf.getScaleX();
                    contentStream.setLineWidth((float) Math.abs(basicStroke.getLineWidth() * scaleX));
                    float[] dashArray = basicStroke.getDashArray();
                    if (dashArray != null) {
                        for (int i = 0; i < dashArray.length; i++) {
                            dashArray[i] = (float) Math.abs(dashArray[i] * scaleX);
                        }
                        contentStream.setLineDashPattern(dashArray,
                                (float) Math.abs(basicStroke.getDashPhase() * scaleX));
                    }
                }
                contentStream.stroke();
                hasPathOnStream = false;
            }
            drawControl.afterShapeDraw(s, drawControlEnvironment);
            contentStreamRestoreState();
        } catch (IOException e) {
            throw new PdfBoxGraphics2dException(e);
        }
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        BufferedImage img1 = op.filter(img, null);
        drawImage(img1, new AffineTransform(1f, 0f, 0f, 1f, x, y), null);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        WritableRaster data = img.copyData(null);
        drawImage(new BufferedImage(img.getColorModel(), data, false, null), xform, null);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        drawRenderedImage(img.createDefaultRendering(), xform);
    }

    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float) x, (float) y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        AttributedString attributedString = new AttributedString(str);
        attributedString.addAttribute(TextAttribute.FONT, font);
        drawString(attributedString.getIterator(), x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        AffineTransform tf = new AffineTransform();
        tf.translate(x, y);
        tf.scale((float) width / img.getWidth(null), (float) height / img.getHeight(null));
        return drawImage(img, tf, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor,
                             ImageObserver observer) {
        try {
            if (bgcolor != null) {
                contentStream.setNonStrokingColor(colorMapper.mapColor(contentStream, bgcolor));
                walkShape(new Rectangle(x, y, width, height));
                contentStream.fill();
            }
            return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), observer);
        } catch (IOException e) {
            throw new PdfBoxGraphics2dException(e);
        }
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
                             int sx2, int sy2, ImageObserver observer) {
        return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy2, sx2, sy2, null, observer);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        checkNoCopyActive();
        AffineTransform tf = new AffineTransform();
        tf.concatenate(baseTransform);
        tf.concatenate(transform);
        if (xform != null) {
            tf.concatenate((AffineTransform) xform.clone());
        }
        PDImageXObject pdImage = imageEncoder.encodeImage(document, contentStream, img);
        try {
            contentStreamSaveState();
            int imgHeight = img.getHeight(obs);
            tf.translate(0, imgHeight);
            tf.scale(1, -1);
            contentStream.transform(new Matrix(tf));
            Object keyInterpolation = renderingHints.get(RenderingHints.KEY_INTERPOLATION);
            if (RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.equals(keyInterpolation)) {
                pdImage.setInterpolate(false);
            }
            if (composite != null) {
                applyPaint(null);
            }
            contentStream.drawImage(pdImage, 0, 0, img.getWidth(obs), imgHeight);
            contentStreamRestoreState();
        } catch (IOException e) {
            throw new PdfBoxGraphics2dException(e);
        }
        return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
                             int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        try {
            contentStreamSaveState();
            int width = dx2 - dx1;
            int height = dy2 - dy1;
            walkShape(new Rectangle2D.Double(dx1, dy1, width, height));
            contentStream.clip();
            if (bgcolor != null) {
                contentStream.setNonStrokingColor(colorMapper.mapColor(contentStream, bgcolor));
                walkShape(new Rectangle(dx1, dy1, width, height));
                contentStream.fill();
            }
            AffineTransform tf = new AffineTransform();
            tf.translate(dx1, dy1);
            float imgWidth = img.getWidth(observer);
            float imgHeight = img.getHeight(observer);
            tf.scale((float) width / imgWidth, (float) height / imgHeight);
            tf.translate(-sx1, -sy1);
            tf.scale((sx2 - sx1) / imgWidth, (sy2 - sy1) / imgHeight);
            drawImage(img, tf, observer);
            contentStreamRestoreState();
            return true;
        } catch (IOException e) {
            throw new PdfBoxGraphics2dException(e);
        }
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        if (paint == null) {
            return;
        }
        try {
            contentStreamSaveState();
            if (fontDrawer.canDrawText((AttributedCharacterIterator) iterator.clone(), fontDrawerEnv)) {
                drawStringUsingText(iterator, x, y);
            } else {
                drawStringUsingShapes(iterator, x, y);
            }
            contentStreamRestoreState();
        } catch (IOException | FontFormatException e) {
            throw new PdfBoxGraphics2dException(e);
        }
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        checkNoCopyActive();
        AffineTransform transformOrig = (AffineTransform) transform.clone();
        transform.translate(x, y);
        fill(g.getOutline());
        transform = transformOrig;
    }

    @Override
    public void fill(Shape s) {
        checkNoCopyActive();
        if (paint == null) {
            return;
        }
        try {
            contentStreamSaveState();
            Shape shapeToFill = drawControl.transformShapeBeforeFill(s, drawControlEnvironment);
            if (shapeToFill != null) {
                boolean useEvenOdd = walkShape(shapeToFill);
                PDShading shading = applyPaint(shapeToFill);
                if (shading != null) {
                    Rectangle2D r2d = s.getBounds2D();
                    if ((r2d.getWidth() <= 0) || (r2d.getHeight() <= 0)) {
                        applyShadingAsColor(shading);
                        fill(useEvenOdd);
                    } else {
                        internalClip(useEvenOdd);
                        contentStream.shadingFill(shading);
                    }
                } else {
                    fill(useEvenOdd);
                }
                hasPathOnStream = false;
            }
            drawControl.afterShapeFill(s, drawControlEnvironment);
            contentStreamRestoreState();
        } catch (IOException e) {
            throw new PdfBoxGraphics2dException(e);
        }
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return false;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }

    @Override
    public void setComposite(Composite comp) {
        composite = comp;
    }

    @Override
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        renderingHints.put(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        return renderingHints.get(hintKey);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        hints.clear();
        addRenderingHints(hints);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        renderingHints.putAll((Map<? extends Key, ?>) hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return new RenderingHints(renderingHints);
    }

    /**
     * Creates a copy of this graphics object. Please call {@link #dispose()} always
     * on the copy after you have finished drawing with it.
     * Never draw both in this copy and its parent graphics at the same time, as
     * they all write to the same content stream. This will create a broken PDF
     * content stream. You should get an {@link IllegalStateException} if
     * you do so, but better just don't try.
     * The copy allows you to have different transforms, paints, etc. than the
     * parent graphics context without affecting the parent. You may also call
     * create() on a copy, but always remember to call {@link #dispose()} in reverse
     * order.
     *
     * @return a copy of this Graphics.
     */
    @Override
    public PdfBoxGraphics2D create() {
        try {
            return new PdfBoxGraphics2D(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PdfBoxGraphics2D create(int x, int y, int width, int height) {
        return (PdfBoxGraphics2D) super.create(x, y, width, height);
    }

    @Override
    public void translate(int x, int y) {
        transform.translate(x, y);
    }

    @Override
    public Color getColor() {
        if (paint instanceof Color) {
            return (Color) paint;
        }
        return null;
    }

    @Override
    public void setColor(Color color) {
        this.paint = color;
    }

    @Override
    public void setPaintMode() {
        xorColor = null;
    }

    /**
     * XOR Mode is currently not implemented as it's not possible in PDF. This mode is ignored.
     *
     * @param c1 the XORMode Color
     */
    @Override
    public void setXORMode(Color c1) {
        xorColor = c1;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        try {
            return fontDrawer.getFontMetrics(f, fontDrawerEnv);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Rectangle getClipBounds() {
        Shape clip = getClip();
        if (clip != null) {
            return clip.getBounds();
        }
        return null;
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
        clip(rect);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    @Override
    public Shape getClip() {
        try {
            return transform.createInverse().createTransformedShape(clipShape);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    @Override
    public void setClip(Shape clip) {
        checkNoCopyActive();
        this.clipShape = transform.createTransformedShape(clip);
        try {
            contentStreamRestoreState();
            contentStreamSaveState();
            if (clip != null) {
                internalClip(walkShape(clip));
            }
        } catch (IOException e) {
            throw new PdfBoxGraphics2dException(e);
        }
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        throw new UnsupportedOperationException("copyArea() not implemented");
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        draw(new Line2D.Double(x1, y1, x2, y2));
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        fill(new Rectangle(x, y, width, height));
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        Paint p = paint;
        paint = backgroundColor;
        fillRect(x, y, width, height);
        paint = p;
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        draw(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        fill(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.PIE));
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++)
            path.lineTo(xPoints[i], yPoints[i]);
        draw(path);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        draw(new Polygon(xPoints, yPoints, nPoints));
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        fill(new Polygon(xPoints, yPoints, nPoints));
    }

    @Override
    public void translate(double tx, double ty) {
        checkNoCopyActive();
        transform.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        checkNoCopyActive();
        transform.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        checkNoCopyActive();
        transform.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        checkNoCopyActive();
        transform.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) {
        checkNoCopyActive();
        transform.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        checkNoCopyActive();
        transform.concatenate(Tx);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        checkNoCopyActive();
        transform = new AffineTransform();
        transform.concatenate(Tx);
    }

    @Override
    public AffineTransform getTransform() {
        return (AffineTransform) transform.clone();
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public Composite getComposite() {
        return composite;
    }

    @Override
    public void setBackground(Color color) {
        backgroundColor = color;
    }

    @Override
    public Color getBackground() {
        return backgroundColor;
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void clip(Shape shape) {
        Shape clip = getClip();
        if (clip == null) {
            setClip(shape);
        } else {
            Area area = new Area(clip);
            area.intersect(new Area(shape));
            setClip(area);
        }
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        calcGfx.addRenderingHints(renderingHints);
        return calcGfx.getFontRenderContext();
    }

    /**
     * @return the PDAppearanceStream which resulted in this graphics
     */
    public PDFormXObject getXFormObject() {
        if (!disposed) {
            throw new IllegalStateException("You can only get the XFormObject after you disposed the Graphics2D object");
        }
        return xFormObject;
    }

    /**
     * Set a new color mapper.
     *
     * @param colorMapper the color mapper which maps Color to PDColor.
     */
    public void setColorMapper(ColorMapper colorMapper) {
        this.colorMapper = colorMapper;
    }

    /**
     * Set a new image encoder
     *
     * @param imageEncoder the image encoder, which encodes a image as PDImageXForm.
     */
    public void setImageEncoder(ImageEncoder imageEncoder) {
        this.imageEncoder = imageEncoder;
    }

    /**
     * Set a new paint applier. You should always derive your custom paint applier
     * from the {@link PaintApplier} and just extend the paint
     * mapping for custom paint.
     * If the paint you map is a paint from a standard library and you can implement
     * the mapping using reflection please feel free to send a pull request to
     * extend the default paint mapper.
     *
     * @param paintApplier the paint applier responsible for mapping the paint correctly
     */
    public void setPaintApplier(PaintApplier paintApplier) {
        this.paintApplier = paintApplier;
    }

    /**
     * Set a new draw control. This allows you to influence fill() and draw()
     * operations. drawString() is only influence if the text is drawn as vector
     * shape.
     *
     * @param drawControl the draw control
     */
    public void setDrawControl(DrawControl drawControl) {
        this.drawControl = drawControl;
    }

    /**
     * Set an optional text drawer. By default, all text is vectorized and drawn
     * using vector shapes. To embed fonts into a PDF file it is necessary to have
     * the underlying TTF file. The java.awt.Font class does not provide that. The
     * FontTextDrawer must perform the java.awt.Font &lt;=&gt; PDFont mapping and
     * also must perform the text layout. If it can not map the text or font
     * correctly, the font drawing falls back to vectoring the text.
     *
     * @param fontDrawer The text drawer, which can draw text using fonts
     */
    public void setFontTextDrawer(FontDrawer fontDrawer) {
        this.fontDrawer = fontDrawer;
    }

    /**
     * Set an internal flag that some path - which may be added from the paint
     * applyer to the content stream or by walkShape() - is on the content stream.
     * We can then safely clip() if there is a path on the content stream.
     */
    public void markPathIsOnStream() {
        hasPathOnStream = true;
    }

    private void fill(boolean useEvenOdd) throws IOException {
        if (useEvenOdd) {
            contentStream.fillEvenOdd();
        } else {
            contentStream.fill();
        }
    }

    private void drawStringUsingShapes(AttributedCharacterIterator iterator, float x, float y) {
        Stroke originalStroke = stroke;
        Paint originalPaint = paint;
        TextLayout textLayout = new TextLayout(iterator, getFontRenderContext());
        textLayout.draw(this, x, y);
        paint = originalPaint;
        stroke = originalStroke;
    }

    private void drawStringUsingText(AttributedCharacterIterator iterator, float x, float y)
            throws IOException, FontFormatException {
        contentStreamSaveState();
        AffineTransform tf = new AffineTransform(baseTransform);
        tf.concatenate(transform);
        tf.translate(x, y);
        contentStream.transform(new Matrix(tf));
        fontDrawer.drawText(iterator, fontDrawerEnv);
        contentStreamRestoreState();
    }

    private void contentStreamSaveState() throws IOException {
        saveCounter++;
        contentStream.saveGraphicsState();
    }

    private void contentStreamRestoreState() throws IOException {
        if (saveCounter == 0) {
            throw new IllegalStateException("Internal save/restore state error. Should never happen.");
        }
        saveCounter--;
        contentStream.restoreGraphicsState();
    }

    private void applyShadingAsColor(PDShading shading) throws IOException {
        PDTilingPattern pattern = new PDTilingPattern();
        pattern.setPaintType(PDTilingPattern.PAINT_COLORED);
        pattern.setTilingType(PDTilingPattern.TILING_CONSTANT_SPACING_FASTER_TILING);
        PDRectangle anchorRect = xFormObject.getBBox();
        pattern.setBBox(anchorRect);
        pattern.setXStep(anchorRect.getWidth());
        pattern.setYStep(anchorRect.getHeight());
        PDAppearanceStream appearance = new PDAppearanceStream(this.document);
        appearance.setResources(pattern.getResources());
        appearance.setBBox(pattern.getBBox());
        PDPageContentStream imageContentStream = new PDPageContentStream(document, appearance,
                ((COSStream) pattern.getCOSObject()).createOutputStream());
        imageContentStream.addRect(0, 0, anchorRect.getWidth(), anchorRect.getHeight());
        imageContentStream.clip();
        imageContentStream.shadingFill(shading);
        imageContentStream.close();
        PDColorSpace patternCS1 = new PDPattern(null);
        COSName tilingPatternName = xFormObject.getResources().add(pattern);
        PDColor patternColor = new PDColor(tilingPatternName, patternCS1);
        contentStream.setNonStrokingColor(patternColor);
        contentStream.setStrokingColor(patternColor);
    }

    private PDShading applyPaint(Shape shapeToDraw) throws IOException {
        return applyPaint(paint, shapeToDraw);
    }

    private PDShading applyPaint(Paint paintToApply, Shape shapeToDraw) throws IOException {
        AffineTransform tf = new AffineTransform(baseTransform);
        tf.concatenate(transform);
        paintEnv.shapeToDraw = shapeToDraw;
        return paintApplier.applyPaint(paintToApply, contentStream, tf, paintEnv);
    }

    /**
     * Walk the path and return true if we need to use the even odd winding rule.
     *
     * @return true if we need to use the even odd winding rule
     */
    private boolean walkShape(Shape clip) throws IOException {
        checkNoCopyActive();
        AffineTransform tf = new AffineTransform(baseTransform);
        tf.concatenate(transform);
        PathIterator pi = clip.getPathIterator(tf);
        float[] coords = new float[6];
        while (!pi.isDone()) {
            int segment = pi.currentSegment(coords);
            switch (segment) {
                case PathIterator.SEG_MOVETO:
                    if (isFinite(coords, 2)) {
                        contentStream.moveTo(coords[0], coords[1]);
                    }
                    break;
                case PathIterator.SEG_LINETO:
                    if (isFinite(coords, 2)) {
                        contentStream.lineTo(coords[0], coords[1]);
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    if (isFinite(coords, 4)) {
                        contentStream.curveTo1(coords[0], coords[1], coords[2], coords[3]);
                    }
                    break;
                case PathIterator.SEG_CUBICTO:
                    if (isFinite(coords, 6)) {
                        contentStream.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    }
                    break;
                case PathIterator.SEG_CLOSE:
                    contentStream.closePath();
                    break;
            }
            pi.next();
        }
        markPathIsOnStream();
        return pi.getWindingRule() == PathIterator.WIND_EVEN_ODD;
    }

    /**
     * Perform a clip, but only if we really have an active clipping path
     *
     * @param useEvenOdd true when we should use the evenOdd rule.
     */
    public void internalClip(boolean useEvenOdd) throws IOException {
        if (hasPathOnStream) {
            if (useEvenOdd) {
                contentStream.clipEvenOdd();
            } else {
                contentStream.clip();
            }
            hasPathOnStream = false;
        }
    }

    private void checkNoCopyActive() {
        if (copyList.size() > 0) {
            throw new IllegalStateException("Don't use the main context as long as a copy is active! Child context is missing a .dispose() call\n"
                    + gatherDebugCopyInfo(this));
        }
    }

    private static PDFormXObject createXObject(PDDocument document, PDRectangle bbox) {
        PDFormXObject xFormObject = new PDAppearanceStream(document);
        xFormObject.setResources(new PDResources());
        xFormObject.setBBox(bbox);
        return xFormObject;
    }

    private static String gatherDebugCopyInfo(PdfBoxGraphics2D gfx) {
        StringBuilder sb = new StringBuilder();
        if (!gfx.copyList.isEmpty()) {
            for (CopyInfo copyInfo : gfx.copyList) {
                sb.append("# Dangling Child").append(copyInfo.toString()).append("\n");
            }
        }
        while (gfx != null) {
            if (gfx.copyList.isEmpty()) {
                sb.append("* Last Child\n");
            } else {
                sb.append("- Parent with ").append(gfx.copyList.size()).append(" childs.\n");
            }
            if (gfx.copyInfo == null) {
                break;
            }
            gfx = gfx.copyInfo.sourceGfx;
        }
        return sb.toString();
    }

    /**
     * Float#isFinite() is JDK 8+. We just copied the trivial implementation here.
     * When we require JDK 8+ we can just drop this method and replace it bei
     * Float#isFinite()
     */
    private static boolean isFinite(float f) {
        return Math.abs(f) <= Float.MAX_VALUE;
    }

    /**
     * @return true when all required values are finite
     */
    private static boolean isFinite(float[] coords, int count) {
        for (int i = 0; i < count; i++) {
            if (!isFinite(coords[i])) {
                return false;
            }
        }
        return true;
    }

    private static void disposeCopies(List<CopyInfo> cl) {
        while (cl.size() > 0) {
            CopyInfo copyInfo = cl.get(0);
            disposeCopies(copyInfo.copy.copyList);
            copyInfo.copy.dispose();
        }
    }

    private class DefaultPaintApplierEnvironment implements PaintApplierEnvironment {

        private Shape shapeToDraw;

        @Override
        public Shape getShapeToDraw() {
            return shapeToDraw;
        }

        @Override
        public ColorMapper getColorMapper() {
            return colorMapper;
        }

        @Override
        public ImageEncoder getImageEncoder() {
            return imageEncoder;
        }

        @Override
        public PDDocument getDocument() {
            return document;
        }

        @Override
        public PDResources getResources() {
            return xFormObject.getResources();
        }

        @Override
        public Composite getComposite() {
            return PdfBoxGraphics2D.this.getComposite();
        }

        @Override
        public PdfBoxGraphics2D getGraphics2D() {
            return PdfBoxGraphics2D.this;
        }

        @Override
        public Color getXORMode() {
            return xorColor;
        }
    }

    private static class CopyInfo {

        private PdfBoxGraphics2D sourceGfx;

        private PdfBoxGraphics2D copy;

        private String creatingContextInfo;

        @Override
        public String toString() {
            return "CopyInfo{creatingContextInfo='" + creatingContextInfo + '\'' + '}';
        }
    }
}
