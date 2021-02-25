package org.xbib.graphics.pdfbox.groovy

class BlockNode extends BaseNode implements Stylable, Alignable {

    static Margin defaultMargin = new Margin(top: 0, bottom: 0, left: 0, right: 0)

    Margin margin = new Margin()

    Border border = new Border()
}
