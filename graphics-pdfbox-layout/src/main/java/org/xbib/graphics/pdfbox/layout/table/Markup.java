package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class Markup implements ParagraphProcessor {

    public enum MarkupSupportedFont {
        TIMES, COURIER, HELVETICA
    }

    public static final Map<MarkupSupportedFont, BaseFont> FONT_MAP = new EnumMap<>(Markup.MarkupSupportedFont.class);
    static {
        FONT_MAP.put(Markup.MarkupSupportedFont.HELVETICA, BaseFont.HELVETICA);
        FONT_MAP.put(Markup.MarkupSupportedFont.COURIER, BaseFont.COURIER);
        FONT_MAP.put(Markup.MarkupSupportedFont.TIMES, BaseFont.TIMES);
    }

    private String markup;

    private MarkupSupportedFont font;

    private Float fontSize;

    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public String getMarkup() {
        return markup;
    }

    public void setFont(MarkupSupportedFont font) {
        this.font = font;
    }

    public MarkupSupportedFont getFont() {
        return font;
    }

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public Float getFontSize() {
        return fontSize;
    }

    @Override
    public void process(Paragraph paragraph, Settings settings) throws IOException {
        float fontSize = getFontSize() != null ? getFontSize() : settings.getFontSize();
        paragraph.addMarkup(getMarkup(), fontSize, FONT_MAP.get(getFont()));
    }

}
