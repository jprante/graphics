package org.xbib.graphics.pdfbox.layout.table.render;

import org.xbib.graphics.pdfbox.layout.table.BorderStyleInterface;
import java.awt.Color;

public class PositionedLine {

    private float width;

    private float startX;

    private float startY;

    private float endX;

    private float endY;

    private Color color;

    private Color resetColor;

    private BorderStyleInterface borderStyle;

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStartY() {
        return startY;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public float getEndY() {
        return endY;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setResetColor(Color resetColor) {
        this.resetColor = resetColor;
    }

    public Color getResetColor() {
        return resetColor;
    }

    public void setBorderStyle(BorderStyleInterface borderStyle) {
        this.borderStyle = borderStyle;
    }

    public BorderStyleInterface getBorderStyle() {
        return borderStyle;
    }
}
