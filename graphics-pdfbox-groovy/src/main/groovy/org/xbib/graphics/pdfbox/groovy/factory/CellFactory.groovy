package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Row
import org.xbib.graphics.pdfbox.groovy.Text
import org.xbib.graphics.pdfbox.groovy.TextBlock

class CellFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Cell cell = new Cell(attributes)
        Row row = builder.current as Row
        cell.parent = row
        builder.setNodeProperties(cell, attributes, 'cell')
        if (value) {
            TextBlock paragraph = builder.getColumnParagraph(cell)
            List elements = paragraph.addText(value.toString())
            elements.each { node ->
                if (node instanceof Text) {
                    builder.setNodeProperties(node, [:], 'text')
                }
            }
        }
        cell
    }

    boolean isLeaf() { false }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) { false }

    void onNodeCompleted(FactoryBuilderSupport builder, parent, child) {
        if (builder.onCellComplete) {
            builder.onCellComplete(child)
        }
    }

}
