package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.Font;

import java.io.IOException;

public class Markup implements ParagraphProcessor {

    private String markup;

    private Font font;

    private Float fontSize;

    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public String getMarkup() {
        return markup;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public Float getFontSize() {
        return fontSize;
    }

    @Override
    public void process(Paragraph paragraph, Parameters parameters) {
        float fontSize = getFontSize() != null ? getFontSize() : parameters.getFontSize();
        paragraph.addMarkup(getMarkup(), fontSize, font);
    }

}
