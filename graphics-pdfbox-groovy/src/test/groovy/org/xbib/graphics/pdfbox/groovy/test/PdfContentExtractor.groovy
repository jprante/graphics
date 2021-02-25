package org.xbib.graphics.pdfbox.groovy.test

import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageTree
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.Font
import org.xbib.graphics.pdfbox.groovy.Text
import org.xbib.graphics.pdfbox.groovy.TextBlock

class PdfContentExtractor extends PDFTextStripper {

    private tablePosition = [row: 0, cell: 0]
    private int currentChildNumber = 0
    private Document doc
    private TextPosition lastPosition
    private PDRectangle pageSize

    PdfContentExtractor(Document doc) {
        super.setSortByPosition(true)
        this.doc = doc
        this.document = doc.element
    }

    private getCurrentChild() {
        if (!doc.children || doc.children.size() < currentChildNumber) {
            null
        } else {
            doc.children[currentChildNumber - 1]
        }
    }

    @Override
    protected void processPages(PDPageTree pages) throws IOException {
        super.processPages(pages)
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        this.pageSize = page.getCropBox()
        super.processPage(page)
    }

    @Override
    protected void writePage() throws IOException {
        // disabled
    }

    @Override
    void processTextPosition(TextPosition text) {
        updateChildNumber(text)
        Font currentFont = new Font(family: text.font.fontDescriptor.fontFamily, size: text.fontSize)
        def textNode
        if (currentChild.getClass() == TextBlock) {
            textNode = processParagraph(text, currentFont)
        } else {
            textNode = processTable(text, currentFont)
        }
        textNode?.value += text.unicode
        lastPosition = text
    }

    private processTable(TextPosition text, Font font) {
        def textNode
        Cell cell = currentChild.children[tablePosition.row].children[tablePosition.cell]
        TextBlock paragraph = cell.children[0]
        paragraph.font = paragraph.font ?: font
        if (!paragraph.children || isNewSection(text)) {
            textNode = getText(paragraph, font)
            paragraph.children << textNode
        } else {
            textNode = paragraph.children.last()
        }
        textNode
    }

    private processParagraph(TextPosition text, Font font) {
        def textNode
        if (!currentChild.children) {
            textNode = getText(currentChild, font)
            currentChild.children << textNode
            setParagraphProperties(currentChild, text, font)
        } else if (isNewSection(text)) {
            textNode = getText(currentChild, font)
            currentChild.children << textNode
        } else {
            textNode = currentChild.children.last()
        }
        textNode
    }

    private void setParagraphProperties(paragraph, TextPosition text, Font font) {
        paragraph.font = font.clone()
        paragraph.margin.left = text.x - doc.margin.left
        int totalPageWidth = pageSize.getWidth() - doc.margin.right - doc.margin.left
        paragraph.margin.right = totalPageWidth - text.width - paragraph.margin.left
        int topMargin = Math.ceil(text.y - doc.margin.top)
        paragraph.margin.top = Math.round(topMargin)
    }

    private Text getText(paragraph, Font font) {
        new Text(parent: paragraph, value: '', font: font)
    }

    private void updateChildNumber(TextPosition current) {
        if (!lastPosition || (lastPosition.y != current.y)) {
            currentChildNumber++
            tablePosition.row = 0
            tablePosition.cell = 0
        }
    }

    private boolean isNewSection(TextPosition current) {
        boolean isNewSection = false
        if (!lastPosition) {
            isNewSection = true
        } else if (current.font != lastPosition.font) {
            isNewSection = true
        } else if (current.fontSizeInPt != lastPosition.fontSizeInPt) {
            isNewSection = true
        }
        isNewSection
    }

}
