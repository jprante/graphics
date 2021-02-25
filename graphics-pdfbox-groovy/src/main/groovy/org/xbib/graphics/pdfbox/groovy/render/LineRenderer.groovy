package org.xbib.graphics.pdfbox.groovy.render

import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.xbib.graphics.pdfbox.groovy.Line
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument

class LineRenderer implements Renderable {

    private Line line

    LineRenderer(Line line, PdfDocument pdfDocument, BigDecimal startX, BigDecimal startY) {
        this.line = line
        this.pdfDocument = pdfDocument
        this.startX = startX
        this.startY = startY
    }

    @Override
    void parse(BigDecimal maxHeight) {
    }

    @Override
    Boolean getFullyParsed() {
        true
    }

    @Override
    BigDecimal getTotalHeight() {
        line.strokewidth
    }

    @Override
    BigDecimal getParsedHeight() {
        line.strokewidth
    }

    @Override
    BigDecimal getRenderedHeight() {
        line.strokewidth
    }

    @Override
    void renderElement(BigDecimal startX, BigDecimal startY) {
        if (parsedHeight == 0d) {
            return
        }
        PDPageContentStream contentStream = pdfDocument.contentStream
        contentStream.setLineWidth(line.strokewidth)
        BigDecimal x1 = startX + line.startX
        BigDecimal y1 = startY + line.startY
        contentStream.moveTo(x1 as float, pdfDocument.translateY(y1) as float)
        BigDecimal x2 = startX + line.endX
        BigDecimal y2 = startY + line.endY
        contentStream.lineTo(x2 as float, pdfDocument.translateY(y2) as float)
        contentStream.stroke()
    }
}
