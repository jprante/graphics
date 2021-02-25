package org.xbib.graphics.pdfbox.groovy.factory

class CreateFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        [:]
    }


    boolean isLeaf() {
        false
    }

    void setChild(FactoryBuilderSupport builder, parent, child) {
        parent.document = child
    }

}
