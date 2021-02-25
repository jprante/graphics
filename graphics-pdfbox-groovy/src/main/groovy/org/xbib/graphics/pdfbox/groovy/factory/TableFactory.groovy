package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.Table
import org.xbib.graphics.pdfbox.groovy.builder.RenderState

class TableFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Table table = new Table(attributes)
        table.parent = builder.parentName == 'create' ? builder.document : builder.current
        if (table.parent instanceof Cell) {
            table.parent.children << table
        }
        builder.setNodeProperties(table, attributes, 'table')
        table
    }

    void setChild(FactoryBuilderSupport builder, table, row) {
        table.children << row
    }

    void onNodeCompleted(FactoryBuilderSupport builder, parent, table) {
        if (parent instanceof Document || builder.renderState != RenderState.PAGE) {
            table.normalizeColumnWidths()
        }
        if (builder.onTableComplete) {
            builder.onTableComplete(table)
        }
    }

    boolean isLeaf() { false }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) { false }

}
