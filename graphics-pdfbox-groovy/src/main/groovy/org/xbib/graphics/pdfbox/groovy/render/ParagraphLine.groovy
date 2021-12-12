package org.xbib.graphics.pdfbox.groovy.render

import org.xbib.graphics.pdfbox.groovy.TextBlock
import org.xbib.graphics.pdfbox.groovy.render.element.ImageElement
import org.xbib.graphics.pdfbox.groovy.render.element.TextElement

class ParagraphLine {

    final BigDecimal maxWidth

    BigDecimal contentWidth = 0

    TextBlock paragraph

    List<Renderable> elements = []

    ParagraphLine(TextBlock paragraph, BigDecimal maxWidth) {
        this.paragraph = paragraph
        this.maxWidth = maxWidth
    }

    BigDecimal getRemainingWidth() {
        maxWidth - contentWidth
    }

    BigDecimal getTotalHeight() {
        getContentHeight()
    }

    BigDecimal getContentHeight() {
        elements.collect {
            if (it instanceof TextElement) {
                it.node.font.size * it.heightfactor
            } else if (it instanceof ImageElement) {
                it.node.height
            } else {
                0
            }
        }.max() as BigDecimal ?: paragraph.font.size
    }
}
