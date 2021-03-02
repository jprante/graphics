package org.xbib.graphics.pdfbox.layout.table.render;

import org.xbib.graphics.pdfbox.layout.font.Font;

import java.awt.Color;

public class PositionedStyledText {

    private final float x;

    private final float y;

    private final String text;

    private final Font font;

    private final int fontSize;

    private final Color color;

    public PositionedStyledText(float x, float y, String text, Font font, int fontSize, Color color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public int getFontSize() {
        return fontSize;
    }

    public Color getColor() {
        return color;
    }
}
