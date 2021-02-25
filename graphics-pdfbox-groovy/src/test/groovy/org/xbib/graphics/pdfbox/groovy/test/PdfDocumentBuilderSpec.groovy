package org.xbib.graphics.pdfbox.groovy.test

import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.builder.DocumentBuilder
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocumentBuilder

class PdfDocumentBuilderSpec extends BaseBuilderSpec {

    DocumentBuilder getBuilderInstance(OutputStream out) {
        new PdfDocumentBuilder(out)
    }

    Document getDocument(byte[] data) {
        PdfDocumentLoader.load(data)
    }
}
