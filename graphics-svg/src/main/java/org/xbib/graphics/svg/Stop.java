package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Color;
import java.io.IOException;

public class Stop extends SVGElement {

    public static final String TAG_NAME = "stop";

    public float offset = 0f;

    public float opacity = 1f;

    public Color color = Color.black;

    public Stop() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("offset"))) {
            offset = sty.getFloatValue();
            String units = sty.getUnits();
            if (units != null && units.equals("%")) {
                offset /= 100f;
            }
            if (offset > 1) {
                offset = 1;
            }
            if (offset < 0) {
                offset = 0;
            }
        }
        if (getStyle(sty.setName("stop-color"))) {
            color = sty.getColorValue();
        }
        if (getStyle(sty.setName("stop-opacity"))) {
            opacity = sty.getRatioValue();
        }
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("offset"))) {
            float newVal = sty.getFloatValue();
            if (newVal != offset) {
                offset = newVal;
                shapeChange = true;
            }
        }
        if (getStyle(sty.setName("stop-color"))) {
            Color newVal = sty.getColorValue();
            if (newVal != color) {
                color = newVal;
                shapeChange = true;
            }
        }
        if (getStyle(sty.setName("stop-opacity"))) {
            float newVal = sty.getFloatValue();
            if (newVal != opacity) {
                opacity = newVal;
                shapeChange = true;
            }
        }
        return shapeChange;
    }
}
