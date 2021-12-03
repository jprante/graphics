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

    private Parameters parameters;

    private Float height;

    private Row next;

    private Row(final List<AbstractCell> cells) {
        super();
        this.cells = cells;
    }

    public void setSettings(Parameters parameters) {
        this.parameters = parameters;
    }

    public Parameters getSettings() {
        return parameters;
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

        private final Parameters parameters = new Parameters();

        private Builder() {
        }

        public Builder add(final AbstractCell cell) {
            cells.add(cell);
            return this;
        }

        public Builder font(Font font) {
            parameters.setFont(font);
            return this;
        }

        public Builder fontSize(final Integer fontSize) {
            parameters.setFontSize(fontSize);
            return this;
        }

        public Builder textColor(final Color textColor) {
            parameters.setTextColor(textColor);
            return this;
        }

        public Builder backgroundColor(final Color backgroundColor) {
            parameters.setBackgroundColor(backgroundColor);
            return this;
        }

        public Builder padding(final float padding) {
            parameters.setPaddingTop(padding);
            parameters.setPaddingBottom(padding);
            parameters.setPaddingLeft(padding);
            parameters.setPaddingRight(padding);
            return this;
        }

        public Builder borderWidth(final float borderWidth) {
            parameters.setBorderWidthTop(borderWidth);
            parameters.setBorderWidthBottom(borderWidth);
            parameters.setBorderWidthLeft(borderWidth);
            parameters.setBorderWidthRight(borderWidth);
            return this;
        }

        public Builder borderStyle(final BorderStyleInterface borderStyle) {
            parameters.setBorderStyleTop(borderStyle);
            parameters.setBorderStyleBottom(borderStyle);
            parameters.setBorderStyleLeft(borderStyle);
            parameters.setBorderStyleRight(borderStyle);
            return this;
        }

        public Builder borderColor(final Color borderColor) {
            parameters.setBorderColor(borderColor);
            return this;
        }

        public Builder horizontalAlignment(HorizontalAlignment alignment) {
            parameters.setHorizontalAlignment(alignment);
            return this;
        }

        public Builder verticalAlignment(VerticalAlignment alignment) {
            parameters.setVerticalAlignment(alignment);
            return this;
        }

        public Builder wordBreak(Boolean wordBreak) {
            parameters.setWordBreak(wordBreak);
            return this;
        }

        public Row build() {
            final Row row = new Row(cells);
            row.setSettings(parameters);
            //row.setHeight(height);
            return row;
        }
    }
}
