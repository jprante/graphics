package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;

import java.io.IOException;

public class Hkern extends SVGElement {

    @Override
    public String getTagName() {
        return "hkern";
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }
}
