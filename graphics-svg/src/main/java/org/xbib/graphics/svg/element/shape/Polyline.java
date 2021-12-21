package org.xbib.graphics.svg.element.shape;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.element.ShapeElement;
import org.xbib.graphics.svg.xml.StyleAttribute;
import org.xbib.graphics.svg.xml.XMLParseUtil;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Polyline extends ShapeElement {

    private int fillRule = GeneralPath.WIND_NON_ZERO;

    private String pointsStrn = "";

    private GeneralPath path;

    @Override
    public String getTagName() {
        return "polyline";
    }

    @Override
    public void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("points"))) {
            pointsStrn = sty.getStringValue();
        }
        String fillRuleStrn = getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
        fillRule = fillRuleStrn.equals("evenodd") ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO;
        buildPath();
    }

    protected void buildPath() {
        float[] points = XMLParseUtil.parseFloatList(pointsStrn);
        path = new GeneralPath(fillRule, points.length / 2);
        path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
            path.lineTo(points[i], points[i + 1]);
        }
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
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("points"))) {
            String newVal = sty.getStringValue();
            if (!newVal.equals(pointsStrn)) {
                pointsStrn = newVal;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }
}
