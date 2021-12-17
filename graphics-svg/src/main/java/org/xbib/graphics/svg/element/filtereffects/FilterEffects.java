package org.xbib.graphics.svg.element.filtereffects;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Rectangle;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public abstract class FilterEffects extends SVGElement {

    public static final String TAG_NAME = "filtereffects";

    public static final int FP_SOURCE_GRAPHIC = 0;

    public static final int FP_SOURCE_ALPHA = 1;

    public static final int FP_BACKGROUND_IMAGE = 2;

    public static final int FP_BACKGROUND_ALPHA = 3;

    public static final int FP_FILL_PAINT = 4;

    public static final int FP_STROKE_PAINT = 5;

    public static final int FP_CUSTOM = 5;

    float x = 0f;

    float y = 0f;

    float width = 1f;

    float height = 1f;

    URL href = null;

    public FilterEffects() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
    }

    public List<FilterOp> getOperations(Rectangle bounds, float xScale, float yScale) {
        return null;
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
        return stateChange;
    }
}
