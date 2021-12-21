package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.element.glyph.Glyph;
import org.xbib.graphics.svg.element.glyph.MissingGlyph;
import org.xbib.graphics.svg.SVGElementException;
import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.SVGLoaderHelper;
import org.xbib.graphics.svg.SVGParseException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Font extends SVGElement {

    private int horizOriginX = 0;

    private int horizOriginY = 0;

    private int horizAdvX = -1;

    private int vertOriginX = -1;

    private int vertOriginY = -1;

    private int vertAdvY = -1;

    private FontFace fontFace = null;

    private MissingGlyph missingGlyph = null;

    private final Map<String, SVGElement> glyphs = new HashMap<>();

    @Override
    public String getTagName() {
        return "font";
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
