package org.xbib.graphics.svg.element.glyph;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;

public class Glyph extends MissingGlyph {

    private String unicode;

    @Override
    public String getTagName() {
        return "glyph";
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("unicode"))) {
            unicode = sty.getStringValue();
        }
    }

    public String getUnicode() {
        return unicode;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }
}
