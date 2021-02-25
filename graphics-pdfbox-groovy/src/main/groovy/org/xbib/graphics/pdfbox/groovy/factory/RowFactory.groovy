package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Row

class RowFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Row row = new Row(attributes)
        row.parent = builder.current
        builder.setNodeProperties(row, attributes, 'row')
        row
    }

    void setChild(FactoryBuilderSupport builder, row, column) {
        column.parent = row
        row.children << column
    }

    boolean isLeaf() { false }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) { false }

    void onNodeCompleted(FactoryBuilderSupport builder, parent, child) {
        if (builder.onRowComplete) {
            builder.onRowComplete(child)
        }
    }

}
