package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Document

class DocumentFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Document document = new Document(attributes)
        builder.document = document
        builder.setNodeProperties(document, attributes, 'document')
        builder.initializeDocument(document)
        document
    }

    void setChild(FactoryBuilderSupport builder, parent, child) {
        parent.children << child
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        builder.writeDocument(builder.document)
    }

    boolean isLeaf() { false }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) { false }

}
