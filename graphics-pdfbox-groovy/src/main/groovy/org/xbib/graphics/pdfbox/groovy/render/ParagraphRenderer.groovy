package org.xbib.graphics.pdfbox.groovy.render

import groovy.util.logging.Log4j2
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream
import org.xbib.graphics.barcode.Code3Of9
import org.xbib.graphics.barcode.HumanReadableLocation
import org.xbib.graphics.barcode.render.GraphicsRenderer
import org.xbib.graphics.pdfbox.PdfBoxGraphics2D
import org.xbib.graphics.pdfbox.groovy.*
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocument
import org.xbib.graphics.pdfbox.groovy.render.element.BarcodeElement
import org.xbib.graphics.pdfbox.groovy.render.element.ImageElement
import org.xbib.graphics.pdfbox.groovy.render.element.LineElement
import org.xbib.graphics.pdfbox.groovy.render.element.TextElement

import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage

@Log4j2
class ParagraphRenderer implements Renderable {

    TextBlock node

    List<ParagraphLine> lines

    BigDecimal renderedHeight = 0

    private Integer parseStart = 0

    private Integer linesParsed = 0

    private BigDecimal startX

    private BigDecimal startY

    private Boolean parsedAndRendered = false

    private Boolean fullyRendered = false

    private Boolean fullyParsed = false

    ParagraphRenderer(TextBlock paragraph, PdfDocument pdfDocument, BigDecimal startX, BigDecimal startY, BigDecimal maxWidth) {
        this.node = paragraph
        this.pdfDocument = pdfDocument
        this.startX = startX
        this.startY = startY
        lines = ParagraphParser.getLines(paragraph, maxWidth)
    }

    Boolean getFullyParsed() {
        this.fullyParsed
    }

    int getParseStart() {
        this.parseStart
    }

    int getParseEnd() {
        int parseEnd = Math.max(0, parseStart + linesParsed - 1) as int
        Math.min(lines.size() - 1, parseEnd)
    }

    int getLinesParsed() {
        this.linesParsed
    }

    @Override
    void parse(BigDecimal height) {
        if (!lines || fullyRendered) {
            fullyParsed = true
            return
        }
        if (parsedAndRendered) {
            parseStart += linesParsed
            parseStart = Math.min(lines.size() - 1, parseStart)
        }
        linesParsed = 0
        boolean reachedEnd = false
        BigDecimal parsedHeight = 0
        while (!reachedEnd) {
            ParagraphLine line = lines[parseStart + linesParsed]
            if (line.getTotalHeight() > 0) {
                parsedHeight += line.getTotalHeight()
                linesParsed++
                if (parsedHeight > height) {
                    linesParsed = Math.max(0f, linesParsed - 1) as int
                    reachedEnd = true
                    fullyParsed = false
                } else if (line == lines.last()) {
                    reachedEnd = true
                    fullyParsed = true
                }
            } else {
                linesParsed++
                if (line == lines.last()) {
                    reachedEnd = true
                    fullyParsed = true
                }
            }
        }
        parsedAndRendered = false
    }

    @Override
    void renderElement(BigDecimal startX, BigDecimal startY) {
        if (fullyRendered || !linesParsed) {
            return
        }
        pdfDocument.x = startX
        pdfDocument.y = startY
        lines[parseStart..parseEnd].each { ParagraphLine line ->
            renderLine(line)
        }
        renderedHeight = getParsedHeight()
        fullyRendered = fullyParsed
        parsedAndRendered = true
    }

    @Override
    BigDecimal getTotalHeight() {
        lines.sum { it.totalHeight } as BigDecimal
    }

    @Override
    BigDecimal getParsedHeight() {
        if (!linesParsed) {
            return 0
        }
        lines[parseStart..parseEnd]*.totalHeight.sum() as BigDecimal ?: 0
    }

    private void renderLine(ParagraphLine line) {
        BigDecimal renderStartX = startX
        BigDecimal delta = line.maxWidth - line.contentWidth
        switch (line.paragraph.align) {
            case Align.RIGHT:
                renderStartX += delta
                break
            case Align.CENTER:
                renderStartX += (delta / 2)
                break
        }
        pdfDocument.x = renderStartX
        pdfDocument.y += line.getContentHeight()
        line.elements.each { element ->
            switch (element.getClass()) {
                case TextElement:
                    renderTextElement(element as TextElement)
                    pdfDocument.x += element.width
                    break
                case LineElement:
                    renderLineElement(element as LineElement)
                    break
                case ImageElement:
                    renderImageElement(element as ImageElement)
                    break
                case BarcodeElement:
                    renderBarcodeElement(element as BarcodeElement)
            }
        }
    }

    private void renderTextElement(TextElement element) {
        Text text = element.node
        PDPageContentStream contentStream = pdfDocument.contentStream
        contentStream.beginText()
        contentStream.newLineAtOffset(pdfDocument.x as float, pdfDocument.translatedY as float)
        def color = text.font.color.rgb
        contentStream.setNonStrokingColor(color[0], color[1], color[2])
        contentStream.setFont(element.pdfFont, text.font.size)
        String string = element.text.replaceAll("\\p{C}","")
        contentStream.showText(string) // remove control chars
        contentStream.endText()
    }

    private void renderLineElement(LineElement element) {
        Line line = element.node
        PDPageContentStream contentStream = pdfDocument.contentStream
        BigDecimal x1 = pdfDocument.x + line.startX
        BigDecimal y1 = pdfDocument.translateY(pdfDocument.y + line.startY)
        contentStream.moveTo(x1 as float, y1 as float)
        BigDecimal x2 = pdfDocument.x + line.endX
        BigDecimal y2 = pdfDocument.translateY(pdfDocument.y + line.endY)
        contentStream.lineTo(x2 as float, y2 as float)
        contentStream.setLineWidth(line.strokewidth)
        contentStream.stroke()
    }

    private void renderImageElement(ImageElement element) {
        Image image = element.node
        ImageType imageType = element.node.type
        if (imageType) {
            InputStream inputStream = new ByteArrayInputStream(element.node.data)
            // TODO add TwelveMonkeys
            BufferedImage bufferedImage = ImageIO.read(inputStream)
            BigDecimal x = pdfDocument.x + image.x
            BigDecimal y = pdfDocument.translateY(pdfDocument.y + image.y)
            int width = element.node.width
            int height = element.node.height
            PDImageXObject img = imageType == ImageType.JPG ?
                    JPEGFactory.createFromImage(pdfDocument.pdDocument, bufferedImage) :
                    LosslessFactory.createFromImage(pdfDocument.pdDocument, bufferedImage)
            pdfDocument.contentStream.drawImage(img, x as float, y as float, width, height)
            inputStream.close()
        }
    }

    private void renderBarcodeElement(BarcodeElement element) {
        switch (element.node.getType()) {
            case BarcodeType.CODE39:
                float x = pdfDocument.x + element.node.x
                float y = pdfDocument.translateY(pdfDocument.y + element.node.y)
                Code3Of9 code3Of9 = new Code3Of9()
                code3Of9.setContent(element.node.value)
                code3Of9.setBarHeight(element.node.height)
                code3Of9.setHumanReadableLocation(HumanReadableLocation.NONE)
                PDFormXObject formXObject = createXObject(pdfDocument.pdDocument, x, y)
                PdfBoxGraphics2D g2d = new PdfBoxGraphics2D(pdfDocument.pdDocument, formXObject, pdfDocument.contentStream, null)
                Rectangle rectangle = new Rectangle(0, 0, x as int, y as int)
                GraphicsRenderer graphicsRenderer =
                        new GraphicsRenderer(g2d, rectangle, 1.0d, java.awt.Color.WHITE, java.awt.Color.BLACK, false, false);
                graphicsRenderer.render(code3Of9);
                graphicsRenderer.close();
                break
        }
    }

    private static PDFormXObject createXObject(PDDocument document, float x, float y) {
        PDFormXObject xFormObject = new PDAppearanceStream(document)
        xFormObject.setResources(new PDResources())
        xFormObject.setBBox(new PDRectangle(x, y))
        return xFormObject
    }

}
