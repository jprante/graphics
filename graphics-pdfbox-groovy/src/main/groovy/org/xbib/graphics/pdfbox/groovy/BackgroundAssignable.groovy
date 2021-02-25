package org.xbib.graphics.pdfbox.groovy

trait BackgroundAssignable {
    Color background
    
    void setBackground(String value) {
        if (value) {
            background = background ?: new Color()
            background.color = value
        }
    }
}
