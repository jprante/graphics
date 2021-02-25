package org.xbib.graphics.pdfbox.groovy.test

import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.Font
import org.xbib.graphics.pdfbox.groovy.Margin
import org.xbib.graphics.pdfbox.groovy.Row
import org.xbib.graphics.pdfbox.groovy.Table
import org.xbib.graphics.pdfbox.groovy.TextBlock
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument
import org.xbib.graphics.pdfbox.groovy.render.TableRenderer
import spock.lang.Shared

class TableRendererSpec extends RendererTestBase {

    @Shared Table table

    @Shared TableRenderer tableRenderer

    @Shared BigDecimal defaultRowHeight

    @Shared int rowCount = 2

    def setup() {
        table = new Table(margin: Margin.NONE, padding:20, border:[size:3], columns:[1])
        TextBlock paragraph = makeParagraph(5)
        paragraph.margin = Margin.NONE
        tableRenderer = makeTableElement(table, paragraph, rowCount)
        defaultRowHeight = (defaultLineHeight * 5) + (table.padding * 2) + (table.border.size)
    }

    def cleanup() {
        tableRenderer.pdfDocument.close()
    }

    def "parse first row"() {
        BigDecimal firstRowHeight = defaultRowHeight + table.border.size

        when:
        tableRenderer.parse(firstRowHeight)

        then:
        tableRenderer.parsedHeight == firstRowHeight

        and:
        tableRenderer.parseStart == 0

        and:
        tableRenderer.parseEnd == 0
    }

    def "parse part of first row"() {
        BigDecimal partialRowHeight = table.padding + (defaultLineHeight * 3) + table.border.size

        when:
        tableRenderer.parse(partialRowHeight)

        then:
        tableRenderer.parseStart == 0

        and:
        tableRenderer.parseEnd == 0

        and:
        tableRenderer.parsedHeight == partialRowHeight
    }

    def "parse all rows"() {
        BigDecimal totalHeight = (rowCount * defaultRowHeight) + table.border.size

        when:
        tableRenderer.parse(totalHeight)

        then:
        tableRenderer.parsedHeight == totalHeight

        and:
        tableRenderer.fullyParsed
    }

    private TableRenderer makeTableElement(Table table, TextBlock paragraph, int rows) {
        Document tableDocument = makeDocument()
        table.parent = tableDocument
        int cellCount = table.columns.size()
        rows.times {
            Row row = new Row(font:new Font())
            row.parent = table
            table.children << row
            cellCount.times {
                Cell cell = new Cell(font:new Font())
                row.children << cell
                cell.parent = row
                makeParagraph(paragraph, cell)
            }
        }
        table.updateRowspanColumns()
        table.normalizeColumnWidths()
        PdfDocument pdfDocument = new PdfDocument(tableDocument)
        new TableRenderer(table, pdfDocument, 0 as BigDecimal, 0 as BigDecimal)
    }
}
