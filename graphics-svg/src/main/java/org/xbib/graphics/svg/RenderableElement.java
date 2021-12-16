package org.xbib.graphics.svg;

import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URI;
import java.util.List;

abstract public class RenderableElement extends TransformableElement {

    AffineTransform cachedXform = null;

    Mask cachedMask;

    Filter filter;

    Shape cachedClip = null;

    public static final int VECTOR_EFFECT_NONE = 0;

    public static final int VECTOR_EFFECT_NON_SCALING_STROKE = 1;

    int vectorEffect;

    private BufferPainter.Cache bufferCache;

    public RenderableElement() {
    }

    public RenderableElement(String id, SVGElement parent) {
        super(id, parent);
    }

    BufferPainter.Cache getBufferCache() {
        return bufferCache;
    }

    void setBufferImage(BufferPainter.Cache bufferCache) {
        this.bufferCache = bufferCache;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("vector-effect"))) {
            if ("non-scaling-stroke".equals(sty.getStringValue())) {
                vectorEffect = VECTOR_EFFECT_NON_SCALING_STROKE;
            } else {
                vectorEffect = VECTOR_EFFECT_NONE;
            }
        } else {
            vectorEffect = VECTOR_EFFECT_NONE;
        }
        cachedMask = getMask(sty);
        filter = getFilter(sty);
    }

    public void render(Graphics2D g) throws SVGException, IOException {
        BufferPainter.paintElement(g, this);
    }

    private Mask getMask(StyleAttribute styleAttrib) throws SVGException {
        if (getStyle(styleAttrib.setName("mask"), false)
                && !"none".equals(styleAttrib.getStringValue())) {
            URI uri = styleAttrib.getURIValue(getXMLBase());
            if (uri == null) {
                return null;
            }
            return (Mask) diagram.getUniverse().getElement(uri);
        }
        return null;
    }

    private Filter getFilter(StyleAttribute styleAttrib) throws SVGException {
        if (getStyle(styleAttrib.setName("filter"), false)
                && !"none".equals(styleAttrib.getStringValue())) {
            URI uri = styleAttrib.getURIValue(getXMLBase());
            if (uri == null) {
                return null;
            }
            return (Filter) diagram.getUniverse().getElement(uri);
        }
        return null;
    }

    abstract protected void doRender(Graphics2D g) throws SVGException, IOException;

    void pick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        if (cachedMask != null) {
            cachedMask.pickElement(point, boundingBox, retVec, this);
        } else {
            doPick(point, boundingBox, retVec);
        }
    }

    protected abstract void doPick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException;

    void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        if (cachedMask != null) {
            cachedMask.pickElement(pickArea, ltw, boundingBox, retVec, this);
        } else {
            doPick(pickArea, ltw, boundingBox, retVec);
        }
    }

    protected abstract void doPick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException;

    abstract public Rectangle2D getBoundingBox() throws SVGException;

    protected void beginLayer(Graphics2D g) throws SVGException {
        if (xform != null) {
            cachedXform = g.getTransform();
            g.transform(xform);
        }
        StyleAttribute styleAttrib = new StyleAttribute();
        Shape clipPath = null;
        int clipPathUnits = ClipPath.CP_USER_SPACE_ON_USE;
        if (getStyle(styleAttrib.setName("clip-path"), false)
                && !"none".equals(styleAttrib.getStringValue())) {
            URI uri = styleAttrib.getURIValue(getXMLBase());
            if (uri != null) {
                ClipPath ele = (ClipPath) diagram.getUniverse().getElement(uri);
                clipPath = ele.getClipPathShape();
                clipPathUnits = ele.getClipPathUnits();
            }
        }
        if (clipPath != null) {
            if (clipPathUnits == ClipPath.CP_OBJECT_BOUNDING_BOX && (this instanceof ShapeElement)) {
                Rectangle2D rect = this.getBoundingBox();
                AffineTransform at = new AffineTransform();
                at.scale(rect.getWidth(), rect.getHeight());
                clipPath = at.createTransformedShape(clipPath);
            }
            cachedClip = g.getClip();
            if (cachedClip == null) {
                g.setClip(clipPath);
            } else {
                Area newClip = new Area(cachedClip);
                newClip.intersect(new Area(clipPath));
                g.setClip(newClip);
            }
        }
    }

    protected void finishLayer(Graphics2D g) {
        if (cachedClip != null) {
            g.setClip(cachedClip);
        }
        if (cachedXform != null) {
            g.setTransform(cachedXform);
        }
    }
}
