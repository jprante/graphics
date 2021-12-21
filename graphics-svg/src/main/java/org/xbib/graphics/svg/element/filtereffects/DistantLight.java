package org.xbib.graphics.svg.element.filtereffects;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;

public class DistantLight extends Light {

    private float azimuth = 0f;

    private float elevation = 0f;

    @Override
    public String getTagName() {
        return "fedistantlight";
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("azimuth"))) {
            azimuth = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("elevation"))) {
            elevation = sty.getFloatValueWithUnits();
        }
    }

    public float getAzimuth() {
        return azimuth;
    }

    public float getElevation() {
        return elevation;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        StyleAttribute sty = new StyleAttribute();
        boolean stateChange = false;
        if (getPres(sty.setName("azimuth"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != azimuth) {
                azimuth = newVal;
                stateChange = true;
            }
        }
        if (getPres(sty.setName("elevation"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != elevation) {
                elevation = newVal;
                stateChange = true;
            }
        }
        return stateChange;
    }
}
