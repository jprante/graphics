package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;

public class Hkern extends SVGElement {

    public static final String TAG_NAME = "hkern";

    String u1;

    String u2;

    int k;

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
       if (getPres(sty.setName("u1"))) {
            u1 = sty.getStringValue();
        }
        if (getPres(sty.setName("u2"))) {
            u2 = sty.getStringValue();
        }
        if (getPres(sty.setName("k"))) {
            k = sty.getIntValue();
        }
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }
}
