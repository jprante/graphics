package org.xbib.graphics.svg;

import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URI;

public class Use extends ShapeElement {

    public static final String TAG_NAME = "use";

    float x = 0f;

    float y = 0f;

    float width = 1f;

    float height = 1f;

    URI href = null;

    AffineTransform refXform;

    public Use() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("x"))) {
            x = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("y"))) {
            y = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("width"))) {
            width = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("height"))) {
            height = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("xlink:href"))) {
            href = sty.getURIValue(getXMLBase());
        }
        refXform = new AffineTransform();
        refXform.translate(this.x, this.y);
    }

    @Override
    protected void doRender(Graphics2D g) throws SVGException, IOException {
        beginLayer(g);
        AffineTransform oldXform = g.getTransform();
        g.transform(refXform);
        SVGElement ref = diagram.getUniverse().getElement(href);
        if (!(ref instanceof RenderableElement)) {
            return;
        }
        RenderableElement rendEle = (RenderableElement) ref;
        rendEle.pushParentContext(this);
        rendEle.render(g);
        rendEle.popParentContext();
        g.setTransform(oldXform);
        finishLayer(g);
    }

    @Override
    public Shape getShape() {
        SVGElement ref = diagram.getUniverse().getElement(href);
        if (ref instanceof ShapeElement) {
            Shape shape = ((ShapeElement) ref).getShape();
            shape = refXform.createTransformedShape(shape);
            shape = shapeToParent(shape);
            return shape;
        }
        return null;
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        SVGElement ref = diagram.getUniverse().getElement(href);
        if (ref instanceof ShapeElement) {
            ShapeElement shapeEle = (ShapeElement) ref;
            shapeEle.pushParentContext(this);
            Rectangle2D bounds = shapeEle.getBoundingBox();
            shapeEle.popParentContext();
            bounds = refXform.createTransformedShape(bounds).getBounds2D();
            bounds = boundsToParent(bounds);
            return bounds;
        }
        return null;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("x"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x) {
                x = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("y"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y) {
                y = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("width"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != width) {
                width = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("height"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != height) {
                height = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("xlink:href"))) {
            URI src = sty.getURIValue(getXMLBase());
            if (!src.equals(href)) {
                href = src;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }
}
