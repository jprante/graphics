package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.ShapeElement;
import org.xbib.graphics.svg.xml.StyleAttribute;
import org.xbib.graphics.svg.xml.XMLParseUtil;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Polygon extends ShapeElement {

    public static final String TAG_NAME = "polygon";

    int fillRule = GeneralPath.WIND_NON_ZERO;
    String pointsStrn = "";
    GeneralPath path;

    /**
     * Creates a new instance of Rect
     */
    public Polygon() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
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
        path.closePath();
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

    /**
     * Updates all attributes in this diagram associated with a time event. Ie,
     * all attributes with track information.
     *
     * @return - true if this node has changed state as a result of the time
     * update
     */
    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
//        if (trackManager.getNumTracks() == 0) return false;
        boolean changeState = super.updateTime(curTime);

        //Get current values for parameters
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
//            buildPath();
//            return true;
        }

        return changeState || shapeChange;
    }
}
