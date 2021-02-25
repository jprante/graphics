package org.xbib.graphics.pdfbox.groovy

enum Align {
    LEFT('left'),
    RIGHT('right'),
    CENTER('center')

    String value

    Align(String value) {
        this.value = value
    }
}
