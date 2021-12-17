package org.xbib.graphics.pdfbox.layout.font;

import org.xbib.graphics.pdfbox.layout.element.Document;

import java.util.HashMap;
import java.util.Map;

public enum Fonts {
    HELVETICA,
    TIMES,
    COURIER,
    NOTOSANS;

    private final Map<String, Font> map = new HashMap<>();

    public Font getFont(Document document) {
        return map.computeIfAbsent(name(), name -> {
            if ("notosans".equalsIgnoreCase(name())) {
                return new NotoSansFont(document);
            }
            return BaseFont.valueOf(name());
        });
    }
}
