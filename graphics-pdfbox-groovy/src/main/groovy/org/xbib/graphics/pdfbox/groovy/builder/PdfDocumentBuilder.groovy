package org.xbib.graphics.pdfbox.groovy.builder

import groovy.transform.InheritConstructors
import groovy.util.logging.Log
import groovy.xml.MarkupBuilder
import org.apache.pdfbox.pdmodel.common.PDMetadata
import org.xbib.graphics.pdfbox.groovy.Barcode
import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.HeaderFooterOptions
import org.xbib.graphics.pdfbox.groovy.Image
import org.xbib.graphics.pdfbox.groovy.Line
import org.xbib.graphics.pdfbox.groovy.PageBreak
import org.xbib.graphics.pdfbox.groovy.Row
import org.xbib.graphics.pdfbox.groovy.Table
import org.xbib.graphics.pdfbox.groovy.TextBlock
import org.xbib.graphics.pdfbox.groovy.render.ParagraphRenderer
import org.xbib.graphics.pdfbox.groovy.render.TableRenderer

@Log
@InheritConstructors
class PdfDocumentBuilder extends DocumentBuilder {

    PdfDocument pdfDocument

    @Override
    void initializeDocument(Document document) {
        pdfDocument = new PdfDocument(document)
        pdfDocument.x = document.margin.left
        pdfDocument.y = document.margin.top
        document.element = pdfDocument
        if (fontDefs) {
            fontDefs.each { fd ->
                String s = fd.resource
                s = s && s.startsWith('/') ? s : '/' + s
                InputStream inputStream = getClass().getResourceAsStream(s)
                if (inputStream) {
                    inputStream.withCloseable {
                        boolean loaded = PdfFont.addFont(pdfDocument.pdDocument, fd.name, inputStream, fd.bold, fd.italic)
                        if (!loaded) {
                            log.warning("font ${fd.name} not loaded")
                        } else {
                            log.info("font ${fd.name} added")
                        }
                    }
                } else {
                    log.warning("font ${fd.name} not found in class path")
                }
            }
        }
    }

    @Override
    void writeDocument(Document document) {
        addHeaderFooter()
        addMetadata()
        pdfDocument.contentStream?.close()
        pdfDocument.pdDocument.save(getOutputStream())
        pdfDocument.pdDocument.close()
    }

    def addPageBreakToDocument = { PageBreak pageBreak, Document document ->
        pdfDocument.addPage()
    }

    def onTextBlockComplete = { TextBlock paragraph ->
        if (renderState == RenderState.PAGE && paragraph.parent instanceof Document) {
            int pageWidth = document.width - document.margin.left - document.margin.right
            int maxLineWidth = pageWidth - paragraph.margin.left - paragraph.margin.right
            int renderStartX = document.margin.left + paragraph.margin.left
            int renderStartY = paragraph.margin.top
            pdfDocument.x = renderStartX
            pdfDocument.scrollDownPage(renderStartY)
            ParagraphRenderer paragraphRenderer = new ParagraphRenderer(paragraph, pdfDocument, renderStartX, renderStartY, maxLineWidth)
            while (!paragraphRenderer.fullyParsed) {
                paragraphRenderer.parse(pdfDocument.remainingPageHeight)
                paragraphRenderer.render(pdfDocument.x, pdfDocument.y)
                if (paragraphRenderer.fullyParsed) {
                    pdfDocument.scrollDownPage(paragraphRenderer.renderedHeight)
                } else {
                    pdfDocument.addPage()
                }
            }
            pdfDocument.scrollDownPage(paragraph.margin.bottom)
        }
    }

    def onTableComplete = { Table table ->
        if (renderState == RenderState.PAGE) {
            pdfDocument.x = table.margin.left + document.margin.left
            //pdfDocument.y = table.margin.top + document.margin.top // TODO
            pdfDocument.scrollDownPage(table.margin.top)
            TableRenderer tableRenderer = new TableRenderer(table, pdfDocument, pdfDocument.x, pdfDocument.y)
            while (!tableRenderer.fullyParsed) {
                tableRenderer.parse(pdfDocument.remainingPageHeight)
                tableRenderer.render(pdfDocument.x, pdfDocument.y)
                if (tableRenderer.fullyParsed) {
                    pdfDocument.scrollDownPage(tableRenderer.renderedHeight)
                } else {
                    pdfDocument.addPage()
                }
            }
            pdfDocument.scrollDownPage(table.margin.bottom)
        }
    }

    def onLineComplete = { Line line ->
        /*if (renderState == RenderState.PAGE) {
            LineRenderer lineRenderer = new LineRenderer(line, pdfDocument, pdfDocument.x, pdfDocument.y)
            lineRenderer.render(pdfDocument.x, pdfDocument.y)
        }*/
    }

    def onRowComplete = { Row row ->
    }

    def onCellComplete = { Cell cell ->
    }

    private void addHeaderFooter() {
        int pageCount = pdfDocument.pages.size()
        def options = new HeaderFooterOptions(pageCount: pageCount, dateGenerated: new Date())
        (1..pageCount).each { int pageNumber ->
            pdfDocument.pageNumber = pageNumber
            options.pageNumber = pageNumber
            if (document.header) {
                renderState = RenderState.HEADER
                def header = document.header(options)
                renderHeaderFooter(header)
            }
            if (document.footer) {
                renderState = RenderState.FOOTER
                def footer = document.footer(options)
                renderHeaderFooter(footer)
            }
        }
        renderState = RenderState.PAGE
    }

    private void renderHeaderFooter(headerFooter) {
        float startX = document.margin.left + headerFooter.margin.left
        float startY
        if (renderState == RenderState.HEADER) {
            startY = headerFooter.margin.top
        } else {
            float pageBottom = pdfDocument.pageBottomY + document.margin.bottom
            startY = pageBottom - getElementHeight(headerFooter) - headerFooter.margin.bottom
        }
        def renderer = null
        if (headerFooter instanceof TextBlock) {
            renderer = new ParagraphRenderer(headerFooter, pdfDocument, startX, startY, document.width)
        } else if (headerFooter instanceof Table) {
            renderer = new TableRenderer(headerFooter as Table, pdfDocument, startX, startY)
        }
        if (renderer) {
            renderer.parse(document.height)
            renderer.render(startX, startY)
        }
    }

    private float getElementHeight(element) {
        float width = document.width - document.margin.top - document.margin.bottom
        if (element instanceof TextBlock) {
            new ParagraphRenderer(element, pdfDocument, 0, 0, width).totalHeight
        } else if (element instanceof Table) {
            new TableRenderer(element, pdfDocument, 0, 0).totalHeight
        } else if (element instanceof Line) {
            element.strokewidth
        } else {
            0
        }
    }

    private void addMetadata() {
        ByteArrayOutputStream xmpOut = new ByteArrayOutputStream()
        def xml = new MarkupBuilder(xmpOut.newWriter())
        xml.document(marginTop: "${document.margin.top}", marginBottom: "${document.margin.bottom}",
                marginLeft: "${document.margin.left}", marginRight: "${document.margin.right}") {
            delegate = xml
            resolveStrategy = Closure.DELEGATE_FIRST
            document.children.each { child ->
                switch (child.getClass()) {
                    case TextBlock:
                        addParagraphToMetadata(delegate, child)
                        break
                    case Table:
                        addTableToMetadata(delegate, child)
                        break
                }
            }
        }
        def catalog = pdfDocument.pdDocument.documentCatalog
        InputStream inputStream = new ByteArrayInputStream(xmpOut.toByteArray())
        PDMetadata metadata = new PDMetadata(pdfDocument.pdDocument, inputStream)
        catalog.metadata = metadata
    }

    private void addParagraphToMetadata(builder, TextBlock paragraphNode) {
        builder.paragraph(marginTop: "${paragraphNode.margin.top}",
                marginBottom: "${paragraphNode.margin.bottom}",
                marginLeft: "${paragraphNode.margin.left}",
                marginRight: "${paragraphNode.margin.right}") {
            paragraphNode.children?.findAll { it.getClass() == Image }.each {
                builder.image()
            }
            paragraphNode.children?.findAll { it.getClass() == Barcode }.each {
                builder.barcode()
            }
        }
    }

    private void addTableToMetadata(builder, Table tableNode) {
        builder.table(columns: tableNode.columnCount, width: tableNode.width, borderSize: tableNode.border.size) {
            delegate = builder
            resolveStrategy = Closure.DELEGATE_FIRST
            tableNode.children.each {
                def cells = it.children
                row {
                    cells.each {
                        cell(width: "${it.width ?: 0}")
                    }
                }
            }
        }
    }

}
