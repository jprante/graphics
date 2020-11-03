package org.xbib.graphics.chart.io.vector;

import org.xbib.graphics.chart.io.vector.intermediate.commands.Command;
import org.xbib.graphics.chart.io.vector.intermediate.commands.CreateCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.DisposeCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.DrawImageCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.DrawShapeCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.DrawStringCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.FillShapeCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.RotateCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.ScaleCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetBackgroundCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetClipCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetColorCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetCompositeCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetFontCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetHintCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetPaintCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetStrokeCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetTransformCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.SetXORModeCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.ShearCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.TransformCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.TranslateCommand;
import org.xbib.graphics.chart.io.vector.util.GraphicsUtils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
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
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Base for classes that want to implement vector export.
 */
public class VectorGraphics2D extends Graphics2D implements Cloneable {
    /**
     * List of operations that were performed on this graphics object and its
     * derived objects.
     */
    private final List<Command<?>> commands;
    /**
     * Device configuration settings.
     */
    //private final GraphicsConfiguration deviceConfig;
    /**
     * Context settings used to render fonts.
     */
    private final FontRenderContext fontRenderContext;
    /**
     * Flag that tells whether this graphics object has been disposed.
     */
    private boolean disposed;

    private GraphicsState state;

    private Graphics2D _debug_validate_graphics;

    public VectorGraphics2D() {
        commands = new LinkedList<Command<?>>();
        emit(new CreateCommand(this));
        fontRenderContext = new FontRenderContext(null, false, true);

        state = new GraphicsState();

        BufferedImage _debug_validate_bimg = new BufferedImage(200, 250, BufferedImage.TYPE_INT_ARGB);
        _debug_validate_graphics = (Graphics2D) _debug_validate_bimg.getGraphics();
        _debug_validate_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            VectorGraphics2D clone = (VectorGraphics2D) super.clone();
            clone.state = (GraphicsState) state.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
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
        _debug_validate_graphics.clip(s);
        Shape clipOld = getClip();

        Shape clip = getClip();
        if ((clip != null) && (s != null)) {
            s = intersectShapes(clip, s);
        }
        setClip(s);

        Shape clipNew = getClip();
        if ((clipNew == null || _debug_validate_graphics.getClip() == null) && clipNew != _debug_validate_graphics.getClip()) {
            System.err.println("clip() validation failed: clip(" + clipOld + ", " + s + ") => " + clipNew + " != " + _debug_validate_graphics.getClip());
        }
        if (clipNew != null && !GraphicsUtils.equals(clipNew, _debug_validate_graphics.getClip())) {
            System.err.println("clip() validation failed: clip(" + clipOld + ", " + s + ") => " + clipNew + " != " + _debug_validate_graphics.getClip());
        }
    }

    @Override
    public void draw(Shape s) {
        if (isDisposed() || s == null) {
            return;
        }
        emit(new DrawShapeCommand(s));

        _debug_validate_graphics.draw(s);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape s = g.getOutline(x, y);
        draw(s);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        BufferedImage bimg = getTransformedImage(img, xform);
        return drawImage(bimg, bimg.getMinX(), bimg.getMinY(),
                bimg.getWidth(), bimg.getHeight(), null, null);
    }

    /**
     * Returns a transformed version of an image.
     *
     * @param image Image to be transformed
     * @param xform Affine transform to be applied
     * @return Image with transformed content
     */
    private BufferedImage getTransformedImage(Image image,
                                              AffineTransform xform) {
        Integer interpolationType =
                (Integer) getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        if (RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                .equals(interpolationType)) {
            interpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
        } else if (RenderingHints.VALUE_INTERPOLATION_BILINEAR
                .equals(interpolationType)) {
            interpolationType = AffineTransformOp.TYPE_BILINEAR;
        } else {
            interpolationType = AffineTransformOp.TYPE_BICUBIC;
        }
        AffineTransformOp op = new AffineTransformOp(xform, interpolationType);
        BufferedImage bufferedImage = GraphicsUtils.toBufferedImage(image);
        return op.filter(bufferedImage, null);
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
        BufferedImage bimg = GraphicsUtils.toBufferedImage(img);
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
        boolean isTextAsVectors = false;
        if (isTextAsVectors) {
            TextLayout layout = new TextLayout(str, getFont(),
                    getFontRenderContext());
            Shape s = layout.getOutline(
                    AffineTransform.getTranslateInstance(x, y));
            fill(s);
        } else {
            emit(new DrawStringCommand(str, x, y));

            _debug_validate_graphics.drawString(str, x, y);
        }

    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x,
                           float y) {
        // TODO Draw styled text
        StringBuilder buf = new StringBuilder();
        for (char c = iterator.first(); c != AttributedCharacterIterator.DONE;
             c = iterator.next()) {
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

        _debug_validate_graphics.fill(s);
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

        _debug_validate_graphics.setBackground(color);
        if (!getBackground().equals(_debug_validate_graphics.getBackground())) {
            System.err.println("setBackground() validation failed");
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
            throw new IllegalArgumentException("Cannot set a null composite.");
        }
        emit(new SetCompositeCommand(comp));
        state.setComposite(comp);

        _debug_validate_graphics.setComposite(comp);
        if (!getComposite().equals(_debug_validate_graphics.getComposite())) {
            System.err.println("setComposite() validation failed");
        }
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        throw new UnsupportedOperationException();
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

        _debug_validate_graphics.setPaint(paint);
        if (!getPaint().equals(_debug_validate_graphics.getPaint())) {
            System.err.println("setPaint() validation failed");
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

        _debug_validate_graphics.setStroke(s);
        if (!getStroke().equals(_debug_validate_graphics.getStroke())) {
            System.err.println("setStroke() validation failed");
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

        boolean _debug_hit = _debug_validate_graphics.hit(rect, s, onStroke);
        if (hit != _debug_hit) {
            System.err.println("setClip() validation failed");
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

        _debug_validate_graphics.setTransform(tx);
        if (!getTransform().equals(_debug_validate_graphics.getTransform())) {
            System.err.println("setTransform() validation failed");
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

        _debug_validate_graphics.shear(shx, shy);
        if (!getTransform().equals(_debug_validate_graphics.getTransform())) {
            System.err.println("shear() validation failed");
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

        _debug_validate_graphics.transform(tx);
        if (!getTransform().equals(_debug_validate_graphics.getTransform())) {
            System.err.println("transform() validation failed");
        }
    }

    @Override
    public void translate(int x, int y) {
        translate((double) x, (double) y);
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

        _debug_validate_graphics.translate(tx, ty);
        if (!getTransform().equals(_debug_validate_graphics.getTransform())) {
            System.err.println("translate() validation failed");
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
            _debug_validate_graphics.rotate(theta);
            if (!getTransform().equals(_debug_validate_graphics.getTransform())) {
                System.err.println("rotate(theta) validation failed");
            }
        } else {
            _debug_validate_graphics.rotate(theta, x, y);
            if (!getTransform().equals(_debug_validate_graphics.getTransform())) {
                System.err.println("rotate(theta,x,y) validation failed");
            }
        }
    }

    @Override
    public void scale(double sx, double sy) {
        if (sx == 1.0 && sy == 1.0) {
            return;
        }
        AffineTransform txNew = getTransform();
        txNew.scale(sx, sy);
        emit(new ScaleCommand(sx, sy));
        state.setTransform(txNew);

        _debug_validate_graphics.scale(sx, sy);
        if (!getTransform().equals(_debug_validate_graphics.getTransform())) {
            System.err.println("scale() validation failed");
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
        // TODO Implement
        //throw new UnsupportedOperationException("copyArea() isn't supported by VectorGraphics2D.");
    }

    @Override
    public Graphics create() {
        if (isDisposed()) {
            return null;
        }
        VectorGraphics2D clone = null;
        try {
            clone = (VectorGraphics2D) this.clone();
            emit(new CreateCommand(clone));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (clone != null) {
            clone._debug_validate_graphics = (Graphics2D) _debug_validate_graphics.create();
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

        _debug_validate_graphics.dispose();
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle,
                        int arcAngle) {
        draw(new Arc2D.Double(x, y, width, height,
                startAngle, arcAngle, Arc2D.OPEN));
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return drawImage(img, x, y, img.getWidth(observer),
                img.getHeight(observer), null, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor,
                             ImageObserver observer) {
        return drawImage(img, x, y, img.getWidth(observer),
                img.getHeight(observer), bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
                             ImageObserver observer) {
        return drawImage(img, x, y, width, height, null, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
                             Color bgcolor, ImageObserver observer) {
        if (isDisposed() || img == null) {
            return true;
        }

        int imageWidth = img.getWidth(observer);
        int imageHeight = img.getHeight(observer);
        Rectangle bounds = new Rectangle(x, y, width, height);

        if (bgcolor != null) {
            // Fill rectangle with bgcolor
            Color bgcolorOld = getColor();
            setColor(bgcolor);
            fill(bounds);
            setColor(bgcolorOld);
        }

        emit(new DrawImageCommand(img, imageWidth, imageHeight, x, y, width, height));

        _debug_validate_graphics.drawImage(img, x, y, width, height, bgcolor, observer);

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

        // Draw image on rectangle
        BufferedImage bufferedImg = GraphicsUtils.toBufferedImage(img);
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

        _debug_validate_graphics.setClip(clip);
        if (getClip() == null) {
            if (_debug_validate_graphics.getClip() != null) {
                System.err.printf("setClip() validation failed: clip=null, validation=%s\n", _debug_validate_graphics.getClip());
            }
        } else if (!GraphicsUtils.equals(getClip(), _debug_validate_graphics.getClip())) {
            System.err.printf("setClip() validation failed: clip=%s, validation=%s\n", getClip(), _debug_validate_graphics.getClip());
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

        _debug_validate_graphics.setColor(c);
        if (!getColor().equals(_debug_validate_graphics.getColor())) {
            System.err.println("setColor() validation failed");
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

        _debug_validate_graphics.setFont(font);
        if (!getFont().equals(_debug_validate_graphics.getFont())) {
            System.err.println("setFont() validation failed");
        }
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        BufferedImage bi =
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
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

        _debug_validate_graphics.setPaintMode();
    }

    public Color getXORMode() {
        return state.getXorMode();
    }

    @Override
    public void setXORMode(Color c1) {
        if (isDisposed() || c1 == null) {
            return;
        }
        emit(new SetXORModeCommand(c1));
        state.setXorMode(c1);

        _debug_validate_graphics.setXORMode(c1);
    }

    private void emit(Command<?> command) {
        commands.add(command);
    }

    protected Iterable<Command<?>> getCommands() {
        return commands;
    }

    protected boolean isDisposed() {
        return disposed;
    }
}

