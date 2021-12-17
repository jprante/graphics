package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.element.Drawable;
import org.xbib.graphics.pdfbox.layout.element.Element;
import org.xbib.graphics.pdfbox.layout.element.HorizontalRuler;
import org.xbib.graphics.pdfbox.layout.font.Font;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.naturalOrder;

public class Row {

    private static final Float DEFAULT_HEIGHT = 10f;

    private Table table;

    private List<Cell> cells;

    private Parameters parameters;

    private Float height;

    private Row next;

    private Row(List<Cell> cells) {
        this.cells = cells;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public Row getNext() {
        return next;
    }

    public static Float getDefaultHeight() {
        return DEFAULT_HEIGHT;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public void setNext(Row next) {
        this.next = next;
    }

    public float getHeight() {
        if (table == null) {
            throw new TableNotYetBuiltException();
        }
        if (height == null) {
            this.height = getCells().stream()
                    .filter(cell -> cell.getRowSpan() == 1)
                    .map(Cell::getHeight)
                    .max(naturalOrder())
                    .orElse(DEFAULT_HEIGHT);
        }
        return height;
    }

    void doRowSpanSizeAdaption(float heightOfHighestCell, float rowsHeight) {
        float rowSpanSizeDifference = heightOfHighestCell - rowsHeight;
        this.height += (this.height / (heightOfHighestCell - rowSpanSizeDifference)) * rowSpanSizeDifference;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements Element {

        private final List<Cell> cells = new ArrayList<>();

        private final Parameters parameters = new Parameters();

        private Builder() {
        }

        @Override
        public Builder add(Element element) {
            if (element instanceof Cell) {
                cells.add((Cell) element);
            } else if (element instanceof HorizontalRuler) {
                Cell cell = DrawableCell.builder()
                        .drawable((Drawable) element)
                        .build();
                cells.add(cell);
            }
            return this;
        }

        public Builder font(Font font) {
            parameters.setFont(font);
            return this;
        }

        public Builder fontSize(float fontSize) {
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
            row.setParameters(parameters);
            return row;
        }
    }
}
