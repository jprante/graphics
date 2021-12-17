package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.SVGElement;

public class Title extends SVGElement {

    public static final String TAG_NAME = "title";

    StringBuilder text = new StringBuilder();

    public Title() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public void loaderAddText(SVGLoaderHelper helper, String text) {
        this.text.append(text);
    }

    public String getText() {
        return text.toString();
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }
}
