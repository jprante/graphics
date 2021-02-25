package org.xbib.graphics.pdfbox.groovy.render

import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument

trait Renderable {

    BigDecimal startX

    BigDecimal startY

    PdfDocument pdfDocument
    
    abstract void parse(BigDecimal maxHeight)

    abstract Boolean getFullyParsed()

    abstract BigDecimal getTotalHeight()

    abstract BigDecimal getParsedHeight()

    abstract BigDecimal getRenderedHeight()

    abstract void renderElement(BigDecimal startX, BigDecimal startY)

    int renderCount = 0
    
    void render(BigDecimal startX, BigDecimal startY) {
        BigDecimal currentX = pdfDocument.x
        BigDecimal currentY = pdfDocument.y
        pdfDocument.y = startY
        pdfDocument.x = startX
        renderElement(startX, startY)
        pdfDocument.x = currentX
        pdfDocument.y = currentY
        renderCount = renderCount + 1
    }

    Boolean getOnFirstPage() {
        renderCount <= 1
    }
}
