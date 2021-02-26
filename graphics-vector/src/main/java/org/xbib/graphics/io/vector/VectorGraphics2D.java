package org.xbib.graphics.io.vector;

import static org.xbib.graphics.io.vector.util.ImageUtil.toBufferedImage;
import org.xbib.graphics.io.vector.commands.CreateCommand;
import org.xbib.graphics.io.vector.commands.DisposeCommand;
import org.xbib.graphics.io.vector.commands.DrawImageCommand;
import org.xbib.graphics.io.vector.commands.DrawShapeCommand;
import org.xbib.graphics.io.vector.commands.DrawStringCommand;
import org.xbib.graphics.io.vector.commands.FillShapeCommand;
import org.xbib.graphics.io.vector.commands.RotateCommand;
import org.xbib.graphics.io.vector.commands.ScaleCommand;
import org.xbib.graphics.io.vector.commands.SetBackgroundCommand;
import org.xbib.graphics.io.vector.commands.SetClipCommand;
import org.xbib.graphics.io.vector.commands.SetColorCommand;
import org.xbib.graphics.io.vector.commands.SetCompositeCommand;
import org.xbib.graphics.io.vector.commands.SetFontCommand;
import org.xbib.graphics.io.vector.commands.SetHintCommand;
import org.xbib.graphics.io.vector.commands.SetPaintCommand;
import org.xbib.graphics.io.vector.commands.SetStrokeCommand;
import org.xbib.graphics.io.vector.commands.SetTransformCommand;
import org.xbib.graphics.io.vector.commands.SetXORModeCommand;
import org.xbib.graphics.io.vector.commands.ShearCommand;
import org.xbib.graphics.io.vector.commands.TransformCommand;
import org.xbib.graphics.io.vector.commands.TranslateCommand;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Base for classes that want to implement vector export.
 */
public class VectorGraphics2D extends Graphics2D implements Cloneable {

    private final Processor processor;

    private final PageSize pageSize;

    /**
     * List of operations that were performed on this graphics object and its
     * derived objects.
     */
    private final List<Command<?>> commands;

    private final GraphicsConfiguration deviceConfig;

    /**
     * Context settings used to render fonts.
     */
    private final FontRenderContext fontRenderContext;
    /**
     * Flag that tells whether this graphics object has been disposed.
     */
    private boolean disposed;

    private GraphicsState state;

    private Graphics2D graphics2D;

    public VectorGraphics2D() {
        this(null, null, null);
    }

    public VectorGraphics2D(Processor processor, PageSize pageSize) {
        this(processor, pageSize, null);
    }

    public VectorGraphics2D(Processor processor, PageSize pageSize, Graphics2D graphics2D) {
        this.processor = processor;
        this.pageSize = pageSize;
        this.commands = new LinkedList<>();
        emit(new CreateCommand(this));
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (!graphicsEnvironment.isHeadlessInstance()) {
            GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
            deviceConfig = graphicsDevice.getDefaultConfiguration();
        } else {
            deviceConfig = null;
        }
        fontRenderContext = new FontRenderContext(null, false, true);
        state = new GraphicsState();
        if (graphics2D == null) {
            BufferedImage bufferedImage = new BufferedImage(200, 250, BufferedImage.TYPE_INT_ARGB);
            this.graphics2D = (Graphics2D) bufferedImage.getGraphics();
            this.graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            this.graphics2D = graphics2D;
        }
    }

    public PageSize getPageSize() {
        return pageSize;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (out) {
            writeTo(out);
        }
        return out.toByteArray();
    }

    public void writeTo(OutputStream out) throws IOException {
        processor.process(getCommands(), pageSize).write(out);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        VectorGraphics2D clone = (VectorGraphics2D) super.clone();
        clone.state = (GraphicsState) state.clone();
        return clone;
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        if (isDisposed()) {
            return;
        }
        for (Entry<?, ?> entry : hints.entrySet()) {
            setRenderingHint((Key) entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clip(Shape s) {
        if (isDisposed() || s == null) {
            return;
        }
        if (graphics2D != null) {
            graphics2D.clip(s);
        }
        Shape clip = getClip();
        if (clip != null) {
            s = intersectShapes(clip, s);
        }
        setClip(s);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape s = g.getOutline(x, y);
        draw(s);
    }

    @Override
    public void draw(Shape s) {
        if (isDisposed() || s == null) {
            return;
        }
        emit(new DrawShapeCommand(s));
        if (graphics2D != null) {
            graphics2D.draw(s);
        }
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        BufferedImage bufferedImage = getTransformedImage(img, xform);
        return drawImage(bufferedImage, bufferedImage.getMinX(), bufferedImage.getMinY(),
                bufferedImage.getWidth(), bufferedImage.getHeight(), null, null);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        if (op != null) {
            img = op.filter(img, null);
        }
        drawImage(img, x, y, img.getWidth(), img.getHeight(), null, null);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        drawRenderedImage(img.createDefaultRendering(), xform);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        BufferedImage bimg = toBufferedImage(img);
        drawImage(bimg, xform, null);
    }

    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float) x, (float) y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        if (isDisposed() || str == null || str.trim().length() == 0) {
            return;
        }
        emit(new DrawStringCommand(str, x, y));
        if (graphics2D != null) {
            graphics2D.drawString(str, x, y);
        }
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        StringBuilder buf = new StringBuilder();
        for (char c = iterator.first(); c != AttributedCharacterIterator.DONE; c = iterator.next()) {
            buf.append(c);
        }
        drawString(buf.toString(), x, y);
    }

    @Override
    public void fill(Shape s) {
        if (isDisposed() || s == null) {
            return;
        }
        emit(new FillShapeCommand(s));
        if (graphics2D != null) {
            graphics2D.fill(s);
        }
    }

    @Override
    public Color getBackground() {
        return state.getBackground();
    }

    @Override
    public void setBackground(Color color) {
        if (isDisposed() || color == null || getColor().equals(color)) {
            return;
        }
        emit(new SetBackgroundCommand(color));
        state.setBackground(color);
        if (graphics2D != null) {
            graphics2D.setBackground(color);
        }
    }

    @Override
    public Composite getComposite() {
        return state.getComposite();
    }

    @Override
    public void setComposite(Composite comp) {
        if (isDisposed()) {
            return;
        }
        if (comp == null) {
            throw new IllegalArgumentException("Cannot set a null composite");
        }
        emit(new SetCompositeCommand(comp));
        state.setComposite(comp);
        if (graphics2D != null) {
            graphics2D.setComposite(comp);
        }
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return deviceConfig;
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return fontRenderContext;
    }

    @Override
    public Paint getPaint() {
        return state.getPaint();
    }

    @Override
    public void setPaint(Paint paint) {
        if (isDisposed() || paint == null) {
            return;
        }
        if (paint instanceof Color) {
            setColor((Color) paint);
            return;
        }
        if (getPaint().equals(paint)) {
            return;
        }
        emit(new SetPaintCommand(paint));
        state.setPaint(paint);
        if (graphics2D != null) {
            graphics2D.setPaint(paint);
        }
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        if (RenderingHints.KEY_ANTIALIASING.equals(hintKey)) {
            return RenderingHints.VALUE_ANTIALIAS_OFF;
        } else if (RenderingHints.KEY_TEXT_ANTIALIASING.equals(hintKey)) {
            return RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
        } else if (RenderingHints.KEY_FRACTIONALMETRICS.equals(hintKey)) {
            return RenderingHints.VALUE_FRACTIONALMETRICS_ON;
        }
        return state.getHints().get(hintKey);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return (RenderingHints) state.getHints().clone();
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        if (isDisposed()) {
            return;
        }
        state.getHints().clear();
        for (Entry<?, ?> hint : hints.entrySet()) {
            setRenderingHint((Key) hint.getKey(), hint.getValue());
        }
    }

    @Override
    public Stroke getStroke() {
        return state.getStroke();
    }

    @Override
    public void setStroke(Stroke s) {
        if (isDisposed()) {
            return;
        }
        if (s == null) {
            throw new IllegalArgumentException("Cannot set a null stroke.");
        }
        emit(new SetStrokeCommand(s));
        state.setStroke(s);
        if (graphics2D != null) {
            graphics2D.setStroke(s);
        }
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        Shape hitShape = s;
        if (onStroke) {
            hitShape = getStroke().createStrokedShape(hitShape);
        }
        hitShape = state.transformShape(hitShape);
        boolean hit = hitShape.intersects(rect);
        if (graphics2D != null) {
            graphics2D.hit(rect, s, onStroke);
        }
        return hit;
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        if (isDisposed()) {
            return;
        }
        state.getHints().put(hintKey, hintValue);
        emit(new SetHintCommand(hintKey, hintValue));
    }

    @Override
    public AffineTransform getTransform() {
        return new AffineTransform(state.getTransform());
    }

    @Override
    public void setTransform(AffineTransform tx) {
        if (isDisposed() || tx == null || state.getTransform().equals(tx)) {
            return;
        }
        emit(new SetTransformCommand(tx));
        state.setTransform(tx);
        if (graphics2D != null) {
            graphics2D.setTransform(tx);
        }
    }

    @Override
    public void shear(double shx, double shy) {
        if (shx == 0.0 && shy == 0.0) {
            return;
        }
        AffineTransform txNew = getTransform();
        txNew.shear(shx, shy);
        emit(new ShearCommand(shx, shy));
        state.setTransform(txNew);
        if ( graphics2D != null) {
            graphics2D.shear(shx, shy);
        }
    }

    @Override
    public void transform(AffineTransform tx) {
        if (tx.isIdentity()) {
            return;
        }
        AffineTransform txNew = getTransform();
        txNew.concatenate(tx);
        emit(new TransformCommand(tx));
        state.setTransform(txNew);
        if (graphics2D != null) {
            graphics2D.transform(tx);
        }
    }

    @Override
    public void translate(int x, int y) {
        translate(x, (double) y);
    }

    @Override
    public void translate(double tx, double ty) {
        if (tx == 0.0 && ty == 0.0) {
            return;
        }
        AffineTransform txNew = getTransform();
        txNew.translate(tx, ty);
        emit(new TranslateCommand(tx, ty));
        state.setTransform(txNew);
        if (graphics2D != null) {
            graphics2D.translate(tx, ty);
        }
    }

    @Override
    public void rotate(double theta) {
        rotate(theta, 0.0, 0.0);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        if (theta == 0.0) {
            return;
        }
        AffineTransform txNew = getTransform();
        if (x == 0.0 && y == 0.0) {
            txNew.rotate(theta);
        } else {
            txNew.rotate(theta, x, y);
        }
        emit(new RotateCommand(theta, x, y));
        state.setTransform(txNew);
        if (x == 0.0 && y == 0.0) {
            if (graphics2D != null) {
                graphics2D.rotate(theta);
            }
        } else {
            if (graphics2D != null) {
                graphics2D.rotate(theta, x, y);
            }
        }
    }

    @Override
    public void scale(double sx, double sy) {
        if (sx == 1.0 && sy == 1.0) {
            return;
        }
        AffineTransform affineTransform = getTransform();
        affineTransform.scale(sx, sy);
        emit(new ScaleCommand(sx, sy));
        state.setTransform(affineTransform);
        if (graphics2D != null) {
            graphics2D.scale(sx, sy);
        }
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        Color colorOld = getColor();
        setColor(getBackground());
        fillRect(x, y, width, height);
        setColor(colorOld);
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        clip(new Rectangle(x, y, width, height));
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        // unable to implement
    }

    @Override
    public Graphics create() {
        if (isDisposed()) {
            return null;
        }
        VectorGraphics2D clone;
        try {
            clone = (VectorGraphics2D) this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        emit(new CreateCommand(clone));
        if (graphics2D != null) {
            clone.graphics2D = (Graphics2D) graphics2D.create();
        }
        return clone;
    }

    @Override
    public void dispose() {
        if (isDisposed()) {
            return;
        }
        emit(new DisposeCommand(this));
        disposed = true;
        if (graphics2D != null) {
            graphics2D.dispose();
        }
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), null, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return drawImage(img, x, y, width, height, null, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        if (isDisposed() || img == null) {
            return true;
        }
        int imageWidth = img.getWidth(observer);
        int imageHeight = img.getHeight(observer);
        Rectangle bounds = new Rectangle(x, y, width, height);
        if (bgcolor != null) {
            Color bgcolorOld = getColor();
            setColor(bgcolor);
            fill(bounds);
            setColor(bgcolorOld);
        }
        emit(new DrawImageCommand(img, imageWidth, imageHeight, x, y, width, height));
        if (graphics2D != null) {
            graphics2D.drawImage(img, x, y, width, height, bgcolor, observer);
        }
        return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null,
                observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2, Color bgcolor,
                             ImageObserver observer) {
        if (img == null) {
            return true;
        }
        int sx = Math.min(sx1, sx2);
        int sy = Math.min(sy1, sy2);
        int sw = Math.abs(sx2 - sx1);
        int sh = Math.abs(sy2 - sy1);
        int dx = Math.min(dx1, dx2);
        int dy = Math.min(dy1, dy2);
        int dw = Math.abs(dx2 - dx1);
        int dh = Math.abs(dy2 - dy1);
        BufferedImage bufferedImg = toBufferedImage(img);
        Image cropped = bufferedImg.getSubimage(sx, sy, sw, sh);
        return drawImage(cropped, dx, dy, dw, dh, bgcolor, observer);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        draw(new Line2D.Double(x1, y1, x2, y2));
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        draw(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    public void drawPolygon(Polygon p) {
        draw(p);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        draw(new Polygon(xPoints, yPoints, nPoints));
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        Path2D p = new Path2D.Float();
        for (int i = 0; i < nPoints; i++) {
            if (i > 0) {
                p.lineTo(xPoints[i], yPoints[i]);
            } else {
                p.moveTo(xPoints[i], yPoints[i]);
            }
        }
        draw(p);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        draw(new Rectangle(x, y, width, height));
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight) {
        draw(new RoundRectangle2D.Double(x, y, width, height,
                arcWidth, arcHeight));
    }

    @Override
    public void fillArc(int x, int y, int width, int height,
                        int startAngle, int arcAngle) {
        fill(new Arc2D.Double(x, y, width, height,
                startAngle, arcAngle, Arc2D.PIE));
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        fill(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    public void fillPolygon(Polygon p) {
        fill(p);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        fill(new Polygon(xPoints, yPoints, nPoints));
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        fill(new Rectangle(x, y, width, height));
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight) {
        fill(new RoundRectangle2D.Double(x, y, width, height,
                arcWidth, arcHeight));
    }

    @Override
    public Shape getClip() {
        return state.getClip();
    }

    @Override
    public void setClip(Shape clip) {
        if (isDisposed()) {
            return;
        }
        emit(new SetClipCommand(clip));
        state.setClip(clip);
        if (graphics2D != null) {
            graphics2D.setClip(clip);
        }
    }

    @Override
    public Rectangle getClipBounds() {
        if (getClip() == null) {
            return null;
        }
        return getClip().getBounds();
    }

    @Override
    public Color getColor() {
        return state.getColor();
    }

    @Override
    public void setColor(Color c) {
        if (isDisposed() || c == null || getColor().equals(c)) {
            return;
        }
        emit(new SetColorCommand(c));
        state.setColor(c);
        state.setPaint(c);
        if (graphics2D != null) {
            graphics2D.setColor(c);
        }
    }

    @Override
    public Font getFont() {
        return state.getFont();
    }

    @Override
    public void setFont(Font font) {
        if (isDisposed() || (font != null && getFont().equals(font))) {
            return;
        }
        emit(new SetFontCommand(font));
        state.setFont(font);
        if (graphics2D != null) {
            graphics2D.setFont(font);
        }
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics g = bi.getGraphics();
        FontMetrics fontMetrics = g.getFontMetrics(getFont());
        g.dispose();
        return fontMetrics;
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    @Override
    public void setPaintMode() {
        setComposite(AlphaComposite.SrcOver);
        if (graphics2D != null) {
            graphics2D.setPaintMode();
        }
    }

    @Override
    public void setXORMode(Color c1) {
        if (isDisposed() || c1 == null) {
            return;
        }
        emit(new SetXORModeCommand(c1));
        state.setXorMode(c1);
        if (graphics2D != null) {
            graphics2D.setXORMode(c1);
        }
    }

    public Color getXORMode() {
        return state.getXorMode();
    }

    public Iterable<Command<?>> getCommands() {
        return commands;
    }

    protected boolean isDisposed() {
        return disposed;
    }

    private void emit(Command<?> command) {
        commands.add(command);
    }

    /**
     * Returns a transformed version of an image.
     *
     * @param image Image to be transformed
     * @param xform Affine transform to be applied
     * @return Image with transformed content
     */
    private BufferedImage getTransformedImage(Image image, AffineTransform xform) {
        Integer interpolationType = (Integer) getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        if (RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.equals(interpolationType)) {
            interpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
        } else if (RenderingHints.VALUE_INTERPOLATION_BILINEAR.equals(interpolationType)) {
            interpolationType = AffineTransformOp.TYPE_BILINEAR;
        } else {
            interpolationType = AffineTransformOp.TYPE_BICUBIC;
        }
        AffineTransformOp op = new AffineTransformOp(xform, interpolationType);
        BufferedImage bufferedImage = toBufferedImage(image);
        return op.filter(bufferedImage, null);
    }

    /*private static boolean notEquals(Shape shapeA, Shape shapeB) {
        PathIterator pathAIterator = shapeA.getPathIterator(null);
        PathIterator pathBIterator = shapeB.getPathIterator(null);
        if (pathAIterator.getWindingRule() != pathBIterator.getWindingRule()) {
            return true;
        }
        double[] pathASegment = new double[6];
        double[] pathBSegment = new double[6];
        while (!pathAIterator.isDone()) {
            int pathASegmentType = pathAIterator.currentSegment(pathASegment);
            int pathBSegmentType = pathBIterator.currentSegment(pathBSegment);
            if (pathASegmentType != pathBSegmentType) {
                return true;
            }
            for (int segmentIndex = 0; segmentIndex < pathASegment.length; segmentIndex++) {
                if (pathASegment[segmentIndex] != pathBSegment[segmentIndex]) {
                    return true;
                }
            }
            pathAIterator.next();
            pathBIterator.next();
        }
        return !pathBIterator.isDone();
    }*/

    private static Shape intersectShapes(Shape s1, Shape s2) {
        if (s1 instanceof Rectangle2D && s2 instanceof Rectangle2D) {
            Rectangle2D r1 = (Rectangle2D) s1;
            Rectangle2D r2 = (Rectangle2D) s2;
            double x1 = Math.max(r1.getMinX(), r2.getMinX());
            double y1 = Math.max(r1.getMinY(), r2.getMinY());
            double x2 = Math.min(r1.getMaxX(), r2.getMaxX());
            double y2 = Math.min(r1.getMaxY(), r2.getMaxY());
            Rectangle2D intersection = new Rectangle2D.Double();
            if ((x2 < x1) || (y2 < y1)) {
                intersection.setFrameFromDiagonal(0, 0, 0, 0);
            } else {
                intersection.setFrameFromDiagonal(x1, y1, x2, y2);
            }
            return intersection;
        } else {
            Area intersection = new Area(s1);
            intersection.intersect(new Area(s2));
            return intersection;
        }
    }
}