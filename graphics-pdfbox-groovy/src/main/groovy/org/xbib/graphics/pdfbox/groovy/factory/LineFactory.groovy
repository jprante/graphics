package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Line

class LineFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Line line  = new Line(attributes)
        line.parent = builder.parentName == 'create' ? builder.document : builder.current
        if (builder.parentName == 'paragraph' || builder.parentName == 'cell') {
            line.parent.children << line
        }
        line
    }

    void onNodeCompleted(FactoryBuilderSupport builder, parent, line) {
        if (builder.onLineComplete) {
            builder.onLineComplete(line)
        }
    }

    boolean isLeaf() { true }
}
