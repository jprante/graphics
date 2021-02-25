package org.xbib.graphics.pdfbox.groovy.render.element

import org.apache.pdfbox.pdmodel.font.PDFont
import org.xbib.graphics.pdfbox.groovy.Text

class TextElement {

    PDFont pdfFont

    Text node

    String text

    int width

    BigDecimal heightfactor
}
