package org.xbib.graphics.pdfbox.groovy.test

import groovy.transform.InheritConstructors
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.builder.DocumentBuilder

@InheritConstructors
class TestBuilder extends DocumentBuilder {

    @Override
    void initializeDocument(Document document) { }

    @Override
    void writeDocument(Document document) { }
}
