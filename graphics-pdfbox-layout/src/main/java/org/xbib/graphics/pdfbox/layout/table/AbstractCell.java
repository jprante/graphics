package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.table.render.AbstractCellRenderer;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import java.awt.Color;

public abstract class AbstractCell {

    private static final float DEFAULT_MIN_HEIGHT = 10f;

    private int colSpan = 1;

    private int rowSpan = 1;

    protected AbstractCellRenderer<AbstractCell> drawer;

    private Row row;

    private Column column;

    private float width;

    private float minHeight = DEFAULT_MIN_HEIGHT;

    protected Parameters parameters;

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public float getPaddingBottom() {
        return parameters.getPaddingBottom();
    }

    public float getPaddingTop() {
        return parameters.getPaddingTop();
    }

    public float getPaddingLeft() {
        return parameters.getPaddingLeft();
    }

    public float getPaddingRight() {
        return parameters.getPaddingRight();
    }

    public float getHorizontalPadding() {
        return parameters.getPaddingLeft() + parameters.getPaddingRight();
    }

    public float getVerticalPadding() {
        return parameters.getPaddingTop() + parameters.getPaddingBottom();
    }

    public float getBorderWidthTop() {
        return hasBorderTop() ? parameters.getBorderWidthTop() : 0;
    }

    public boolean hasBorderTop() {
        return parameters.getBorderWidthTop() != null && parameters.getBorderWidthTop() > 0;
    }

    public float getBorderWidthBottom() {
        return hasBorderBottom() ? parameters.getBorderWidthBottom() : 0;
    }

    public boolean hasBorderBottom() {
        return parameters.getBorderWidthBottom() != null && parameters.getBorderWidthBottom() > 0;
    }

    public float getBorderWidthLeft() {
        return hasBorderLeft() ? parameters.getBorderWidthLeft() : 0;
    }

    public boolean hasBorderLeft() {
        return parameters.getBorderWidthLeft() != null && parameters.getBorderWidthLeft() > 0;
    }

    public float getBorderWidthRight() {
        return hasBorderRight() ? parameters.getBorderWidthRight() : 0;
    }

    public boolean hasBorderRight() {
        return parameters.getBorderWidthRight() != null && parameters.getBorderWidthRight() > 0;
    }

    public BorderStyleInterface getBorderStyleTop() {
        return parameters.getBorderStyleTop();
    }

    public BorderStyleInterface getBorderStyleBottom() {
        return parameters.getBorderStyleBottom();
    }

    public BorderStyleInterface getBorderStyleLeft() {
        return parameters.getBorderStyleLeft();
    }

    public BorderStyleInterface getBorderStyleRight() {
        return parameters.getBorderStyleRight();
    }

    public boolean hasBackgroundColor() {
        return parameters.getBackgroundColor() != null;
    }

    public Color getBackgroundColor() {
        return parameters.getBackgroundColor();
    }

    public Color getBorderColor() {
        return parameters.getBorderColor();
    }

    public boolean isWordBreak() {
        return parameters.isWordBreak();
    }

    public Column getColumn() {
        return column;
    }

    public static float getDefaultMinHeight() {
        return DEFAULT_MIN_HEIGHT;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public float getWidth() {
        return width;
    }

    public int getColSpan() {
        return colSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public Row getRow() {
        return row;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public void setDrawer(AbstractCellRenderer<AbstractCell> drawer) {
        this.drawer = drawer;
    }

    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        assertIsRendered();
        return getRowSpan() > 1 ? calculateHeightForRowSpan() : getMinHeight();
    }

    public Renderer getDrawer() {
        return this.drawer != null ? this.drawer.withCell(this) : createDefaultDrawer();
    }

    protected abstract Renderer createDefaultDrawer();

    public float calculateHeightForRowSpan() {
        Row currentRow = row;
        float result = currentRow.getHeight();
        for (int i = 1; i < getRowSpan(); i++) {
            result += currentRow.getNext().getHeight();
            currentRow = currentRow.getNext();
        }
        return result;
    }

    protected void assertIsRendered() {
        if (column == null || row == null) {
            throw new TableNotYetBuiltException();
        }
    }

    public boolean isHorizontallyAligned(HorizontalAlignment alignment) {
        return getParameters().getHorizontalAlignment() == alignment;
    }

    public boolean isVerticallyAligned(VerticalAlignment alignment) {
        return getParameters().getVerticalAlignment() == alignment;
    }
}
