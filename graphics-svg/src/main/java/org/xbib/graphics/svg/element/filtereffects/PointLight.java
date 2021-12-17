package org.xbib.graphics.svg.element.filtereffects;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;

public class PointLight extends Light {

    public static final String TAG_NAME = "fepointlight";

    float x = 0f;

    float y = 0f;

    float z = 0f;

    public PointLight() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("x"))) {
            x = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("y"))) {
            y = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("z"))) {
            z = sty.getFloatValueWithUnits();
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
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
        if (getPres(sty.setName("z"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != z) {
                z = newVal;
                stateChange = true;
            }
        }
        return stateChange;
    }
}
