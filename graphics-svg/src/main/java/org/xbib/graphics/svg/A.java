package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.Group;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.io.IOException;
import java.net.URI;

public class A extends Group {

    public static final String TAG_NAME = "a";

    URI href;

    String title;

    public A() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();

        if (getPres(sty.setName("xlink:href"))) {
            href = sty.getURIValue(getXMLBase());
        }

        if (getPres(sty.setName("xlink:title"))) {
            title = sty.getStringValue();
        }
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("xlink:href"))) {
            href = sty.getURIValue(getXMLBase());
        }
        if (getPres(sty.setName("xlink:title"))) {
            title = sty.getStringValue();
        }
        return changeState;
    }
}
