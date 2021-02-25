package org.xbib.graphics.pdfbox.groovy.test

import org.apache.pdfbox.pdmodel.PDDocument
import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.Image
import org.xbib.graphics.pdfbox.groovy.Row
import org.xbib.graphics.pdfbox.groovy.Table
import org.xbib.graphics.pdfbox.groovy.TextBlock

class PdfDocumentLoader {

    static Document load(byte[] data) {
        PDDocument pdfDoc = PDDocument.load(new ByteArrayInputStream(data))
        Document document = new Document(element: pdfDoc)
        def metaData = new XmlParser().parse(pdfDoc.documentCatalog.metadata.createInputStream())
        document.margin.top = metaData.'@marginTop' as Integer
        document.margin.bottom = metaData.'@marginBottom' as Integer
        document.margin.left = metaData.'@marginLeft' as Integer
        document.margin.right = metaData.'@marginRight' as Integer
        metaData.each {
            if (it.name() == 'paragraph') {
                loadParagraph(document, it)
            } else if (it.name() == 'table') {
                loadTable(document, it)
            } else {
                throw new IOException('unknown metadata name ' + it.name())
            }
        }
        def extractor = new PdfContentExtractor(document)
        extractor.processPages(pdfDoc.getPages())
        pdfDoc.close()
        document
    }

    private static loadParagraph(Document document, paragraphNode) {
        def paragraph = new TextBlock(parent: document)
        paragraph.margin.top = paragraphNode.'@marginTop' as Integer
        paragraph.margin.bottom = paragraphNode.'@marginBottom' as Integer
        paragraph.margin.left = paragraphNode.'@marginLeft' as Integer
        paragraph.margin.right = paragraphNode.'@marginRight' as Integer
        paragraphNode.image.each {
            paragraph.children << new Image(parent: paragraph)
        }
        document.children << paragraph
    }

    private static loadTable(Document document, tableNode) {
        def table = new Table(parent: document, width: tableNode.'@width' as Integer)
        tableNode.row.each { rowNode ->
            Row row = new Row()
            rowNode.cell.each { cellNode ->
                def cell = new Cell(width: cellNode.'@width' as Integer)
                cell.children << new TextBlock()
                row.children << cell
            }
            table.children << row
        }
        document.children << table
    }

}
