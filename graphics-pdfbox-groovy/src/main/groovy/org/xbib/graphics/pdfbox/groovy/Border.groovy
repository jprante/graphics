package org.xbib.graphics.pdfbox.groovy

class Border implements ColorAssignable {
    Integer size = 1

    def leftShift(Map properties) {
        properties?.each { key, value -> this[key] = value }
    }
}
