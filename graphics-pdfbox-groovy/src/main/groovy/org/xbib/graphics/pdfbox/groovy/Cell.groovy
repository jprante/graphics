package org.xbib.graphics.pdfbox.groovy

class Cell extends BlockNode implements Stylable, Alignable, BackgroundAssignable {

    List children = []

    Integer width = 0

    Integer colspan = 1

    Integer rowspan = 1

    Integer rowsSpanned = 0

    BigDecimal rowspanHeight = 0
}
