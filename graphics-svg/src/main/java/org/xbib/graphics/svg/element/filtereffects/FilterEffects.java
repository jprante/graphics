package org.xbib.graphics.svg.element.filtereffects;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Rectangle;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public abstract class FilterEffects extends SVGElement {

    private float x = 0f;

    private float y = 0f;

    private float width = 1f;

    private float height = 1f;

    private URL href;

    @Override
    public String getTagName() {
        return "filtereffects";
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
        if (getPres(sty.setName("xlink:href"))) {
            URI src = sty.getURIValue(getXMLBase());
            URL newVal;
            try {
                newVal = src.toURL();
            } catch (MalformedURLException e) {
                throw new SVGException(e);
            }
            if (!newVal.equals(href)) {
                href = newVal;
                stateChange = true;
            }
        }
        return stateChange;
    }
}
