package org.xbib.graphics.pdfbox.groovy.render

import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Line
import org.xbib.graphics.pdfbox.groovy.Table
import org.xbib.graphics.pdfbox.groovy.TextBlock
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument

class CellRenderer implements Renderable {

    BigDecimal currentRowHeight = 0

    BigDecimal renderedHeight = 0

    Cell cell

    List<Renderable> childRenderers = []

    CellRenderer(Cell cell, PdfDocument pdfDocument, BigDecimal startX, BigDecimal startY) {
        this.cell = cell
        this.startX = startX
        this.startY = startY
        this.pdfDocument = pdfDocument
        Table table = cell.parent.parent
        BigDecimal renderWidth = cell.width - (table.padding + table.padding)
        BigDecimal childStartX = startX + table.padding
        BigDecimal childStartY = startY + table.padding
        cell.children.each { child ->
            if (child instanceof TextBlock) {
                childRenderers << new ParagraphRenderer(child, pdfDocument, childStartX, childStartY, renderWidth)
            } else if (child instanceof Line) {
                childRenderers << new LineRenderer(child, pdfDocument, childStartX, childStartY)
            } else if (child instanceof Table) {
                childRenderers << new TableRenderer(child, pdfDocument, childStartX, childStartY)
            }
        }
    }

    BigDecimal getRowspanHeight() {
        cell.rowspanHeight + currentRowHeight
    }

    BigDecimal getPadding() {
        cell.parent.parent.padding
    }

    @Override
    Boolean getFullyParsed() {
        if (cell.rowspan > 1 && !onLastRowspanRow) {
            return true
        }
        childRenderers.every { it.fullyParsed }
    }

    @Override
    BigDecimal getTotalHeight() {
        (childRenderers*.totalHeight.sum() as BigDecimal ?: 0) + (padding * 2)
    }

    @Override
    BigDecimal getParsedHeight() {
        if (!childRenderers || !onLastRowspanRow) {
            return 0
        }
        BigDecimal parsedHeight = (childRenderers*.parsedHeight.sum() as BigDecimal) ?: 0
        if (onFirstPage && parsedHeight) {
            parsedHeight += padding
        }
        if (fullyParsed) {
            parsedHeight += padding
        }
        if (cell.rowspan > 1) {
            parsedHeight -= cell.rowspanHeight
        }
        parsedHeight
    }

    @Override
    void renderElement(BigDecimal startX, BigDecimal startY) {
        BigDecimal childX = startX
        BigDecimal childY = startY
        if (cell.rowspan > 1) {
            childY -= cell.rowspanHeight
        }
        if (onFirstPage) {
            childY += padding
        }
        if (onLastRowspanRow) {
            childRenderers*.render(childX, childY)
        }
        else {
            cell.rowspanHeight += currentRowHeight
            currentRowHeight = 0
        }
        renderedHeight = parsedHeight
    }

    @Override
    void parse(BigDecimal height) {
        if (height < 0) {
            return
        }
        childRenderers*.parse(height - padding)
    }

    Boolean isOnLastRowspanRow() {
        (cell.rowspan == 1) || (cell.rowsSpanned == (cell.rowspan - 1))
    }

}
