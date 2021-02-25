package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.LineBreak
import org.xbib.graphics.pdfbox.groovy.TextBlock

class LineBreakFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        LineBreak lineBreak = new LineBreak()
        TextBlock paragraph
        if (builder.parentName == 'paragraph') {
            paragraph = builder.current as TextBlock
        } else {
            paragraph = builder.getColumnParagraph(builder.current)
        }
        lineBreak.parent = paragraph
        paragraph.children << lineBreak
        lineBreak
    }

    boolean isLeaf() { true }
}
