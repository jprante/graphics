package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleSheet;

import java.io.IOException;

public class Defs extends TransformableElement {

    @Override
    public String getTagName() {
        return "defs";
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean stateChange = false;
        for (SVGElement ele : children) {
            stateChange = stateChange || ele.updateTime(curTime);
        }
        return super.updateTime(curTime) || stateChange;
    }

    public StyleSheet getStyleSheet() {
        for (int i = 0; i < getNumChildren(); ++i) {
            SVGElement ele = getChild(i);
            if (ele instanceof Style) {
                return ((Style) ele).getStyleSheet();
            }
        }
        return null;
    }
}
