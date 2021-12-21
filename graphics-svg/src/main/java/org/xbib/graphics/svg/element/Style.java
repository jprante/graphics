package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.SVGLoaderHelper;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.xml.StyleAttribute;
import org.xbib.graphics.svg.xml.StyleSheet;

import java.io.IOException;

public class Style extends SVGElement {

    private final StringBuilder text = new StringBuilder();

    private StyleSheet styleSheet;

    @Override
    public String getTagName() {
        return "style";
    }

    @Override
    public void loaderAddText(SVGLoaderHelper helper, String text) {
        this.text.append(text);
        styleSheet = null;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("type"))) {
            String type = sty.getStringValue();
        }
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }

    public StyleSheet getStyleSheet() {
        if (styleSheet == null && text.length() > 0) {
            styleSheet = StyleSheet.parseSheet(text.toString());
        }
        return styleSheet;
    }
}
