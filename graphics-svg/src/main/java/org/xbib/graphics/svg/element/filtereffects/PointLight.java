package org.xbib.graphics.svg.element.filtereffects;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;

public class PointLight extends Light {

    private float x = 0f;

    private float y = 0f;

    private float z = 0f;

    @Override
    public String getTagName() {
        return "fepointlight";
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
