package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.element.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotatedStyledText;
import org.xbib.graphics.pdfbox.layout.text.annotations.Annotations;

import java.awt.Color;
import java.util.Collections;

public class Hyperlink implements ParagraphProcessor {

    private String text;

    private String url;

    private Font font;

    private Float fontSize;

    private Color color = Color.BLUE;

    private float baselineOffset = 1f;

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setBaselineOffset(float baselineOffset) {
        this.baselineOffset = baselineOffset;
    }

    public float getBaselineOffset() {
        return baselineOffset;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Float getFontSize() {
        return fontSize;
    }

    @Override
    public void process(Paragraph paragraph, Parameters parameters) {
        Annotations.HyperlinkAnnotation hyperlink =
                new Annotations.HyperlinkAnnotation(getUrl(), Annotations.HyperlinkAnnotation.LinkStyle.ul);
        FontDescriptor fontDescriptor = new FontDescriptor(getFont() != null ? getFont() : parameters.getFont(),
                getFontSize() != null ? getFontSize() : parameters.getFontSize());
        paragraph.add(new AnnotatedStyledText(getText(), fontDescriptor,
                        getColor(), getBaselineOffset(), 0, 0, Collections.singleton(hyperlink)));
    }

}