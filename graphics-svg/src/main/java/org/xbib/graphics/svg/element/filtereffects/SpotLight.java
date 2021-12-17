package org.xbib.graphics.svg.element.filtereffects;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;

public class SpotLight extends Light {

    public static final String TAG_NAME = "fespotlight";

    float x = 0f;

    float y = 0f;

    float z = 0f;

    float pointsAtX = 0f;

    float pointsAtY = 0f;

    float pointsAtZ = 0f;

    float specularComponent = 0f;

    float limitingConeAngle = 0f;

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
        if (getPres(sty.setName("pointsAtX"))) {
            pointsAtX = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("pointsAtY"))) {
            pointsAtY = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("pointsAtZ"))) {
            pointsAtZ = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("specularComponent"))) {
            specularComponent = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("limitingConeAngle"))) {
            limitingConeAngle = sty.getFloatValueWithUnits();
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

    public float getPointsAtX() {
        return pointsAtX;
    }

    public float getPointsAtY() {
        return pointsAtY;
    }

    public float getPointsAtZ() {
        return pointsAtZ;
    }

    public float getSpecularComponent() {
        return specularComponent;
    }

    public float getLimitingConeAngle() {
        return limitingConeAngle;
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
        if (getPres(sty.setName("pointsAtX"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != pointsAtX) {
                pointsAtX = newVal;
                stateChange = true;
            }
        }
        if (getPres(sty.setName("pointsAtY"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != pointsAtY) {
                pointsAtY = newVal;
                stateChange = true;
            }
        }
        if (getPres(sty.setName("pointsAtZ"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != pointsAtZ) {
                pointsAtZ = newVal;
                stateChange = true;
            }
        }
        if (getPres(sty.setName("specularComponent"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != specularComponent) {
                specularComponent = newVal;
                stateChange = true;
            }
        }
        if (getPres(sty.setName("limitingConeAngle"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != limitingConeAngle) {
                limitingConeAngle = newVal;
                stateChange = true;
            }
        }
        return stateChange;
    }
}
