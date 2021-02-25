package org.xbib.graphics.pdfbox.groovy.render

import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Row
import org.xbib.graphics.pdfbox.groovy.Table
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument

class RowRenderer implements Renderable {

    Row row

    List<CellRenderer> cellRenderers = []

    BigDecimal renderedHeight = 0

    RowRenderer(Row row, PdfDocument pdfDocument, BigDecimal startX, BigDecimal startY) {
        this.row = row
        this.startX = startX
        this.startY = startY
        this.pdfDocument = pdfDocument
        Table table = row.parent
        BigDecimal columnX = startX + table.border.size
        BigDecimal columnY = startY + table.border.size
        row.children.each { Cell column ->
            cellRenderers << new CellRenderer(column, pdfDocument, columnX, columnY)
            columnX += column.width + table.border.size
        }
    }

    @Override
    void parse(BigDecimal height) {
        cellRenderers*.parse(height)
        cellRenderers*.currentRowHeight = parsedHeight
    }

    @Override
    Boolean getFullyParsed() {
        cellRenderers.every { it.fullyParsed }
    }

    @Override
    BigDecimal getTotalHeight() {
        cellRenderers*.totalHeight.max() + table.border.size
    }

    @Override
    BigDecimal getParsedHeight() {
        BigDecimal parsedHeight = cellRenderers*.parsedHeight.max() as BigDecimal ?: 0
        if (fullyParsed && parsedHeight > 0) {
            parsedHeight += table.border.size
        }
        parsedHeight
    }

    @Override
    void renderElement(BigDecimal startX, BigDecimal startY) {
        if (parsedHeight == 0) {
            return
        }
        renderBackgrounds(startX, startY)
        renderBorders(startX, startY)
        cellRenderers*.render(startX, startY)
        renderedHeight = parsedHeight
    }

    private Table getTable() {
        row.parent
    }

    BigDecimal getTableBorderOffset() {
        table.border.size / 2
    }

    private void renderBackgrounds(BigDecimal startX, BigDecimal startY) {
        BigDecimal backgroundStartY = startY + parsedHeight
        if (!firstRow) {
            backgroundStartY += tableBorderOffset
        }
        if (!fullyParsed) {
            backgroundStartY -= table.border.size
        }
        BigDecimal translatedStartY = pdfDocument.translateY(backgroundStartY)
        PDPageContentStream contentStream = pdfDocument.contentStream
        cellRenderers.each { CellRenderer columnElement ->
            Cell column = columnElement.cell
            if (column.background) {
                Boolean isLastColumn = (column == column.parent.children.last())
                contentStream.setNonStrokingColor(*column.background.rgb)
                startX = columnElement.startX - tableBorderOffset
                BigDecimal width = column.width + (isLastColumn ? table.border.size : tableBorderOffset)
                BigDecimal height = parsedHeight - (fullyParsed ? 0 : tableBorderOffset)
                height += ((fullyParsed && !onFirstPage) ? table.border.size : 0)
                contentStream.addRect(startX as float, translatedStartY as float,
                        width as float, height as float)
                contentStream.fill()
            }
        }
    }

    private void renderBorders(BigDecimal startX, BigDecimal startY) {
        if (!table.border.size) {
            return
        }
        BigDecimal translatedYTop = pdfDocument.translateY(startY - tableBorderOffset)
        BigDecimal translatedYBottom = pdfDocument.translateY(startY + parsedHeight)
        BigDecimal rowStartX = startX - tableBorderOffset
        BigDecimal rowEndX = startX + table.width
        PDPageContentStream contentStream = pdfDocument.contentStream
        def borderColor = table.border.color.rgb
        contentStream.setStrokingColor(*borderColor)
        contentStream.setLineWidth(table.border.size)
        if (firstRow || isTopOfPage(startY)) {
            contentStream.moveTo(rowStartX as float, translatedYTop as float)
            contentStream.lineTo(rowEndX as float, translatedYTop as float)
            contentStream.stroke()
        }
        cellRenderers.eachWithIndex { columnElement, i ->
            if (i == 0) {
                BigDecimal firstLineStartX = columnElement.startX - table.border.size
                contentStream.moveTo(firstLineStartX as float, translatedYTop as float)
                contentStream.lineTo(firstLineStartX as float, translatedYBottom as float)
                contentStream.stroke()
            }
            BigDecimal columnStartX = columnElement.startX - table.border.size
            BigDecimal columnEndX = columnElement.startX + columnElement.cell.width + tableBorderOffset
            contentStream.moveTo(columnEndX as float, translatedYTop as float)
            contentStream.lineTo(columnEndX as float, translatedYBottom as float)
            contentStream.stroke()
            if (fullyParsed && columnElement.onLastRowspanRow) {
                contentStream.moveTo(columnStartX as float, translatedYBottom as float)
                contentStream.lineTo(columnEndX as float, translatedYBottom as float)
                contentStream.stroke()
            }
        }
    }

    Boolean isTopOfPage(BigDecimal y) {
        (y as int) == pdfDocument.document.margin.top
    }

    Boolean isFirstRow() {
        row == row.parent.children.first()
    }
}
