package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.element.filtereffects.FilterEffects;
import org.xbib.graphics.svg.SVGElementException;
import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.SVGLoaderHelper;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Filter extends SVGElement {

    private static final int FU_OBJECT_BOUNDING_BOX = 0;

    private static final int FU_USER_SPACE_ON_USE = 1;

    private static final int PU_OBJECT_BOUNDING_BOX = 0;

    private static final int PU_USER_SPACE_ON_USE = 1;

    private int filterUnits = FU_OBJECT_BOUNDING_BOX;

    private float x = 0f;

    private float y = 0f;

    private float width = 1f;

    private float height = 1f;

    private URL href = null;

    private final List<FilterEffects> filterEffects = new ArrayList<>();

    @Override
    public String getTagName() {
        return "filter";
    }

    public List<FilterEffects> getFilterEffects() {
        return filterEffects;
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

    @Override
    public boolean updateTime(double curTime) throws SVGException {
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
                stateChange = true;
            }
        }
        return stateChange;
    }
}
