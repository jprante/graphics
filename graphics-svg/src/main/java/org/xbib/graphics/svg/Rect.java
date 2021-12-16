package org.xbib.graphics.svg;

import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

public class Rect extends ShapeElement {

    public static final String TAG_NAME = "rect";

    float x = 0f;
    float y = 0f;
    float width = 0f;
    float height = 0f;
    float rx = 0f;
    float ry = 0f;
    RectangularShape rect;

    public Rect() {
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
        boolean rxSet = false;
        if (getPres(sty.setName("rx"))) {
            rx = sty.getFloatValueWithUnits();
            rxSet = true;
        }
        boolean rySet = false;
        if (getPres(sty.setName("ry"))) {
            ry = sty.getFloatValueWithUnits();
            rySet = true;
        }
        if (!rxSet) {
            rx = ry;
        }
        if (!rySet) {
            ry = rx;
        }
        if (rx == 0f && ry == 0f) {
            rect = new Rectangle2D.Float(x, y, width, height);
        } else {
            rect = new RoundRectangle2D.Float(x, y, width, height, rx * 2, ry * 2);
        }
    }

    @Override
    protected void doRender(Graphics2D g) throws SVGException, IOException {
        beginLayer(g);
        renderShape(g, rect);
        finishLayer(g);
    }

    @Override
    public Shape getShape() {
        return shapeToParent(rect);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        return boundsToParent(includeStrokeInBounds(rect.getBounds2D()));
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
        if (getPres(sty.setName("rx"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != rx) {
                rx = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("ry"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != ry) {
                ry = newVal;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }
}
