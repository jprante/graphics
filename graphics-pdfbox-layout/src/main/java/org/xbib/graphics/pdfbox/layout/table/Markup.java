package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.element.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.Font;

public class Markup implements ParagraphProcessor {

    private String value;

    private Font font;

    private Float fontSize;

    public Markup setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Markup setFont(Font font) {
        this.font = font;
        return this;
    }

    public Font getFont() {
        return font;
    }

    public Markup setFontSize(Float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Float getFontSize() {
        return fontSize;
    }

    @Override
    public void process(Paragraph paragraph, Parameters parameters) {
        float fontSize = getFontSize() != null ? getFontSize() : parameters.getFontSize();
        paragraph.addMarkup(getValue(), fontSize, font);
    }

}
