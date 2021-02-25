package org.xbib.graphics.pdfbox.groovy.test

import org.xbib.graphics.pdfbox.groovy.BaseNode
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.Font
import org.xbib.graphics.pdfbox.groovy.LineBreak
import org.xbib.graphics.pdfbox.groovy.Margin
import org.xbib.graphics.pdfbox.groovy.Text
import org.xbib.graphics.pdfbox.groovy.TextBlock
import org.xbib.graphics.pdfbox.groovy.UnitUtil
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument
import org.xbib.graphics.pdfbox.groovy.render.ParagraphRenderer
import spock.lang.Specification

class RendererTestBase extends Specification {

    public static final Margin defaultMargin = new Margin(top: UnitUtil.mmToPoint(5 as BigDecimal),
            bottom: UnitUtil.mmToPoint(5 as BigDecimal),
            left: UnitUtil.mmToPoint(5 as BigDecimal),
            right: UnitUtil.mmToPoint(5 as BigDecimal))

    public static final BigDecimal defaultLineHeight = 18 as BigDecimal

    Document makeDocument() {
        new Document(margin: defaultMargin, font: new Font())
    }

    TextBlock makeParagraph(TextBlock paragraph, BaseNode parent = makeDocument()) {
        TextBlock newParagraph = paragraph.clone()
        newParagraph.parent = parent
        parent.children << newParagraph
        newParagraph
    }

    TextBlock makeParagraph(int lineCount, BaseNode parent = makeDocument()) {
        TextBlock paragraph = new TextBlock(margin: Margin.NONE, font: new Font())
        lineCount.times {
            paragraph.children << new Text(value: "Line${it}", font: new Font())
            if (it != lineCount - 1) {
                paragraph.children << new LineBreak()
            }
        }
        paragraph.parent = parent
        parent.children << paragraph
        paragraph
    }

    ParagraphRenderer makeParagraphElement(PdfDocument pdfDocument, TextBlock paragraph) {
        new ParagraphRenderer(paragraph, pdfDocument, 0 as BigDecimal, 0 as BigDecimal, paragraph.parent.width)
    }

}