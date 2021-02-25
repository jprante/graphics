package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Align
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.Text
import org.xbib.graphics.pdfbox.groovy.TextBlock

class ParagraphFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        TextBlock paragraph = new TextBlock(attributes)
        paragraph.parent = builder.parentName == 'create' ? builder.document : builder.current
        builder.setNodeProperties(paragraph, attributes, 'paragraph')
        if (paragraph.parent instanceof Document) {
            paragraph.align = paragraph.align ?: Align.LEFT
        }
        if (value) {
            List elements = paragraph.addText(value.toString())
            elements.each { node ->
                if (node instanceof Text) {
                    builder.setNodeProperties(node, [:], 'text')
                }
            }
        }
        paragraph
    }

    boolean isLeaf() { false }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) { false }

    void onNodeCompleted(FactoryBuilderSupport builder, parent, child) {
        if (builder.onTextBlockComplete) {
            builder.onTextBlockComplete(child)
        }
    }

}
