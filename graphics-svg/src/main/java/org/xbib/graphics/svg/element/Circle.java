package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Circle extends ShapeElement {

    public static final String TAG_NAME = "circle";

    float cx = 0f;

    float cy = 0f;

    float r = 0f;

    Ellipse2D.Float circle = new Ellipse2D.Float();

    public Circle() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("cx"))) {
            cx = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("cy"))) {
            cy = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("r"))) {
            r = sty.getFloatValueWithUnits();
        }
        circle.setFrame(cx - r, cy - r, r * 2f, r * 2f);
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        beginLayer(g);
        renderShape(g, circle);
        finishLayer(g);
    }

    @Override
    public Shape getShape() {
        return shapeToParent(circle);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        return boundsToParent(includeStrokeInBounds(circle.getBounds2D()));
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("cx"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != cx) {
                cx = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("cy"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != cy) {
                cy = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("r"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != r) {
                r = newVal;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }
}