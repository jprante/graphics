package org.xbib.graphics.pdfbox.groovy.render

import org.xbib.graphics.pdfbox.groovy.Row
import org.xbib.graphics.pdfbox.groovy.Table
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument

class TableRenderer implements Renderable {

    Table table

    List<RowRenderer> rowRenderers = []

    BigDecimal renderedHeight = 0

    int parseStart = 0

    int parseEnd = 0

    private boolean parsedAndRendered = false

    TableRenderer(Table table, PdfDocument pdfDocument, BigDecimal startX, BigDecimal startY) {
        this.startX = startX
        this.startY = startY
        this.pdfDocument = pdfDocument
        this.table = table
        table.children.each { Row row ->
            rowRenderers << new RowRenderer(row, pdfDocument, startX, startY)
        }
    }

    @Override
    void parse(BigDecimal height) {
        if (!rowRenderers) {
            return
        }
        if (!parsedAndRendered) {
            parseEnd = parseStart
        }
        boolean reachedEnd = false
        BigDecimal remainingHeight = height - (onFirstPage ? table.border.size : 0)
        while (!reachedEnd) {
            RowRenderer currentRenderer = rowRenderers[parseEnd]
            currentRenderer.parse(remainingHeight)
            remainingHeight -= currentRenderer.parsedHeight
            if (currentRenderer.parsedHeight == 0) {
                reachedEnd = true
            }
            if (remainingHeight < 0) {
                currentRenderer.parse(0 as BigDecimal)
                parseEnd = Math.max(0, parseEnd - 1)
                reachedEnd = true
            } else if (remainingHeight == 0) {
                reachedEnd = true
            } else if (currentRenderer == rowRenderers.last()) {
                reachedEnd = true
            }
            if (!reachedEnd && currentRenderer.fullyParsed) {
                parseEnd++
            }
        }
        if (parseEnd >= rowRenderers.size()) {
            parseEnd = rowRenderers.size() - 1
        }
        if (parseEnd < parseStart) {
            parseEnd = parseStart
        }
        parsedAndRendered = false
    }

    @Override
    Boolean getFullyParsed() {
        rowRenderers ? rowRenderers.every { it.fullyParsed } : true
    }

    @Override
    BigDecimal getTotalHeight() {
        (rowRenderers*.totalHeight.sum() ?: 0) + table.border.size
    }

    @Override
    BigDecimal getParsedHeight() {
        (rowRenderers[parseStart..parseEnd]*.parsedHeight.sum() as BigDecimal?: 0) +
                (onFirstPage ? table.border.size : 0)
    }

    @Override
    void renderElement(BigDecimal startX, BigDecimal startY) {
        if (parsedAndRendered) {
            return
        }
        BigDecimal rowStartX = startX
        BigDecimal rowStartY = startY
        Boolean lastRowRendered = false
        rowRenderers[parseStart..parseEnd].each {
            it.render(rowStartX, rowStartY)
            rowStartY += it.parsedHeight
            lastRowRendered = it.fullyParsed
            if (lastRowRendered) {
                it.cellRenderers.each { it.cell.rowsSpanned++ }
            }
        }
        renderedHeight = parsedHeight
        if (lastRowRendered) {
            parseStart = Math.min(rowRenderers.size() - 1, parseEnd + 1)
            parseEnd = parseStart
        }
        else {
            parseStart = parseEnd
        }
        parsedAndRendered = true
    }
}
