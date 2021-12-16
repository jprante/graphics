package org.xbib.graphics.svg;

import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Font extends SVGElement {

    public static final String TAG_NAME = "font";

    int horizOriginX = 0;

    int horizOriginY = 0;

    int horizAdvX = -1;

    int vertOriginX = -1;

    int vertOriginY = -1;

    int vertAdvY = -1;

    FontFace fontFace = null;

    MissingGlyph missingGlyph = null;

    final Map<String, SVGElement> glyphs = new HashMap<>();

    public Font() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
        super.loaderAddChild(helper, child);
        if (child instanceof Glyph) {
            glyphs.put(((Glyph) child).getUnicode(), child);
        } else if (child instanceof MissingGlyph) {
            missingGlyph = (MissingGlyph) child;
        } else if (child instanceof FontFace) {
            fontFace = (FontFace) child;
        }
    }

    @Override
    public void loaderEndElement(SVGLoaderHelper helper) throws SVGParseException {
        super.loaderEndElement(helper);
        helper.universe.registerFont(this);
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("horiz-origin-x"))) {
            horizOriginX = sty.getIntValue();
        }
        if (getPres(sty.setName("horiz-origin-y"))) {
            horizOriginY = sty.getIntValue();
        }

        if (getPres(sty.setName("horiz-adv-x"))) {
            horizAdvX = sty.getIntValue();
        }

        if (getPres(sty.setName("vert-origin-x"))) {
            vertOriginX = sty.getIntValue();
        }

        if (getPres(sty.setName("vert-origin-y"))) {
            vertOriginY = sty.getIntValue();
        }

        if (getPres(sty.setName("vert-adv-y"))) {
            vertAdvY = sty.getIntValue();
        }
    }

    public FontFace getFontFace() {
        return fontFace;
    }

    public void setFontFace(FontFace face) {
        fontFace = face;
    }

    public MissingGlyph getGlyph(String unicode) {
        Glyph retVal = (Glyph) glyphs.get(unicode);
        if (retVal == null) {
            return missingGlyph;
        }
        return retVal;
    }

    public int getHorizOriginX() {
        return horizOriginX;
    }

    public int getHorizOriginY() {
        return horizOriginY;
    }

    public int getHorizAdvX() {
        return horizAdvX;
    }

    public int getVertOriginX() {
        if (vertOriginX != -1) {
            return vertOriginX;
        }
        vertOriginX = getHorizAdvX() / 2;
        return vertOriginX;
    }

    public int getVertOriginY() {
        if (vertOriginY != -1) {
            return vertOriginY;
        }
        vertOriginY = fontFace.getAscent();
        return vertOriginY;
    }

    public int getVertAdvY() {
        if (vertAdvY != -1) {
            return vertAdvY;
        }
        vertAdvY = fontFace.getUnitsPerEm();
        return vertAdvY;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }
}
