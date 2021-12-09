package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.font.Font;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Table {

    private static final Font DEFAULT_FONT = BaseFont.HELVETICA;

    private static final Float DEFAULT_FONT_SIZE = 11f;

    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;

    private static final Color DEFAULT_BORDER_COLOR = Color.BLACK;

    private static final BorderStyleInterface DEFAULT_BORDER_STYLE = BorderStyle.SOLID;

    private static final float DEFAULT_PADDING = 4f;

    private static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGNMENT = HorizontalAlignment.LEFT;

    private static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT = VerticalAlignment.MIDDLE;

    private final List<Row> rows;

    private final List<Column> columns;

    private final Set<Point> rowSpanCells;

    private Parameters parameters;

    private int numberOfColumns;

    private float width;

    public Table(List<Row> rows, List<Column> columns, Set<Point> rowSpanCells) {
        this.rows = rows;
        this.columns = columns;
        this.rowSpanCells = rowSpanCells;
    }

    public void setSettings(Parameters parameters) {
        this.parameters = parameters;
    }

    public Parameters getSettings() {
        return parameters;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public static BorderStyleInterface getDefaultBorderStyle() {
        return DEFAULT_BORDER_STYLE;
    }

    public static Color getDefaultBorderColor() {
        return DEFAULT_BORDER_COLOR;
    }

    public static Color getDefaultTextColor() {
        return DEFAULT_TEXT_COLOR;
    }

    public static float getDefaultPadding() {
        return DEFAULT_PADDING;
    }

    public static HorizontalAlignment getDefaultHorizontalAlignment() {
        return DEFAULT_HORIZONTAL_ALIGNMENT;
    }

    public static float getDefaultFontSize() {
        return DEFAULT_FONT_SIZE;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public static Font getDefaultFont() {
        return DEFAULT_FONT;
    }

    public Set<Point> getRowSpanCells() {
        return rowSpanCells;
    }

    public static VerticalAlignment getDefaultVerticalAlignment() {
        return DEFAULT_VERTICAL_ALIGNMENT;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public float getHeight() {
        return rows.stream().map(Row::getHeight).reduce(0F, Float::sum);
    }

    public boolean isRowSpanAt(int rowIndex, int columnIndex) {
        return rowSpanCells.contains(new Point(rowIndex, columnIndex));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Parameters parameters = new Parameters();

        private final List<Row> rows = new ArrayList<>();

        private final List<Column> columns = new ArrayList<>();

        private final Set<Point> rowSpanCells = new HashSet<>();

        private int numberOfColumns;

        private float width;

        private Builder() {
            parameters.setFont(DEFAULT_FONT);
            parameters.setFontSize(DEFAULT_FONT_SIZE);
            parameters.setTextColor(DEFAULT_TEXT_COLOR);
            parameters.setBorderColor(DEFAULT_BORDER_COLOR);
            parameters.setBorderStyleTop(DEFAULT_BORDER_STYLE);
            parameters.setBorderStyleBottom(DEFAULT_BORDER_STYLE);
            parameters.setBorderStyleLeft(DEFAULT_BORDER_STYLE);
            parameters.setBorderStyleRight(DEFAULT_BORDER_STYLE);
            parameters.setPaddingTop(DEFAULT_PADDING);
            parameters.setPaddingBottom(DEFAULT_PADDING);
            parameters.setPaddingLeft(DEFAULT_PADDING);
            parameters.setPaddingRight(DEFAULT_PADDING);
            parameters.setWordBreak(true);
        }

        public Builder addRow(final Row row) {
            // Store how many cells can or better have to be omitted in the next rows
            // due to cells in this row that declare row spanning
            updateRowSpanCellsSet(row.getCells());
            if (!rows.isEmpty()) {
                rows.get(rows.size() - 1).setNext(row);
            }
            rows.add(row);
            return this;
        }

        private float getAvailableCellWidthRespectingSpan(int columnIndex, int span) {
            float cellWidth = 0;
            for (int i = 0; i < span; i++) {
                cellWidth += columns.get(columnIndex + i).getWidth();
            }
            return cellWidth;
        }

        // This method is unfortunately a bit complex, but what it does is basically:
        // Put every cell coordinate in the set which needs to be skipped because it is
        // "contained" in another cell due to row spanning.
        // The coordinates are those of the table how it would look like without any spanning.
        private void updateRowSpanCellsSet(List<Cell> cells) {
            int currentColumn = 0;
            for (Cell cell : cells) {
                while (rowSpanCells.contains(new Point(rows.size(), currentColumn))) {
                    currentColumn++;
                }
                if (cell.getRowSpan() > 1) {
                    for (int rowsToSpan = 0; rowsToSpan < cell.getRowSpan(); rowsToSpan++) {
                        // Skip first row's cell, because that is a regular cell
                        if (rowsToSpan >= 1) {
                            for (int colSpan = 0; colSpan < cell.getColSpan(); colSpan++) {
                                rowSpanCells.add(new Point(rows.size() + rowsToSpan, currentColumn + colSpan));
                            }
                        }
                    }
                }
                currentColumn += cell.getColSpan();
            }
        }

        public Builder addColumnsOfWidth(final float... columnWidths) {
            for (float columnWidth : columnWidths) {
                addColumnOfWidth(columnWidth);
            }
            return this;
        }

        public Builder addColumnOfWidth(float width) {
            Column column = new Column(width);
            numberOfColumns++;
            columns.add(column);
            this.width += column.getWidth();
            return this;
        }

        public Builder font(Font font) {
            parameters.setFont(font);
            return this;
        }

        public Builder fontSize(Float fontSize) {
            parameters.setFontSize(fontSize);
            return this;
        }

        public Builder textColor(Color textColor) {
            parameters.setTextColor(textColor);
            return this;
        }

        public Builder backgroundColor(Color backgroundColor) {
            parameters.setBackgroundColor(backgroundColor);
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

        public Builder padding(final float padding) {
            parameters.setPaddingTop(padding);
            parameters.setPaddingBottom(padding);
            parameters.setPaddingLeft(padding);
            parameters.setPaddingRight(padding);
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

        public Table build() {
            if (getNumberOfRegularCells() != getNumberOfSpannedCells()) {
                throw new TableSetupException("Number of table cells does not match with table setup. " +
                        "This could be due to row or col spanning not being correct");
            }
            Table table = new Table(rows, columns, rowSpanCells);
            table.setSettings(parameters);
            table.setWidth(width);
            table.setNumberOfColumns(numberOfColumns);
            setupConnectionsBetweenElementsFor(table);
            correctHeightOfCellsDueToRowSpanningIfNecessaryFor(table);
            return table;
        }

        private void setupConnectionsBetweenElementsFor(Table table) {
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                Row row = rows.get(rowIndex);
                row.setTable(table);
                if (table.getSettings() != null) {
                    row.getParameters().fillingMergeBy(table.getSettings());
                }
                int columnIndex = 0;
                for (Cell cell : row.getCells()) {
                    cell.getParameters().fillingMergeBy(row.getParameters());
                    cell.setRow(row);
                    while (table.isRowSpanAt(rowIndex, columnIndex)) {
                        columnIndex++;
                    }
                    Column column = table.getColumns().get(columnIndex);
                    cell.setColumn(column);
                    cell.setWidth(getAvailableCellWidthRespectingSpan(columnIndex, cell.getColSpan()));
                    columnIndex += cell.getColSpan();
                }
            }
            for (int i = 0; i < table.getColumns().size(); i++) {
                final Column column = table.getColumns().get(i);
                column.setTable(table);
                if (i < table.getColumns().size() - 1) {
                    column.setNext(table.getColumns().get(i + 1));
                }
            }
        }

        private void correctHeightOfCellsDueToRowSpanningIfNecessaryFor(Table table) {
            for (int i = 0; i < table.getRows().size(); i++) {
                final Optional<Cell> highestSpanningCell = rows.get(i).getCells().stream()
                        .filter(x -> x.getRowSpan() > 1)
                        .max(Comparator.comparing(Cell::getMinHeight));
                if (highestSpanningCell.isPresent()) {
                    final float heightOfHighestCell = highestSpanningCell.get().getMinHeight();
                    float regularHeightOfRows = 0;
                    for (int j = i; j < i + highestSpanningCell.get().getRowSpan(); j++) {
                        regularHeightOfRows += rows.get(j).getHeight();
                    }
                    if (heightOfHighestCell > regularHeightOfRows) {
                        for (int k = 0; k < table.getColumns().size(); k++) {
                            float rowsHeight = 0;
                            for (int l = i; l < (i + highestSpanningCell.get().getRowSpan()); l++) {
                                rowsHeight += table.getRows().get(l).getHeight();
                            }
                            for (int l = i; l < (i + highestSpanningCell.get().getRowSpan()); l++) {
                                final Row rowThatNeedsAdaption = table.getRows().get(l);
                                rowThatNeedsAdaption.doRowSpanSizeAdaption(heightOfHighestCell, rowsHeight);
                            }
                        }
                    }
                }
            }
        }

        private int getNumberOfRegularCells() {
            return columns.size() * rows.size();
        }

        private int getNumberOfSpannedCells() {
            return rows.stream()
                    .flatMapToInt(row -> row.getCells().stream().mapToInt(cell -> cell.getRowSpan() * cell.getColSpan()))
                    .sum();
        }
    }
}
