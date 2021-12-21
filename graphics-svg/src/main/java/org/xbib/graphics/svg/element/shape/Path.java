package org.xbib.graphics.svg.element.shape;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.element.ShapeElement;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Path extends ShapeElement {

    private int fillRule = GeneralPath.WIND_NON_ZERO;

    private String d = "";

    private GeneralPath path;

    @Override
    public String getTagName() {
        return "path";
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        String fillRuleStrn = (getStyle(sty.setName("fill-rule"))) ? sty.getStringValue() : "nonzero";
        fillRule = fillRuleStrn.equals("evenodd") ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO;
        if (getPres(sty.setName("d"))) {
            d = sty.getStringValue();
        }
        path = buildPath(d, fillRule);
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        beginLayer(g);
        renderShape(g, path);
        finishLayer(g);
    }

    @Override
    public Shape getShape() {
        return shapeToParent(path);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        return boundsToParent(includeStrokeInBounds(path.getBounds2D()));
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getStyle(sty.setName("fill-rule"))) {
            int newVal = sty.getStringValue().equals("evenodd")
                    ? GeneralPath.WIND_EVEN_ODD
                    : GeneralPath.WIND_NON_ZERO;
            if (newVal != fillRule) {
                fillRule = newVal;
                changeState = true;
            }
        }
        if (getPres(sty.setName("d"))) {
            String newVal = sty.getStringValue();
            if (!newVal.equals(d)) {
                d = newVal;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }
}
