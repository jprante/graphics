package org.xbib.graphics.io.vector;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

public class GraphicsState implements Cloneable {
    /**
     * Default background color.
     */
    public static final Color DEFAULT_BACKGROUND = Color.BLACK;
    /**
     * Default color.
     */
    public static final Color DEFAULT_COLOR = Color.WHITE;
    /**
     * Default clipping shape.
     */
    public static final Shape DEFAULT_CLIP = null;
    /**
     * Default composite mode.
     */
    public static final Composite DEFAULT_COMPOSITE = AlphaComposite.SrcOver;
    /**
     * Default font.
     */
    public static final Font DEFAULT_FONT = Font.decode(null);
    /**
     * Default paint.
     */
    public static final Color DEFAULT_PAINT = DEFAULT_COLOR;
    /**
     * Default stroke.
     */
    public static final Stroke DEFAULT_STROKE = new BasicStroke();
    /**
     * Default transformation.
     */
    public static final AffineTransform DEFAULT_TRANSFORM =
            new AffineTransform();
    /**
     * Default XOR mode.
     */
    public static final Color DEFAULT_XOR_MODE = Color.BLACK;

    /**
     * Rendering hints.
     */
    private RenderingHints hints;
    /**
     * Current background color.
     */
    private Color background;
    /**
     * Current foreground color.
     */
    private Color color;
    /**
     * Shape used for clipping paint operations.
     */
    private Shape clip;
    /**
     * Method used for compositing.
     */
    private Composite composite;
    /**
     * Current font.
     */
    private Font font;
    /**
     * Paint used to fill shapes.
     */
    private Paint paint;
    /**
     * Stroke used for drawing shapes.
     */
    private Stroke stroke;
    /**
     * Current transformation matrix.
     */
    private AffineTransform transform;
    /**
     * XOR mode used for rendering.
     */
    private Color xorMode;

    public GraphicsState() {
        hints = new RenderingHints(null);
        background = DEFAULT_BACKGROUND;
        color = DEFAULT_COLOR;
        clip = DEFAULT_CLIP;
        composite = DEFAULT_COMPOSITE;
        font = DEFAULT_FONT;
        paint = DEFAULT_PAINT;
        stroke = DEFAULT_STROKE;
        transform = new AffineTransform(DEFAULT_TRANSFORM);
        xorMode = DEFAULT_XOR_MODE;
    }

    private static Shape transformShape(Shape s, AffineTransform tx) {
        if (s == null) {
            return null;
        }
        if (tx == null || tx.isIdentity()) {
            return clone(s);
        }
        boolean isRectangle = s instanceof Rectangle2D;
        int nonRectlinearTxMask = AffineTransform.TYPE_GENERAL_TRANSFORM |
                AffineTransform.TYPE_GENERAL_ROTATION;
        boolean isRectlinearTx = (tx.getType() & nonRectlinearTxMask) == 0;
        if (isRectangle && isRectlinearTx) {
            Rectangle2D rect = (Rectangle2D) s;
            double[] corners = new double[]{
                    rect.getMinX(), rect.getMinY(),
                    rect.getMaxX(), rect.getMaxY()
            };
            tx.transform(corners, 0, corners, 0, 2);
            rect = new Rectangle2D.Double();
            rect.setFrameFromDiagonal(corners[0], corners[1], corners[2],
                    corners[3]);
            return rect;
        }
        return tx.createTransformedShape(s);
    }

    private static Shape untransformShape(Shape s, AffineTransform tx) {
        if (s == null) {
            return null;
        }
        try {
            AffineTransform inverse = tx.createInverse();
            return transformShape(s, inverse);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        GraphicsState clone = (GraphicsState) super.clone();
        clone.hints = (RenderingHints) hints.clone();
        clone.clip = clone(clip);
        clone.transform = new AffineTransform(transform);
        return clone;
    }

    public Shape transformShape(Shape shape) {
        return transformShape(shape, transform);
    }

    public Shape untransformShape(Shape shape) {
        return untransformShape(shape, transform);
    }

    public RenderingHints getHints() {
        return hints;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Shape getClip() {
        return untransformShape(clip);
    }

    public void setClip(Shape clip) {
        this.clip = transformShape(clip);
    }

    public Composite getComposite() {
        return composite;
    }

    public void setComposite(Composite composite) {
        this.composite = composite;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    public void setTransform(AffineTransform tx) {
        transform.setTransform(tx);
    }

    public Color getXorMode() {
        return xorMode;
    }

    public void setXorMode(Color xorMode) {
        this.xorMode = xorMode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GraphicsState)) {
            return false;
        }
        GraphicsState o = (GraphicsState) obj;
        return !(!hints.equals(o.hints) || !background.equals(o.background) ||
                !color.equals(o.color) || !composite.equals(o.composite) ||
                !font.equals(o.font) || !paint.equals(o.paint) ||
                !stroke.equals(o.stroke) || !transform.equals(o.transform) ||
                !xorMode.equals(o.xorMode) ||
                ((clip == null || o.clip == null) && clip != o.clip) ||
                (clip != null && !clip.equals(o.clip)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(hints, background, color, composite, font, paint,
                stroke, transform, xorMode, clip);
    }

    public boolean isDefault() {
        return hints.isEmpty() && background.equals(DEFAULT_BACKGROUND) &&
                color.equals(DEFAULT_COLOR) && composite.equals(DEFAULT_COMPOSITE) &&
                font.equals(DEFAULT_FONT) && paint.equals(DEFAULT_PAINT) &&
                stroke.equals(DEFAULT_STROKE) && transform.equals(DEFAULT_TRANSFORM) &&
                xorMode.equals(DEFAULT_XOR_MODE) && clip == DEFAULT_CLIP;
    }

    static Shape clone(Shape shape) {
        if (shape == null) {
            return null;
        }
        Shape clone;
        if (shape instanceof Line2D) {
            clone = (shape instanceof Line2D.Float) ?
                    new Line2D.Float() : new Line2D.Double();
            ((Line2D) clone).setLine((Line2D) shape);
        } else if (shape instanceof Rectangle) {
            clone = new Rectangle((Rectangle) shape);
        } else if (shape instanceof Rectangle2D) {
            clone = (shape instanceof Rectangle2D.Float) ?
                    new Rectangle2D.Float() : new Rectangle2D.Double();
            ((Rectangle2D) clone).setRect((Rectangle2D) shape);
        } else if (shape instanceof RoundRectangle2D) {
            clone = (shape instanceof RoundRectangle2D.Float) ?
                    new RoundRectangle2D.Float() : new RoundRectangle2D.Double();
            ((RoundRectangle2D) clone).setRoundRect((RoundRectangle2D) shape);
        } else if (shape instanceof Ellipse2D) {
            clone = (shape instanceof Ellipse2D.Float) ?
                    new Ellipse2D.Float() : new Ellipse2D.Double();
            ((Ellipse2D) clone).setFrame(((Ellipse2D) shape).getFrame());
        } else if (shape instanceof Arc2D) {
            clone = (shape instanceof Arc2D.Float) ?
                    new Arc2D.Float() : new Arc2D.Double();
            ((Arc2D) clone).setArc((Arc2D) shape);
        } else if (shape instanceof Polygon) {
            Polygon p = (Polygon) shape;
            clone = new Polygon(p.xpoints, p.ypoints, p.npoints);
        } else if (shape instanceof CubicCurve2D) {
            clone = (shape instanceof CubicCurve2D.Float) ?
                    new CubicCurve2D.Float() : new CubicCurve2D.Double();
            ((CubicCurve2D) clone).setCurve((CubicCurve2D) shape);
        } else if (shape instanceof QuadCurve2D) {
            clone = (shape instanceof QuadCurve2D.Float) ?
                    new QuadCurve2D.Float() : new QuadCurve2D.Double();
            ((QuadCurve2D) clone).setCurve((QuadCurve2D) shape);
        } else if (shape instanceof Path2D.Float) {
            clone = new Path2D.Float(shape);
        } else {
            clone = new Path2D.Double(shape);
        }
        return clone;
    }
}
