package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.table.render.AbstractCellRenderer;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import java.awt.Color;

public abstract class AbstractCell implements Cell {

    private static final float DEFAULT_MIN_HEIGHT = 10f;

    private int colSpan = 1;

    private int rowSpan = 1;

    protected AbstractCellRenderer<Cell> renderer;

    private Row row;

    private Column column;

    private float width;

    private float minHeight = DEFAULT_MIN_HEIGHT;

    protected Parameters parameters;

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    @Override
    public int getColSpan() {
        return colSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }
    @Override
    public void setColumn(Column column) {
        this.column = column;
    }

    public Column getColumn() {
        return column;
    }

    @Override
    public void setRow(Row row) {
        this.row = row;
    }

    public Row getRow() {
        return row;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    @Override
    public float getMinHeight() {
        return minHeight;
    }

    @Override
    public float getHeight() {
        assertIsRendered();
        return getRowSpan() > 1 ? calculateHeightForRowSpan() : getMinHeight();
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public Parameters getParameters() {
        return parameters;
    }

    @Override
    public float getPaddingLeft() {
        return parameters.getPaddingLeft();
    }

    @Override
    public float getPaddingRight() {
        return parameters.getPaddingRight();
    }

    @Override
    public float getPaddingTop() {
        return parameters.getPaddingTop();
    }

    @Override
    public float getPaddingBottom() {
        return parameters.getPaddingBottom();
    }

    public float getHorizontalPadding() {
        return parameters.getPaddingLeft() + parameters.getPaddingRight();
    }

    public float getVerticalPadding() {
        return parameters.getPaddingTop() + parameters.getPaddingBottom();
    }

    @Override
    public float getBorderWidthTop() {
        return hasBorderTop() ? parameters.getBorderWidthTop() : 0;
    }

    @Override
    public boolean hasBorderTop() {
        return parameters.getBorderWidthTop() != null && parameters.getBorderWidthTop() > 0;
    }

    @Override
    public float getBorderWidthBottom() {
        return hasBorderBottom() ? parameters.getBorderWidthBottom() : 0;
    }

    @Override
    public boolean hasBorderBottom() {
        return parameters.getBorderWidthBottom() != null && parameters.getBorderWidthBottom() > 0;
    }

    @Override
    public float getBorderWidthLeft() {
        return hasBorderLeft() ? parameters.getBorderWidthLeft() : 0;
    }

    @Override
    public boolean hasBorderLeft() {
        return parameters.getBorderWidthLeft() != null && parameters.getBorderWidthLeft() > 0;
    }

    @Override
    public float getBorderWidthRight() {
        return hasBorderRight() ? parameters.getBorderWidthRight() : 0;
    }

    @Override
    public boolean hasBorderRight() {
        return parameters.getBorderWidthRight() != null && parameters.getBorderWidthRight() > 0;
    }

    @Override
    public BorderStyleInterface getBorderStyleTop() {
        return parameters.getBorderStyleTop();
    }

    @Override
    public BorderStyleInterface getBorderStyleBottom() {
        return parameters.getBorderStyleBottom();
    }

    @Override
    public BorderStyleInterface getBorderStyleLeft() {
        return parameters.getBorderStyleLeft();
    }

    @Override
    public BorderStyleInterface getBorderStyleRight() {
        return parameters.getBorderStyleRight();
    }

    @Override
    public boolean hasBackgroundColor() {
        return parameters.getBackgroundColor() != null;
    }

    @Override
    public Color getBackgroundColor() {
        return parameters.getBackgroundColor();
    }

    @Override
    public Color getBorderColor() {
        return parameters.getBorderColor();
    }

    @Override
    public boolean isHorizontallyAligned(HorizontalAlignment alignment) {
        return parameters.getHorizontalAlignment() == alignment;
    }

    @Override
    public boolean isVerticallyAligned(VerticalAlignment alignment) {
        return parameters.getVerticalAlignment() == alignment;
    }

    public boolean isWordBreak() {
        return parameters.isWordBreak();
    }

    public void setRenderer(AbstractCellRenderer<Cell> renderer) {
        this.renderer = renderer;
    }

    @Override
    public Renderer getRenderer() {
        return this.renderer != null ? this.renderer.withCell(this) : createDefaultRenderer();
    }

    @Override
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

    protected abstract Renderer createDefaultRenderer();
}
