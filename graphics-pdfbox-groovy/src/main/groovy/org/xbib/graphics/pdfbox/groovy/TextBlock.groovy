package org.xbib.graphics.pdfbox.groovy

import groovy.transform.AutoClone

@AutoClone
class TextBlock extends BlockNode implements Linkable, Bookmarkable {

    BigDecimal heightfactor

    List children = []

    String getText() {
        children.findAll { it.getClass() == Text }*.value.join('')
    }

    List addText(String text) {
        List elements = []
        def textSections = text.split('\n')
        textSections.each { String section ->
            elements << new Text(value: section, parent: this)
            if (section != textSections.last()) {
                elements << new LineBreak(parent: this)
            }
        }
        if (text.endsWith('\n')) {
            elements << new LineBreak(parent: this)
        }
        children += elements
        elements
    }
}
