package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Barcode
import org.xbib.graphics.pdfbox.groovy.TextBlock

class BarcodeFactory extends AbstractFactory {

    @Override
    Object newInstance(FactoryBuilderSupport builder, name, value, Map attributes)
            throws InstantiationException, IllegalAccessException {
        Barcode barcode = new Barcode(attributes)
        TextBlock paragraph
        if (builder.parentName == 'paragraph') {
            paragraph = builder.current as TextBlock
        } else {
            paragraph = builder.getColumnParagraph(builder.current)
        }
        barcode.parent = paragraph
        paragraph.children << barcode
        barcode
    }

    @Override
    boolean isLeaf() {
        true
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) {
        false
    }
}
