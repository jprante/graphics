package org.xbib.graphics.pdfbox.test;

import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.xbib.graphics.pdfbox.PdfBoxGraphics2D;
import org.xbib.graphics.pdfbox.font.CoreFontDrawer;
import org.xbib.graphics.pdfbox.font.DefaultFontDrawer;
import org.xbib.graphics.pdfbox.font.ForcedFontDrawer;

import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class PdfBoxGraphics2DTestBase {

    enum Mode {
        DefaultVectorized, FontTextIfPossible, ForceFontText, DefaultFontText
    }

    void exportGraphic(String dir, String name, GraphicsExporter exporter) {
        try {
            PDDocument document = new PDDocument();
            PDFont helvetica = PDType1Font.HELVETICA;
            File parentDir = new File("build/test/" + dir);
            parentDir.mkdirs();
            BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D imageGraphics = image.createGraphics();
            exporter.draw(imageGraphics);
            imageGraphics.dispose();
            ImageIO.write(image, "PNG", new File(parentDir, name + ".png"));
            for (Mode m : Mode.values()) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, 400, 400);
                contentStream.beginText();
                contentStream.setStrokingColor(0f, 0f, 0f);
                contentStream.setNonStrokingColor(0f, 0f, 0f);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 15);
                contentStream.setTextMatrix(Matrix.getTranslateInstance(10, 800));
                contentStream.showText("Mode " + m);
                contentStream.endText();
                DefaultFontDrawer fontTextDrawer = null;
                switch (m) {
                    case FontTextIfPossible:
                        fontTextDrawer = new DefaultFontDrawer();
                        registerFonts(fontTextDrawer);
                        break;
                    case DefaultFontText: {
                        fontTextDrawer = new CoreFontDrawer();
                        registerFonts(fontTextDrawer);
                        break;
                    }
                    case ForceFontText:
                        fontTextDrawer = new ForcedFontDrawer();
                        registerFonts(fontTextDrawer);
                        fontTextDrawer.registerFont("Arial", helvetica);
                        break;
                    case DefaultVectorized:
                    default:
                        break;
                }
                if (fontTextDrawer != null) {
                    pdfBoxGraphics2D.setFontTextDrawer(fontTextDrawer);
                }
                exporter.draw(pdfBoxGraphics2D);
                pdfBoxGraphics2D.dispose();
                PDFormXObject appearanceStream = pdfBoxGraphics2D.getXFormObject();
                Matrix matrix = new Matrix();
                matrix.translate(0, 20);
                contentStream.transform(matrix);
                contentStream.drawForm(appearanceStream);
                matrix.scale(1.5f, 1.5f);
                matrix.translate(0, 100);
                contentStream.transform(matrix);
                contentStream.drawForm(appearanceStream);
                contentStream.close();
            }
            document.save(new File(parentDir, name + ".pdf"));
            document.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerFonts(DefaultFontDrawer fontTextDrawer) {
        String packageName = getClass().getPackageName().replace('.', '/');
        fontTextDrawer.registerFont(new File("src/test/resources/" + packageName + "/DejaVuSerifCondensed.ttf"));
        fontTextDrawer.registerFont(new File("src/test/resources/" + packageName + "/antonio/Antonio-Regular.ttf"));
    }

    interface GraphicsExporter {
        void draw(Graphics2D gfx) throws IOException, FontFormatException;
    }

}
