package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public abstract class TransformableElement extends SVGElement {

    protected AffineTransform xform = null;

    public TransformableElement() {
    }

    public TransformableElement(String id, SVGElement parent) {
        super(id, parent);
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("transform"))) {
            AffineTransform newXform = parseTransform(sty.getStringValue());
            if (!newXform.equals(xform)) {
                xform = newXform;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("transform"))) {
            xform = parseTransform(sty.getStringValue());
        }
    }

    protected Shape shapeToParent(Shape shape) {
        if (xform == null) {
            return shape;
        }
        return xform.createTransformedShape(shape);
    }

    protected Rectangle2D boundsToParent(Rectangle2D rect) {
        if (xform == null || rect == null) {
            return rect;
        }
        return xform.createTransformedShape(rect).getBounds2D();
    }
}
