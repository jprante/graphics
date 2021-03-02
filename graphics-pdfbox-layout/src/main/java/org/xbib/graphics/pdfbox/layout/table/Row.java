package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.font.Font;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.naturalOrder;

public class Row {

    private static final Float DEFAULT_HEIGHT = 10f;

    private Table table;

    private List<AbstractCell> cells;

    private Settings settings;

    private Float height;

    private Row next;

    private Row(final List<AbstractCell> cells) {
        super();
        this.cells = cells;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public Row getNext() {
        return next;
    }

    public static Float getDefaultHeight() {
        return DEFAULT_HEIGHT;
    }

    public List<AbstractCell> getCells() {
        return cells;
    }

    public Table getTable() {
        return table;
    }

    public void setCells(List<AbstractCell> cells) {
        this.cells = cells;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public void setNext(Row next) {
        this.next = next;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public float getHeight() {
        if (table == null) {
            throw new TableNotYetBuiltException();
        }

        if (height == null) {
            this.height = getCells().stream()
                    .filter(cell -> cell.getRowSpan() == 1)
                    .map(AbstractCell::getHeight)
                    .max(naturalOrder())
                    .orElse(DEFAULT_HEIGHT);
        }

        return height;
    }

    void doRowSpanSizeAdaption(float heightOfHighestCell, float rowsHeight) {
        final float rowSpanSizeDifference = heightOfHighestCell - rowsHeight;
        this.height += (this.height / (heightOfHighestCell - rowSpanSizeDifference)) * rowSpanSizeDifference;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<AbstractCell> cells = new ArrayList<>();

        private final Settings settings = new Settings();

        private Builder() {
        }

        public Builder add(final AbstractCell cell) {
            cells.add(cell);
            return this;
        }

        public Builder font(Font font) {
            settings.setFont(font);
            return this;
        }

        public Builder fontSize(final Integer fontSize) {
            settings.setFontSize(fontSize);
            return this;
        }

        public Builder textColor(final Color textColor) {
            settings.setTextColor(textColor);
            return this;
        }

        public Builder backgroundColor(final Color backgroundColor) {
            settings.setBackgroundColor(backgroundColor);
            return this;
        }

        public Builder padding(final float padding) {
            settings.setPaddingTop(padding);
            settings.setPaddingBottom(padding);
            settings.setPaddingLeft(padding);
            settings.setPaddingRight(padding);
            return this;
        }

        public Builder borderWidth(final float borderWidth) {
            settings.setBorderWidthTop(borderWidth);
            settings.setBorderWidthBottom(borderWidth);
            settings.setBorderWidthLeft(borderWidth);
            settings.setBorderWidthRight(borderWidth);
            return this;
        }

        public Builder borderStyle(final BorderStyleInterface borderStyle) {
            settings.setBorderStyleTop(borderStyle);
            settings.setBorderStyleBottom(borderStyle);
            settings.setBorderStyleLeft(borderStyle);
            settings.setBorderStyleRight(borderStyle);
            return this;
        }

        public Builder borderColor(final Color borderColor) {
            settings.setBorderColor(borderColor);
            return this;
        }

        public Builder horizontalAlignment(HorizontalAlignment alignment) {
            settings.setHorizontalAlignment(alignment);
            return this;
        }

        public Builder verticalAlignment(VerticalAlignment alignment) {
            settings.setVerticalAlignment(alignment);
            return this;
        }

        public Builder wordBreak(Boolean wordBreak) {
            settings.setWordBreak(wordBreak);
            return this;
        }

        public Row build() {
            final Row row = new Row(cells);
            row.setSettings(settings);
            //row.setHeight(height);
            return row;
        }
    }
}
