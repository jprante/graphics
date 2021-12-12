package org.xbib.graphics.pdfbox.groovy.render

import org.apache.pdfbox.pdmodel.font.PDFont
import org.xbib.graphics.pdfbox.groovy.Barcode
import org.xbib.graphics.pdfbox.groovy.Font
import org.xbib.graphics.pdfbox.groovy.Image
import org.xbib.graphics.pdfbox.groovy.Line
import org.xbib.graphics.pdfbox.groovy.LineBreak
import org.xbib.graphics.pdfbox.groovy.Text
import org.xbib.graphics.pdfbox.groovy.TextBlock
import org.xbib.graphics.pdfbox.groovy.builder.PdfFont
import org.xbib.graphics.pdfbox.groovy.render.element.BarcodeElement
import org.xbib.graphics.pdfbox.groovy.render.element.ImageElement
import org.xbib.graphics.pdfbox.groovy.render.element.LineElement
import org.xbib.graphics.pdfbox.groovy.render.element.TextElement

class ParagraphParser {

    static List<ParagraphLine> getLines(TextBlock paragraph, BigDecimal maxLineWidth) {

        def lines = []

        def currentChunk = []

        def paragraphChunks = []

        paragraphChunks << currentChunk

        paragraph.children.each { child ->
            if (child.getClass() == LineBreak) {
                currentChunk = []
                paragraphChunks << currentChunk
            } else {
                currentChunk << child
            }
        }

        paragraphChunks.each { lines += parseParagraphChunk(it, paragraph, maxLineWidth) }
        lines
    }

    private static List<ParagraphLine> parseParagraphChunk(chunk, TextBlock paragraph, BigDecimal maxLineWidth) {
        def chunkLines = []
        ParagraphLine currentLine = new ParagraphLine(paragraph, maxLineWidth)
        chunkLines << currentLine
        PDFont pdfFont
        chunk.each { node ->
            if (node.class == Text) {
                Font font = node.font
                pdfFont = PdfFont.getFont(font)
                String remainingText = node.value
                while (remainingText) {
                    BigDecimal heightFactor = paragraph.properties.heightfactor ?: 1.5
                    BigDecimal size = font.size as BigDecimal
                    BigDecimal textWidth = getTextWidth(remainingText, pdfFont, size)
                    if (currentLine.contentWidth + textWidth > maxLineWidth) {
                        String text = getTextUntilBreak(remainingText, pdfFont, size, currentLine.remainingWidth)
                        int nextPosition = text.size()
                        remainingText = remainingText[nextPosition..-1].trim()
                        int elementWidth = getTextWidth(text, pdfFont, size) as int
                        currentLine.contentWidth += elementWidth
                        currentLine.elements << new TextElement(pdfFont: pdfFont, text: text, node: node,
                                width: elementWidth, heightfactor: heightFactor)
                        currentLine = new ParagraphLine(paragraph, maxLineWidth)
                        chunkLines << currentLine
                    } else {
                        currentLine.elements << new TextElement(pdfFont: pdfFont, text: remainingText, node: node,
                                width: textWidth, heightfactor: heightFactor)
                        remainingText = ''
                        currentLine.contentWidth += textWidth
                    }

                }
            } else if (node.class ==  Line) {
                currentLine.elements << new LineElement(node: node)
            } else if (node.class == Image) {
                if (currentLine.remainingWidth < node.width) {
                    currentLine = new ParagraphLine(paragraph, maxLineWidth)
                    chunkLines << currentLine
                }
                currentLine.contentWidth += node.width
                currentLine.elements << new ImageElement(node: node)
            } else if (node.class == Barcode) {
                if (currentLine.remainingWidth < node.width) {
                    currentLine = new ParagraphLine(paragraph, maxLineWidth)
                    chunkLines << currentLine
                }
                currentLine.contentWidth += node.width
                currentLine.elements << new BarcodeElement(node: node)
            } else {
                throw new IllegalStateException('unknown element class ' + node.class.name)
            }
        }
        chunkLines
    }

    private static String getTextUntilBreak(String text, PDFont font, BigDecimal fontSize, BigDecimal width) {
        String result = ''
        String previousResult = ''
        boolean spaceBreakpointFound = false
        String[] words = text.split()*.trim()
        int wordIndex = 0
        BigDecimal resultWidth = 0
        while (words && resultWidth < width && wordIndex < words.size()) {
            result += (wordIndex == 0 ? '' : ' ') + words[wordIndex]
            resultWidth = getTextWidth(result, font, fontSize)
            if (resultWidth == width) {
                spaceBreakpointFound = true
                break
            } else if (resultWidth < width) {
                spaceBreakpointFound = true
            } else if (resultWidth > width) {
                result = previousResult
                break
            }
            wordIndex++
            previousResult = result
        }
        if (!spaceBreakpointFound) {
            int currentCharacter = 0
            while (getTextWidth(result, font, fontSize) < width) {
                result += text[currentCharacter]
                currentCharacter++
            }
            result = result.length() > 0 ? result.subSequence(0, result.length() - 1) : ''
        }
        result
    }

    private static BigDecimal getTextWidth(String text, PDFont font, BigDecimal fontSize) {
        // getStringWidth: not a cheap operation, and full of run time exceptions!
        font.getStringWidth(text.replaceAll("\\p{C}", "")) / 1000 * fontSize
    }
}
