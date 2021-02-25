package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Heading
import org.xbib.graphics.pdfbox.groovy.Text

class HeadingFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Heading heading = new Heading(attributes)
        heading.level = Integer.valueOf(builder.currentName - 'heading')
        heading.parent = builder.document
        builder.setNodeProperties(heading, attributes, 'heading')
        Text text = new Text(value: value, parent: heading)
        heading.children << text
        builder.setNodeProperties(text, [:], 'text')
        heading
    }

    void onNodeCompleted(FactoryBuilderSupport builder, parent, child) {
        if (builder.onTextBlockComplete) {
            builder.onTextBlockComplete(child)
        }
    }

    boolean isLeaf() { false }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) { false }

}
