package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.PageBreak

class PageBreakFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        PageBreak pageBreak = new PageBreak()
        pageBreak.parent = builder.document
        if (builder.addPageBreakToDocument) {
            builder.addPageBreakToDocument(pageBreak, builder.document)
        }
        pageBreak
    }

    boolean isLeaf() { true }
}
