package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.element.ShapeElement;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Line extends ShapeElement {

    public static final String TAG_NAME = "line";

    float x1 = 0f;

    float y1 = 0f;

    float x2 = 0f;

    float y2 = 0f;

    Line2D.Float line;

    public Line() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("x1"))) {
            x1 = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("y1"))) {
            y1 = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("x2"))) {
            x2 = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("y2"))) {
            y2 = sty.getFloatValueWithUnits();
        }
        line = new Line2D.Float(x1, y1, x2, y2);
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        beginLayer(g);
        renderShape(g, line);
        finishLayer(g);
    }

    @Override
    public Shape getShape() {
        return shapeToParent(line);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        return boundsToParent(includeStrokeInBounds(line.getBounds2D()));
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("x1"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x1) {
                x1 = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("y1"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y1) {
                y1 = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("x2"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x2) {
                x2 = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("y2"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y2) {
                y2 = newVal;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }
}
