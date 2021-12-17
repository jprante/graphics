package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Shape;
import java.awt.geom.Area;
import java.io.IOException;

public class ClipPath extends SVGElement {

    public static final String TAG_NAME = "clippath";

    public static final int CP_USER_SPACE_ON_USE = 0;

    public static final int CP_OBJECT_BOUNDING_BOX = 1;

    int clipPathUnits = CP_USER_SPACE_ON_USE;

    public ClipPath() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        clipPathUnits = (getPres(sty.setName("clipPathUnits"))
                && sty.getStringValue().equals("objectBoundingBox"))
                ? CP_OBJECT_BOUNDING_BOX
                : CP_USER_SPACE_ON_USE;
    }

    public int getClipPathUnits() {
        return clipPathUnits;
    }

    public Shape getClipPathShape() {
        if (children.isEmpty()) {
            return null;
        }
        if (children.size() == 1) {
            return ((ShapeElement) children.get(0)).getShape();
        }
        Area clipArea = null;
        for (SVGElement svgElement : children) {
            ShapeElement se = (ShapeElement) svgElement;
            if (clipArea == null) {
                Shape shape = se.getShape();
                if (shape != null) {
                    clipArea = new Area(se.getShape());
                }
                continue;
            }
            Shape shape = se.getShape();
            if (shape != null) {
                clipArea.intersect(new Area(shape));
            }
        }
        return clipArea;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("clipPathUnits"))) {
            String newUnitsStrn = sty.getStringValue();
            int newUnits = newUnitsStrn.equals("objectBoundingBox")
                    ? CP_OBJECT_BOUNDING_BOX
                    : CP_USER_SPACE_ON_USE;
            if (newUnits != clipPathUnits) {
                clipPathUnits = newUnits;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        for (SVGElement ele : children) {
            ele.updateTime(curTime);
        }
        return shapeChange;
    }
}
