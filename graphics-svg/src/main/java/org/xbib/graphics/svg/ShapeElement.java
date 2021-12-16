package org.xbib.graphics.svg;

import org.xbib.graphics.svg.Marker.MarkerLayout;
import org.xbib.graphics.svg.Marker.MarkerPos;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URI;
import java.util.List;

abstract public class ShapeElement extends RenderableElement {

    protected float strokeWidthScalar = 1f;

    public ShapeElement() {
    }

    @Override
    abstract protected void doRender(Graphics2D g) throws SVGException, IOException;

    @Override
    protected void doPick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        if ((boundingBox ? getBoundingBox() : getShape()).contains(point)) {
            retVec.add(getPath(null));
        }
    }

    @Override
    protected void doPick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        if (ltw.createTransformedShape((boundingBox ? getBoundingBox() : getShape())).intersects(pickArea)) {
            retVec.add(getPath(null));
        }
    }

    private Paint handleCurrentColor(StyleAttribute styleAttrib) throws SVGException {
        if (styleAttrib.getStringValue().equals("currentColor")) {
            StyleAttribute currentColorAttrib = new StyleAttribute();
            if (getStyle(currentColorAttrib.setName("color"))) {
                if (!currentColorAttrib.getStringValue().equals("none")) {
                    return currentColorAttrib.getColorValue();
                }
            }
            return null;
        } else {
            return styleAttrib.getColorValue();
        }
    }

    protected void renderShape(Graphics2D g, Shape shape) throws SVGException, IOException {
        StyleAttribute styleAttrib = new StyleAttribute();
        if (getStyle(styleAttrib.setName("visibility"))) {
            if (!styleAttrib.getStringValue().equals("visible")) {
                return;
            }
        }
        if (getStyle(styleAttrib.setName("display"))) {
            if (styleAttrib.getStringValue().equals("none")) {
                return;
            }
        }
        Paint fillPaint = Color.black;
        if (getStyle(styleAttrib.setName("fill"))) {
            if (styleAttrib.getStringValue().equals("none")) {
                fillPaint = null;
            } else {
                fillPaint = handleCurrentColor(styleAttrib);
                if (fillPaint == null) {
                    URI uri = styleAttrib.getURIValue(getXMLBase());
                    if (uri != null) {
                        Rectangle2D bounds = shape.getBounds2D();
                        AffineTransform xform = g.getTransform();
                        SVGElement ele = diagram.getUniverse().getElement(uri);
                        if (ele != null) {
                            try {
                                fillPaint = ((FillElement) ele).getPaint(bounds, xform);
                            } catch (IllegalArgumentException e) {
                                throw new SVGException(e);
                            }
                        }
                    }
                }
            }
        }
        float opacity = 1f;
        if (getStyle(styleAttrib.setName("opacity"))) {
            opacity = styleAttrib.getRatioValue();
        }
        float fillOpacity = opacity;
        if (getStyle(styleAttrib.setName("fill-opacity"))) {
            fillOpacity *= styleAttrib.getRatioValue();
        }
        Paint strokePaint = null;
        if (getStyle(styleAttrib.setName("stroke"))) {
            if (!styleAttrib.getStringValue().equals("none")) {
                strokePaint = handleCurrentColor(styleAttrib);
                if (strokePaint == null) {
                    URI uri = styleAttrib.getURIValue(getXMLBase());
                    if (uri != null) {
                        Rectangle2D bounds = shape.getBounds2D();
                        AffineTransform xform = g.getTransform();
                        SVGElement ele = diagram.getUniverse().getElement(uri);
                        if (ele != null) {
                            strokePaint = ((FillElement) ele).getPaint(bounds, xform);
                        }
                    }
                }
            }
        }
        float[] strokeDashArray = null;
        if (getStyle(styleAttrib.setName("stroke-dasharray"))) {
            strokeDashArray = styleAttrib.getFloatList();
            if (strokeDashArray.length == 0) strokeDashArray = null;
        }
        float strokeDashOffset = 0f;
        if (getStyle(styleAttrib.setName("stroke-dashoffset"))) {
            strokeDashOffset = styleAttrib.getFloatValueWithUnits();
        }
        int strokeLinecap = BasicStroke.CAP_BUTT;
        if (getStyle(styleAttrib.setName("stroke-linecap"))) {
            String val = styleAttrib.getStringValue();
            if (val.equals("round")) {
                strokeLinecap = BasicStroke.CAP_ROUND;
            } else if (val.equals("square")) {
                strokeLinecap = BasicStroke.CAP_SQUARE;
            }
        }
        int strokeLinejoin = BasicStroke.JOIN_MITER;
        if (getStyle(styleAttrib.setName("stroke-linejoin"))) {
            String val = styleAttrib.getStringValue();
            if (val.equals("round")) {
                strokeLinejoin = BasicStroke.JOIN_ROUND;
            } else if (val.equals("bevel")) {
                strokeLinejoin = BasicStroke.JOIN_BEVEL;
            }
        }
        float strokeMiterLimit = 4f;
        if (getStyle(styleAttrib.setName("stroke-miterlimit"))) {
            strokeMiterLimit = Math.max(styleAttrib.getFloatValueWithUnits(), 1);
        }
        float strokeOpacity = opacity;
        if (getStyle(styleAttrib.setName("stroke-opacity"))) {
            strokeOpacity *= styleAttrib.getRatioValue();
        }
        float strokeWidth = 1f;
        if (getStyle(styleAttrib.setName("stroke-width"))) {
            strokeWidth = styleAttrib.getFloatValueWithUnits();
        }
        strokeWidth *= strokeWidthScalar;
        Marker markerStart = null;
        if (getStyle(styleAttrib.setName("marker-start"))) {
            if (!styleAttrib.getStringValue().equals("none")) {
                URI uri = styleAttrib.getURIValue(getXMLBase());
                markerStart = (Marker) diagram.getUniverse().getElement(uri);
            }
        }
        Marker markerMid = null;
        if (getStyle(styleAttrib.setName("marker-mid"))) {
            if (!styleAttrib.getStringValue().equals("none")) {
                URI uri = styleAttrib.getURIValue(getXMLBase());
                markerMid = (Marker) diagram.getUniverse().getElement(uri);
            }
        }
        Marker markerEnd = null;
        if (getStyle(styleAttrib.setName("marker-end"))) {
            if (!styleAttrib.getStringValue().equals("none")) {
                URI uri = styleAttrib.getURIValue(getXMLBase());
                markerEnd = (Marker) diagram.getUniverse().getElement(uri);
            }
        }
        if (fillPaint != null && fillOpacity != 0f) {
            if (!(fillOpacity <= 0)) {
                if (fillOpacity < 1f) {
                    Composite cachedComposite = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fillOpacity));
                    g.setPaint(fillPaint);
                    g.fill(shape);
                    g.setComposite(cachedComposite);
                } else {
                    g.setPaint(fillPaint);
                    g.fill(shape);
                }
            }
        }
        if (strokePaint != null && strokeOpacity != 0f) {
            BasicStroke stroke;
            if (strokeDashArray == null) {
                stroke = new BasicStroke(strokeWidth, strokeLinecap, strokeLinejoin, strokeMiterLimit);
            } else {
                stroke = new BasicStroke(strokeWidth, strokeLinecap, strokeLinejoin, strokeMiterLimit, strokeDashArray, strokeDashOffset);
            }
            Shape strokeShape;
            AffineTransform cacheXform = g.getTransform();
            if (vectorEffect == VECTOR_EFFECT_NON_SCALING_STROKE) {
                strokeShape = cacheXform.createTransformedShape(shape);
                strokeShape = stroke.createStrokedShape(strokeShape);
            } else {
                strokeShape = stroke.createStrokedShape(shape);
            }
            if (!(strokeOpacity <= 0)) {
                Composite cachedComposite = g.getComposite();
                if (strokeOpacity < 1f) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, strokeOpacity));
                }
                if (vectorEffect == VECTOR_EFFECT_NON_SCALING_STROKE) {
                    g.setTransform(new AffineTransform());
                }
                g.setPaint(strokePaint);
                g.fill(strokeShape);
                if (vectorEffect == VECTOR_EFFECT_NON_SCALING_STROKE) {
                    g.setTransform(cacheXform);
                }
                if (strokeOpacity < 1f) {
                    g.setComposite(cachedComposite);
                }
            }
        }
        if (markerStart != null || markerMid != null || markerEnd != null) {
            MarkerLayout layout = new MarkerLayout();
            layout.layout(shape);
            List<MarkerPos> list = layout.getMarkerList();
            for (MarkerPos pos : list) {
                switch (pos.type) {
                    case Marker.MARKER_START:
                        if (markerStart != null) {
                            markerStart.render(g, pos, strokeWidth);
                        }
                        break;
                    case Marker.MARKER_MID:
                        if (markerMid != null) {
                            markerMid.render(g, pos, strokeWidth);
                        }
                        break;
                    case Marker.MARKER_END:
                        if (markerEnd != null) {
                            markerEnd.render(g, pos, strokeWidth);
                        }
                        break;
                }
            }
        }
    }

    abstract public Shape getShape();

    protected Rectangle2D includeStrokeInBounds(Rectangle2D rect) throws SVGException {
        StyleAttribute styleAttrib = new StyleAttribute();
        if (!getStyle(styleAttrib.setName("stroke"))) {
            return rect;
        }
        double strokeWidth = 1;
        if (getStyle(styleAttrib.setName("stroke-width"))) {
            strokeWidth = styleAttrib.getDoubleValue();
        }
        rect.setRect(rect.getX() - strokeWidth / 2,
                rect.getY() - strokeWidth / 2,
                rect.getWidth() + strokeWidth,
                rect.getHeight() + strokeWidth);
        return rect;
    }
}
