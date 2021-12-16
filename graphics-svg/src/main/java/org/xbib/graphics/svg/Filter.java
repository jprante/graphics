package org.xbib.graphics.svg;

import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class Filter extends SVGElement {

    public static final String TAG_NAME = "filter";
    public static final int FU_OBJECT_BOUNDING_BOX = 0;
    public static final int FU_USER_SPACE_ON_USE = 1;
    protected int filterUnits = FU_OBJECT_BOUNDING_BOX;
    public static final int PU_OBJECT_BOUNDING_BOX = 0;
    public static final int PU_USER_SPACE_ON_USE = 1;
    protected int primitiveUnits = PU_OBJECT_BOUNDING_BOX;
    float x = 0f;
    float y = 0f;
    float width = 1f;
    float height = 1f;
    Point2D filterRes = new Point2D.Double();
    URL href = null;
    final ArrayList<FilterEffects> filterEffects = new ArrayList<>();

    /**
     * Creates a new instance of FillElement
     */
    public Filter() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    /**
     * Called after the start element but before the end element to indicate
     * each child tag that has been processed
     */
    @Override
    public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
        super.loaderAddChild(helper, child);

        if (child instanceof FilterEffects) {
            filterEffects.add((FilterEffects) child);
        }
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();

        StyleAttribute sty = new StyleAttribute();
        String strn;

        if (getPres(sty.setName("filterUnits"))) {
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("userspaceonuse")) {
                filterUnits = FU_USER_SPACE_ON_USE;
            } else {
                filterUnits = FU_OBJECT_BOUNDING_BOX;
            }
        }

        if (getPres(sty.setName("primitiveUnits"))) {
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("userspaceonuse")) {
                primitiveUnits = PU_USER_SPACE_ON_USE;
            } else {
                primitiveUnits = PU_OBJECT_BOUNDING_BOX;
            }
        }

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

        try {
            if (getPres(sty.setName("xlink:href"))) {
                URI src = sty.getURIValue(getXMLBase());
                href = src.toURL();
            }
        } catch (Exception e) {
            throw new SVGException(e);
        }

    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
//        if (trackManager.getNumTracks() == 0) return false;

        //Get current values for parameters
        StyleAttribute sty = new StyleAttribute();
        boolean stateChange = false;

        if (getPres(sty.setName("x"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x) {
                x = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("y"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y) {
                y = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("width"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != width) {
                width = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("height"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != height) {
                height = newVal;
                stateChange = true;
            }
        }

        try {
            if (getPres(sty.setName("xlink:href"))) {
                URI src = sty.getURIValue(getXMLBase());
                URL newVal = src.toURL();

                if (!newVal.equals(href)) {
                    href = newVal;
                    stateChange = true;
                }
            }
        } catch (Exception e) {
            throw new SVGException(e);
        }

        if (getPres(sty.setName("filterUnits"))) {
            int newVal;
            String strn = sty.getStringValue().toLowerCase();
            if (strn.equals("userspaceonuse")) {
                newVal = FU_USER_SPACE_ON_USE;
            } else {
                newVal = FU_OBJECT_BOUNDING_BOX;
            }
            if (newVal != filterUnits) {
                filterUnits = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("primitiveUnits"))) {
            int newVal;
            String strn = sty.getStringValue().toLowerCase();
            if (strn.equals("userspaceonuse")) {
                newVal = PU_USER_SPACE_ON_USE;
            } else {
                newVal = PU_OBJECT_BOUNDING_BOX;
            }
            if (newVal != filterUnits) {
                primitiveUnits = newVal;
                stateChange = true;
            }
        }


        return stateChange;
    }
}
