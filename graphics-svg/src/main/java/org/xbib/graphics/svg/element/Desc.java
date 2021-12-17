package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGLoaderHelper;

public class Desc extends SVGElement {

    public static final String TAG_NAME = "desc";

    StringBuilder text = new StringBuilder();

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
    public boolean updateTime(double curTime) {
        return false;
    }
}
