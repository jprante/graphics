package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Ellipse extends ShapeElement {

    public static final String TAG_NAME = "ellipse";

    float cx = 0.0f;

    float cy = 0.0f;

    float rx = 0.0f;

    float ry = 0.0f;

    Ellipse2D.Float ellipse = new Ellipse2D.Float();

    public Ellipse() {
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
        if (getPres(sty.setName("rx"))) {
            rx = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("ry"))) {
            ry = sty.getFloatValueWithUnits();
        }
        ellipse.setFrame(cx - rx, cy - ry, rx * 2f, ry * 2f);
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        beginLayer(g);
        renderShape(g, ellipse);
        finishLayer(g);
    }

    @Override
    public Shape getShape() {
        return shapeToParent(ellipse);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        return boundsToParent(includeStrokeInBounds(ellipse.getBounds2D()));
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("cx"))) {
            float newCx = sty.getFloatValueWithUnits();
            if (newCx != cx) {
                cx = newCx;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("cy"))) {
            float newCy = sty.getFloatValueWithUnits();
            if (newCy != cy) {
                cy = newCy;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("rx"))) {
            float newRx = sty.getFloatValueWithUnits();
            if (newRx != rx) {
                rx = newRx;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("ry"))) {
            float newRy = sty.getFloatValueWithUnits();
            if (newRy != ry) {
                ry = newRy;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }
}
