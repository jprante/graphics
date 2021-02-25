package org.xbib.graphics.pdfbox.groovy

class Font implements ColorAssignable, Cloneable {

    String family = 'Helvetica'

    BigDecimal size = 12

    boolean bold = false

    boolean italic = false

    def leftShift(Map properties) {
        properties?.each { key, value -> this[key] = value }
    }

    Object clone() {
        Font result = new Font(family: family, size: size, bold: bold, italic: italic)
        result.color = "#${color.hex}"
        result
    }
}
