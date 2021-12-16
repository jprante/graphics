package org.xbib.graphics.svg;

import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;

public class FontFace extends SVGElement {

    public static final String TAG_NAME = "fontface";

    String fontFamily;

    private int unitsPerEm = 1000;

    private int ascent = -1;

    private int descent = -1;

    private int accentHeight = -1;

    private int underlinePosition = -1;

    private int underlineThickness = -1;

    private int strikethroughPosition = -1;

    private int strikethroughThickness = -1;

    private int overlinePosition = -1;

    private int overlineThickness = -1;

    public FontFace() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("font-family"))) {
            fontFamily = sty.getStringValue();
        }
        if (getPres(sty.setName("units-per-em"))) {
            unitsPerEm = sty.getIntValue();
        }
        if (getPres(sty.setName("ascent"))) {
            ascent = sty.getIntValue();
        }
        if (getPres(sty.setName("descent"))) {
            descent = sty.getIntValue();
        }
        if (getPres(sty.setName("accent-height"))) {
            accentHeight = sty.getIntValue();
        }
        if (getPres(sty.setName("underline-position"))) {
            underlinePosition = sty.getIntValue();
        }
        if (getPres(sty.setName("underline-thickness"))) {
            underlineThickness = sty.getIntValue();
        }
        if (getPres(sty.setName("strikethrough-position"))) {
            strikethroughPosition = sty.getIntValue();
        }
        if (getPres(sty.setName("strikethrough-thickenss"))) {
            strikethroughThickness = sty.getIntValue();
        }
        if (getPres(sty.setName("overline-position"))) {
            overlinePosition = sty.getIntValue();
        }
        if (getPres(sty.setName("overline-thickness"))) {
            overlineThickness = sty.getIntValue();
        }
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public int getUnitsPerEm() {
        return unitsPerEm;
    }

    public int getAscent() {
        if (ascent == -1) {
            ascent = unitsPerEm - ((Font) parent).getVertOriginY();
        }
        return ascent;
    }

    public int getDescent() {
        if (descent == -1) {
            descent = ((Font) parent).getVertOriginY();
        }
        return descent;
    }

    public int getAccentHeight() {
        if (accentHeight == -1) {
            accentHeight = getAscent();
        }
        return accentHeight;
    }

    public int getUnderlinePosition() {
        if (underlinePosition == -1) {
            underlinePosition = unitsPerEm * 5 / 6;
        }
        return underlinePosition;
    }

    public int getUnderlineThickness() {
        if (underlineThickness == -1) {
            underlineThickness = unitsPerEm / 20;
        }
        return underlineThickness;
    }

    public int getStrikethroughPosition() {
        if (strikethroughPosition == -1) {
            strikethroughPosition = unitsPerEm * 3 / 6;
        }
        return strikethroughPosition;
    }

    public int getStrikethroughThickness() {
        if (strikethroughThickness == -1) {
            strikethroughThickness = unitsPerEm / 20;
        }
        return strikethroughThickness;
    }

    public int getOverlinePosition() {
        if (overlinePosition == -1) {
            overlinePosition = unitsPerEm * 5 / 6;
        }
        return overlinePosition;
    }

    public int getOverlineThickness() {
        if (overlineThickness == -1) {
            overlineThickness = unitsPerEm / 20;
        }
        return overlineThickness;
    }

    @Override
    public boolean updateTime(double curTime) {
        return false;
    }

    public void setUnitsPerEm(int unitsPerEm) {
        this.unitsPerEm = unitsPerEm;
    }

    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    public void setDescent(int descent) {
        this.descent = descent;
    }

    public void setAccentHeight(int accentHeight) {
        this.accentHeight = accentHeight;
    }

    public void setUnderlinePosition(int underlinePosition) {
        this.underlinePosition = underlinePosition;
    }

    public void setUnderlineThickness(int underlineThickness) {
        this.underlineThickness = underlineThickness;
    }

    public void setStrikethroughPosition(int strikethroughPosition) {
        this.strikethroughPosition = strikethroughPosition;
    }

    public void setStrikethroughThickness(int strikethroughThickness) {
        this.strikethroughThickness = strikethroughThickness;
    }

    public void setOverlinePosition(int overlinePosition) {
        this.overlinePosition = overlinePosition;
    }

    public void setOverlineThickness(int overlineThickness) {
        this.overlineThickness = overlineThickness;
    }
}
