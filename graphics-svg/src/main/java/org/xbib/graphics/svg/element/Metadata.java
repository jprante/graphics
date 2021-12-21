package org.xbib.graphics.svg.element;

public class Metadata extends SVGElement {

    @Override
    public String getTagName() {
        return "metadata";
    }

    @Override
    public boolean updateTime(double curTime) {
        return false;
    }
}
