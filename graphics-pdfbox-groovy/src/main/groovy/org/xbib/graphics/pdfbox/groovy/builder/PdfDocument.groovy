package org.xbib.graphics.pdfbox.groovy.builder

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.xbib.graphics.pdfbox.groovy.Document

/**
 *
 */
class PdfDocument implements Closeable {

    BigDecimal x = 0

    BigDecimal y = 0

    Document document

    PDDocument pdDocument

    Integer pageNumber = 0

    PDPageContentStream contentStream

    List<PDPage> pages = []

    PdfDocument(Document document) {
        this.document = document
        this.pdDocument = new PDDocument()
        addPage()
    }

    void toStartPosition() {
        x = document.margin.left
        y = document.margin.top
    }

    int getPageBottomY() {
        currentPage.mediaBox.height - document.margin.bottom
    }

    void addPage() {
        PDRectangle papersize
        switch (document.papersize) {
            case 'A0':
                papersize = PDRectangle.A0
                break
            case 'A1':
                papersize = PDRectangle.A1
                break
            case 'A2':
                papersize = PDRectangle.A2
                break
            case 'A3':
                papersize = PDRectangle.A3
                break
            case 'A4':
                papersize = PDRectangle.A4
                break
            case 'A5':
                papersize = PDRectangle.A5
                break
            case 'A6':
                papersize = PDRectangle.A6
                break
            case 'LETTER':
                papersize = PDRectangle.LETTER
                break
            case 'LEGAL':
                papersize = PDRectangle.LEGAL
                break
            default:
                papersize = PDRectangle.A4
                break
        }
        if (document.orientation == 'landscape') {
                papersize = swapOrientation(papersize)
        }
        document.width = papersize.width as int
        document.height = papersize.height as int
        def newPage = new PDPage(papersize)
        pages << newPage
        pageNumber++
        contentStream?.close()
        contentStream = new PDPageContentStream(pdDocument, currentPage)
        toStartPosition()
        pdDocument.addPage(newPage)
    }

    static PDRectangle swapOrientation(PDRectangle pdRectangle) {
        new PDRectangle(pdRectangle.height, pdRectangle.width)
    }

    PDPage getCurrentPage() {
        pages[pageNumber - 1]
    }

    void setPageNumber(int value) {
        this.pageNumber = value
        contentStream?.close()
        contentStream = new PDPageContentStream(pdDocument, currentPage, true, true)
        toStartPosition()
    }

    BigDecimal getTranslatedY() {
        currentPage.mediaBox.height - y
    }

    void scrollDownPage(BigDecimal amount) {
        if (remainingPageHeight < amount) {
            BigDecimal amountDiff = amount - remainingPageHeight
            addPage()
            y += amountDiff
        }
        else {
            y += amount
        }
    }

    BigDecimal translateY(Number value) {
        currentPage.mediaBox.height - value
    }

    BigDecimal getRemainingPageHeight() {
        (currentPage.mediaBox.height - document.margin.bottom) - y
    }

    @Override
    void close() {
        pdDocument.close()
    }
}
