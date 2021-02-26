package org.xbib.graphics.pdfbox.groovy.test

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.util.Matrix
import org.junit.jupiter.api.Test
import org.xbib.graphics.barcode.Code3Of9
import org.xbib.graphics.barcode.HumanReadableLocation
import org.xbib.graphics.barcode.render.BarcodeGraphicsRenderer
import org.xbib.graphics.pdfbox.PdfBoxGraphics2D

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Paths

class PdfBoxBarcodeTest {

    @Test
    void testBarcodeImage() {
        PDDocument document = new PDDocument()
        PDPage page = new PDPage(PDRectangle.A4)
        document.addPage(page)
        PDPageContentStream contentStream = new PDPageContentStream(document, page)
        BarcodeCreator creator = new ImageBarcodeRenderer(document, contentStream)
        createBarcode(page, creator)
        createText(page, contentStream)
        String filename = "build/pdfbox-with-barcode-image.pdf"
        OutputStream outputStream = Files.newOutputStream(Paths.get(filename))
        document.save(outputStream)
    }

    @Test
    void testBarcodeEmbedded() {
        PDDocument document = new PDDocument()
        PDPage page = new PDPage(PDRectangle.A4)
        document.addPage(page)
        PDPageContentStream contentStream = new PDPageContentStream(document, page)
        BarcodeCreator creator = new PdfboxBarcodeRenderer(document, contentStream)
        createBarcode(page, creator)
        createText(page, contentStream)
        String filename = "build/pdfbox-with-barcode-embedded.pdf"
        OutputStream outputStream = Files.newOutputStream(Paths.get(filename))
        document.save(outputStream)
    }

    private void createBarcode(PDPage page, BarcodeCreator barcodeCreator) {
        float x = 32.0f
        float y = page.getBBox().height - 100.0f
        float width = 150f
        float height = 75f
        Code3Of9 code3Of9 = new Code3Of9()
        code3Of9.setContent('1234567890')
        code3Of9.setBarHeight(height as int)
        code3Of9.setHumanReadableLocation(HumanReadableLocation.BOTTOM)
        barcodeCreator.create(x, y, width, height, code3Of9)
    }

    private static void createText(PDPage page, PDPageContentStream contentStream) {
        contentStream.moveTo(32.0f, (page.getBBox().height - 80f) as float)
        contentStream.beginText()
        contentStream.setFont(PDType1Font.HELVETICA, 12f)
        contentStream.showText("Hello World")
        contentStream.endText()
        contentStream.close()
    }

    interface BarcodeCreator {
        void create(float x, float y, float width, float height, Code3Of9 code3Of9)
    }

    class ImageBarcodeRenderer implements BarcodeCreator {

        PDDocument document
        PDPageContentStream contentStream

        ImageBarcodeRenderer(PDDocument document, PDPageContentStream contentStream) {
            this.document = document
            this.contentStream = contentStream
        }

        @Override
        void create(float x, float y, float width, float height, Code3Of9 code3Of9) {
            BufferedImage bufferedImage = new BufferedImage(width as int, height as int, BufferedImage.TYPE_BYTE_GRAY)
            Graphics2D g2d = bufferedImage.createGraphics()
            Rectangle rectangle = new Rectangle(0, 0, width as int, height as int)
            BarcodeGraphicsRenderer renderer = new BarcodeGraphicsRenderer(g2d, rectangle, 1.0d,
                    Color.WHITE, Color.BLACK, false, false)
            renderer.render(code3Of9)
            renderer.close()
            PDImageXObject img = LosslessFactory.createFromImage(document, bufferedImage)
            Matrix matrix = new Matrix()
            matrix.translate(x, y)
            contentStream.saveGraphicsState()
            contentStream.transform(matrix)
            contentStream.drawImage(img, 0f, 0f, width, height)
            contentStream.restoreGraphicsState()
        }
    }

    class PdfboxBarcodeRenderer implements BarcodeCreator {

        PDDocument document
        PDPageContentStream contentStream

        PdfboxBarcodeRenderer(PDDocument document, PDPageContentStream contentStream) {
            this.document = document
            this.contentStream = contentStream
        }

        @Override
        void create(float x, float y, float width, float height, Code3Of9 code3Of9) {
            PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, width, height)
            Rectangle rectangle = new Rectangle(0, 0, width as int, height as int)
            BarcodeGraphicsRenderer renderer = new BarcodeGraphicsRenderer(pdfBoxGraphics2D, rectangle, 1.0d,
                    Color.WHITE, Color.BLACK, false, false)
            renderer.render(code3Of9)
            renderer.close()
            PDFormXObject xFormObject = pdfBoxGraphics2D.getXFormObject()
            Matrix matrix = new Matrix()
            matrix.translate(x, y)
            contentStream.saveGraphicsState()
            contentStream.transform(matrix)
            contentStream.drawForm(xFormObject)
            contentStream.restoreGraphicsState()
        }
    }
}
