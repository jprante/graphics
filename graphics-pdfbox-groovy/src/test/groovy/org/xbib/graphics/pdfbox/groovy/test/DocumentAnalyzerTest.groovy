package org.xbib.graphics.pdfbox.groovy.test

import groovy.util.logging.Log
import org.junit.Test
import org.xbib.graphics.pdfbox.groovy.analyze.DocumentAnalyzer

@Log
class DocumentAnalyzerTest {

    @Test
    void analyze() {
        InputStream inputStream = getClass().getResourceAsStream("/ghost.pdf")
        if (inputStream) {
            DocumentAnalyzer documentAnalyzer = new DocumentAnalyzer(inputStream)
            log.info(documentAnalyzer.result as String)
        }
    }

    @Test(expected = IOException.class)
    void analyzeNonPDF() {
        InputStream inputStream = getClass().getResourceAsStream("/logging.properties")
        if (inputStream) {
            DocumentAnalyzer documentAnalyzer = new DocumentAnalyzer(inputStream)
            log.info(documentAnalyzer.result as String)
        }
    }
}
