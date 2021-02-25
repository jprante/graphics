package org.xbib.graphics.pdfbox.groovy.test

import groovy.util.logging.Log4j2
import org.junit.Test
import org.xbib.graphics.pdfbox.groovy.analyze.DocumentAnalyzer

@Log4j2
class DocumentAnalyzerTest {

    @Test
    void analyze() {
        InputStream inputStream = getClass().getResourceAsStream("/ghost.pdf")
        if (inputStream) {
            DocumentAnalyzer documentAnalyzer = new DocumentAnalyzer(inputStream)
            log.info(documentAnalyzer.result)
        }
    }

    @Test(expected = IOException.class)
    void analyzeNonPDF() {
        InputStream inputStream = getClass().getResourceAsStream("/log4j2-test.xml")
        if (inputStream) {
            DocumentAnalyzer documentAnalyzer = new DocumentAnalyzer(inputStream)
            log.info(documentAnalyzer.result)
        }
    }

}
