package org.xbib.graphics.pdfbox.layout.element;

import org.xbib.graphics.pdfbox.layout.font.Font;

public class TextElement implements Element {

    private final String value;

    private final Font font;

    private final float size;

    public TextElement(String value, Font font, float size) {
        this.value = value;
        this.font = font;
        this.size = size;
    }

    public String getValue() {
        return value;
    }

    public Font getFont() {
        return font;
    }

    public float getSize() {
        return size;
    }

}
