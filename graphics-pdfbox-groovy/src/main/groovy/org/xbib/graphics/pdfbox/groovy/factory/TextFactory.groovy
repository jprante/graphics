package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Text
import org.xbib.graphics.pdfbox.groovy.TextBlock

class TextFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        TextBlock paragraph
        if (builder.parentName == 'paragraph') {
            paragraph = builder.current as TextBlock
        } else {
            paragraph = builder.getColumnParagraph(builder.current)
        }
        List elements = paragraph.addText(value.toString())
        elements.each { node ->
            node.parent = paragraph
            if (node instanceof Text) {
                node.url = attributes.url
                node.style = attributes.style
                builder.setNodeProperties(node, attributes, 'text')
            }
        }
        elements
    }

    boolean isLeaf() { true }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) { false }
}
