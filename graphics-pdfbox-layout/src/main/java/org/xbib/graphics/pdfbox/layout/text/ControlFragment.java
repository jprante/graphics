package org.xbib.graphics.pdfbox.layout.text;

import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import java.awt.Color;

/**
 * A control fragment has no drawable representation but is meant to control the
 * text rendering.
 */
public class ControlFragment implements TextFragment {

    protected final static FontDescriptor DEFAULT_FONT_DESCRIPTOR =
            new FontDescriptor(BaseFont.HELVETICA, 11);

    private String name;

    private final String text;

    private final FontDescriptor fontDescriptor;

    private final Color color;

    protected ControlFragment(String text, FontDescriptor fontDescriptor) {
        this(null, text, fontDescriptor, Color.black);
    }

    protected ControlFragment(String name, final String text,
                              FontDescriptor fontDescriptor, final Color color) {
        this.name = name;
        if (this.name == null) {
            this.name = getClass().getSimpleName();
        }
        this.text = text;
        this.fontDescriptor = fontDescriptor;
        this.color = color;
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return getFontDescriptor() == null ? 0 : getFontDescriptor().getSize();
    }

    @Override
    public FontDescriptor getFontDescriptor() {
        return fontDescriptor;
    }

    protected String getName() {
        return name;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "ControlFragment [" + name + "]";
    }

}
