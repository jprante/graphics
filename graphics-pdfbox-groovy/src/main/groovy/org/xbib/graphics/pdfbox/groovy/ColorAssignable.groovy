package org.xbib.graphics.pdfbox.groovy

trait ColorAssignable {
    Color color = new Color()

    void setColor(String value) {
        color.color = value
    }
}
