package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.SVGElement;

public class Metadata extends SVGElement {

    public static final String TAG_NAME = "metadata";

    public Metadata() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public boolean updateTime(double curTime) {
        return false;
    }
}
