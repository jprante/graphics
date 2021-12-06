package org.xbib.graphics.pdfbox.layout.font;

import org.xbib.graphics.pdfbox.layout.elements.Document;

public enum Fonts {
    HELVETICA,
    TIMES,
    COURIER,
    NOTOSANS;

    public Font getFont(Document document) {
        if ("notosans".equalsIgnoreCase(name())) {
            return new NotoSansFont(document);
        }
        return BaseFont.valueOf(name());
    }
}
