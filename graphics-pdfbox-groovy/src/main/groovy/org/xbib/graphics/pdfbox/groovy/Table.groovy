package org.xbib.graphics.pdfbox.groovy


import java.math.RoundingMode

class Table extends BlockNode implements BackgroundAssignable {

    static Margin defaultMargin = new Margin(top: 0, bottom: 0, left: 0, right: 0)

    List<Row> children = []

    Integer padding = 10

    Integer width = 0

    List<BigDecimal> columns = []

    int getColumnCount() {
        if (columns) {
            columns.size()
        } else {
            (children) ? children.max { it.children.size() }.children.size() : 0
        }
    }

    void normalizeColumnWidths() {
        updateRowspanColumns()
        width = Math.min(width ?: maxWidth, maxWidth)
        if (!columns) {
            columnCount.times { columns << (1 as BigDecimal) }
        }
        List<BigDecimal> columnWidths = computeColumnWidths()
        children.each { row ->
            int columnWidthIndex = 0
            row.children.eachWithIndex { column, index ->
                int endIndex = columnWidthIndex + column.colspan - 1
                BigDecimal missingBorderWidth = (column.colspan - 1) * border.size
                column.width = columnWidths[columnWidthIndex..endIndex].sum() + missingBorderWidth
                columnWidthIndex += column.colspan
                column.children.findAll { it instanceof Table }.each { it.normalizeColumnWidths() }
            }
        }
    }

    List<BigDecimal> computeColumnWidths() {

        BigDecimal relativeTotal = columns.sum() as BigDecimal

        BigDecimal totalBorderWidth = (columnCount + 1) * border.size

        BigDecimal totalCellWidth = width - totalBorderWidth

        List<BigDecimal> columnWidths = []

        columns.eachWithIndex { column, index ->
            if (index == columns.size() - 1) {
                columnWidths << ((totalCellWidth - (columnWidths.sum() as BigDecimal ?: 0)) as BigDecimal)
            } else {
                BigDecimal d = (columns[index] / relativeTotal) * totalCellWidth
                columnWidths << d.setScale(0, RoundingMode.CEILING)
            }
        }
        columnWidths
    }

    void updateRowspanColumns() {
        def updatedColumns = []
        children.eachWithIndex { row, rowIndex ->
            row.children.eachWithIndex { column, columnIndex ->
                if (column.rowspan > 1 && !updatedColumns.contains(column)) {
                    int rowspanEnd = Math.min(children.size() - 1, rowIndex + column.rowspan - 1)
                    (rowIndex + 1..rowspanEnd).each {
                        children[it].children.addAll(columnIndex, [column])
                    }
                    updatedColumns << column
                }
            }
        }
    }

    private int getMaxWidth() {
        if (parent instanceof Document) {
            parent.width - parent.margin.left - parent.margin.right
        } else if (parent instanceof Cell) {
            Table outerTable = parent.parent.parent
            parent.width - (outerTable.padding * 2)
        } else {
            0
        }
    }

}
