package org.xbib.graphics.pdfbox.groovy

class Heading extends TextBlock implements Linkable {

    static final FONT_SIZE_MULTIPLIERS = [2, 1.5, 1.17, 1.12, 0.83, 0.75]

    int level = 1
}
