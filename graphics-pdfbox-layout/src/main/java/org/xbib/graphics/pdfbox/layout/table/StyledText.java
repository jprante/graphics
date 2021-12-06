package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.util.PdfUtil;

import java.awt.Color;

public class StyledText implements ParagraphProcessor {

    private String text;

    private Float fontSize;

    private Font font;

    private Color color;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public Float getFontSize() {
        return fontSize;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void process(Paragraph paragraph, Parameters parameters) {
        final float actualFontSize = getFontSize() != null ? getFontSize() : parameters.getFontSize();
        final Font actualFont = getFont() != null ? getFont() : parameters.getFont();
        final Color actualColor = getColor() != null ? getColor() : parameters.getTextColor();
        // TODO this is a complete mess to handle new lines!!!
        String[] lines = getText().split(PdfUtil.NEW_LINE_REGEX);
        for (int i = 0; i < lines.length; i++) {
            FontDescriptor fontDescriptor = new FontDescriptor(actualFont, actualFontSize);
            paragraph.add(new org.xbib.graphics.pdfbox.layout.text.StyledText(lines[i], fontDescriptor, actualColor, 0f, 0, 0));
            if (i < lines.length - 1) {
                paragraph.add(new org.xbib.graphics.pdfbox.layout.text.NewLine(new FontDescriptor(actualFont, actualFontSize)));
            }
        }
    }
}
