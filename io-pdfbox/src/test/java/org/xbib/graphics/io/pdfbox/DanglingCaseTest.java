package org.xbib.graphics.io.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class DanglingCaseTest {

    @Test
    public void testDanglingGfx() throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, 400, 400);
        PdfBoxGraphics2D child = pdfBoxGraphics2D.create(10, 10, 40, 40);
        child.setColor(Color.RED);
        child.fillRect(0, 0, 100, 100);
        PdfBoxGraphics2D child2 = child.create(20, 20, 10, 10);
        child2.setColor(Color.GREEN);
        child2.drawOval(0, 0, 5, 5);
        child.create();
        child.dispose();
        child2.dispose();
        pdfBoxGraphics2D.dispose();
        PDFormXObject appearanceStream = pdfBoxGraphics2D.getXFormObject();
        Matrix matrix = new Matrix();
        matrix.translate(0, 20);
        contentStream.transform(matrix);
        contentStream.drawForm(appearanceStream);
        contentStream.close();
        File file = new File("build/test/dangling_test.pdf");
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
    }

    @Test
    public void testDanglingDisposeException() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, 400, 400);
            pdfBoxGraphics2D.create();
            pdfBoxGraphics2D.dispose();
        });
    }
}
