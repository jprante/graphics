package org.xbib.graphics.pdfbox.layout.table.render;

import java.awt.Color;

public class PositionedRectangle {

    private final float x;

    private final float y;

    private final float width;

    private final float height;

    private final Color color;

    public PositionedRectangle(float x,
                               float y,
                               float width,
                               float height,
                               Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }
}
