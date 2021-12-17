package org.xbib.graphics.svg.element.glyph;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;

public class Glyph extends MissingGlyph {

    public static final String TAG_NAME = "missingglyph";

    String unicode;

    public Glyph() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
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
