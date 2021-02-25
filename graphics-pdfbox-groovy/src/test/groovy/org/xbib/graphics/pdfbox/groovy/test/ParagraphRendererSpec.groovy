package org.xbib.graphics.pdfbox.groovy.test

import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.TextBlock
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument
import org.xbib.graphics.pdfbox.groovy.render.ParagraphRenderer
import spock.lang.Shared

class ParagraphRendererSpec extends RendererTestBase {

    @Shared
    ParagraphRenderer paragraphElement

    def setup() {
        TextBlock paragraph = makeParagraph(3)
        Document document = makeDocument()
        PdfDocument pdfDocument = new PdfDocument(document)
        paragraphElement = makeParagraphElement(pdfDocument, paragraph)
    }

    def cleanup() {
        paragraphElement.pdfDocument.getPdDocument().close()
    }

    def "Can parse all lines"() {
        float height = defaultLineHeight * 3f

        when:
        paragraphElement.parse(height)

        then:
        paragraphElement.lines.size() == 3

        and:
        paragraphElement.parseStart == 0

        and:
        paragraphElement.parseEnd == 2

        and:
        paragraphElement.fullyParsed
    }

    def "Can parse a single line"() {
        when:
        paragraphElement.with {
            parse(defaultLineHeight)
        }

        then:
        paragraphElement.parseStart == 0

        and:
        paragraphElement.parseEnd == 0

        and:
        paragraphElement.linesParsed == 1

        when:
        paragraphElement.with {
            render(0, 0)
            parse(defaultLineHeight)
        }

        then:
        paragraphElement.parseStart == 1

        and:
        paragraphElement.parseEnd == 1

        when:
        paragraphElement.with {
            render(0, 0)
            parse(defaultLineHeight)
            render(0, 0)
        }

        then:
        paragraphElement.parseStart == 2

        and:
        paragraphElement.parseEnd == 2

        and:
        paragraphElement.fullyParsed

        and:
        paragraphElement.fullyRendered
    }
}
