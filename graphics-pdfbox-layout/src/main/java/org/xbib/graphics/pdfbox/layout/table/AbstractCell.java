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

    protected Settings settings;

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public float getPaddingBottom() {
        return settings.getPaddingBottom();
    }

    public float getPaddingTop() {
        return settings.getPaddingTop();
    }

    public float getPaddingLeft() {
        return settings.getPaddingLeft();
    }

    public float getPaddingRight() {
        return settings.getPaddingRight();
    }

    public float getHorizontalPadding() {
        return settings.getPaddingLeft() + settings.getPaddingRight();
    }

    public float getVerticalPadding() {
        return settings.getPaddingTop() + settings.getPaddingBottom();
    }

    public float getBorderWidthTop() {
        return hasBorderTop() ? settings.getBorderWidthTop() : 0;
    }

    public boolean hasBorderTop() {
        return settings.getBorderWidthTop() != null && settings.getBorderWidthTop() > 0;
    }

    public float getBorderWidthBottom() {
        return hasBorderBottom() ? settings.getBorderWidthBottom() : 0;
    }

    public boolean hasBorderBottom() {
        return settings.getBorderWidthBottom() != null && settings.getBorderWidthBottom() > 0;
    }

    public float getBorderWidthLeft() {
        return hasBorderLeft() ? settings.getBorderWidthLeft() : 0;
    }

    public boolean hasBorderLeft() {
        return settings.getBorderWidthLeft() != null && settings.getBorderWidthLeft() > 0;
    }

    public float getBorderWidthRight() {
        return hasBorderRight() ? settings.getBorderWidthRight() : 0;
    }

    public boolean hasBorderRight() {
        return settings.getBorderWidthRight() != null && settings.getBorderWidthRight() > 0;
    }

    public BorderStyleInterface getBorderStyleTop() {
        return settings.getBorderStyleTop();
    }

    public BorderStyleInterface getBorderStyleBottom() {
        return settings.getBorderStyleBottom();
    }

    public BorderStyleInterface getBorderStyleLeft() {
        return settings.getBorderStyleLeft();
    }

    public BorderStyleInterface getBorderStyleRight() {
        return settings.getBorderStyleRight();
    }

    public boolean hasBackgroundColor() {
        return settings.getBackgroundColor() != null;
    }

    public Color getBackgroundColor() {
        return settings.getBackgroundColor();
    }

    public Color getBorderColor() {
        return settings.getBorderColor();
    }

    public boolean isWordBreak() {
        return settings.isWordBreak();
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

    public Settings getSettings() {
        return settings;
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

    public void setSettings(Settings settings) {
        this.settings = settings;
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
        return getSettings().getHorizontalAlignment() == alignment;
    }

    public boolean isVerticallyAligned(VerticalAlignment alignment) {
        return getSettings().getVerticalAlignment() == alignment;
    }

    /*public abstract static class AbstractCellBuilder<C extends AbstractCell, B extends AbstractCell.AbstractCellBuilder<C, B>> {

        protected Settings settings = new Settings();

        // We don't want to expose settings directly!
        private void settings(Settings settings) {}

        public B borderWidth(final float borderWidth) {
            settings.setBorderWidthTop(borderWidth);
            settings.setBorderWidthBottom(borderWidth);
            settings.setBorderWidthLeft(borderWidth);
            settings.setBorderWidthRight(borderWidth);
            return (B) this;
        }

        public B borderWidthTop(final float borderWidth) {
            settings.setBorderWidthTop(borderWidth);
            return (B) this;
        }

        public B borderWidthBottom(final float borderWidth) {
            settings.setBorderWidthBottom(borderWidth);
            return (B) this;
        }

        public B borderWidthLeft(final float borderWidth) {
            settings.setBorderWidthLeft(borderWidth);
            return (B) this;
        }

        public B borderWidthRight(final float borderWidth) {
            settings.setBorderWidthRight(borderWidth);
            return (B) this;
        }

        public B borderStyleTop(final BorderStyleInterface style) {
            settings.setBorderStyleTop(style);
            return (B) this;
        }

        public B borderStyleBottom(final BorderStyleInterface style) {
            settings.setBorderStyleBottom(style);
            return (B) this;
        }

        public B borderStyleLeft(final BorderStyleInterface style) {
            settings.setBorderStyleLeft(style);
            return (B) this;
        }

        public B borderStyleRight(final BorderStyleInterface style) {
            settings.setBorderStyleRight(style);
            return (B) this;
        }

        public B borderStyle(final BorderStyleInterface style) {
            return this.borderStyleLeft(style)
                    .borderStyleRight(style)
                    .borderStyleBottom(style)
                    .borderStyleTop(style);
        }

        public B padding(final float padding) {
            return this.paddingTop(padding)
                    .paddingBottom(padding)
                    .paddingLeft(padding)
                    .paddingRight(padding);
        }

        public B paddingTop(final float padding) {
            settings.setPaddingTop(padding);
            return (B) this;
        }

        public B paddingBottom(final float padding) {
            settings.setPaddingBottom(padding);
            return (B) this;
        }

        public B paddingLeft(final float padding) {
            settings.setPaddingLeft(padding);
            return (B) this;
        }

        public B paddingRight(final float padding) {
            settings.setPaddingRight(padding);
            return (B) this;
        }

        public B horizontalAlignment(final HorizontalAlignment alignment) {
            settings.setHorizontalAlignment(alignment);
            return (B) this;
        }

        public B verticalAlignment(final VerticalAlignment alignment) {
            settings.setVerticalAlignment(alignment);
            return (B) this;
        }

        public B backgroundColor(final Color backgroundColor) {
            settings.setBackgroundColor(backgroundColor);
            return (B) this;
        }

        public B borderColor(final Color borderColor) {
            settings.setBorderColor(borderColor);
            return (B) this;
        }

        public B wordBreak(final Boolean wordBreak) {
            settings.setWordBreak(wordBreak);
            return (B) this;
        }
    }*/
}
