package org.xbib.graphics.pdfbox.layout.test;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.PageFormat;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.shape.RoundRect;
import org.xbib.graphics.pdfbox.layout.shape.Shape;
import org.xbib.graphics.pdfbox.layout.shape.Stroke;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.text.DrawContext;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.TextFlow;
import org.xbib.graphics.pdfbox.layout.text.TextFlowUtil;
import org.xbib.graphics.pdfbox.layout.util.TextSequenceUtil;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotationDrawListener;
import java.awt.Color;
import java.io.FileOutputStream;

public class LowLevelText {

    @Test
    public void test() throws Exception {
        PDDocument pdDocument = new PDDocument();
        PDPage page = new PDPage(PageFormat.A4);
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        pdDocument.addPage(page);
        final PDPageContentStream contentStream =
                new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.APPEND, true);

        AnnotationDrawListener annotationDrawListener = new AnnotationDrawListener(new DrawContext() {

            @Override
            public PDDocument getPdDocument() {
                return pdDocument;
            }

            @Override
            public PDPage getCurrentPage() {
                return page;
            }

            @Override
            public PDPageContentStream getCurrentPageContentStream() {
                return contentStream;
            }

        });
        annotationDrawListener.beforePage(null);

        TextFlow text = TextFlowUtil.createTextFlowFromMarkup(
                        "Hello *bold _italic bold-end* italic-end_. Eirmod\ntempor invidunt ut \\*labore",
                        new FontDescriptor(BaseFont.TIMES, 11));
        text.addText("Spongebob", 11, BaseFont.COURIER);
        text.addText(" is ", 20, BaseFont.HELVETICA);
        text.addText("cool", 7, BaseFont.HELVETICA);
        text.setMaxWidth(100);
        float xOffset = TextSequenceUtil.getOffset(text, pageWidth,
                Alignment.RIGHT);
        text.drawText(contentStream, new Position(xOffset, pageHeight - 50),
                Alignment.RIGHT, annotationDrawListener);

        String textBlock = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "{link[https://github.com/ralfstuckert/pdfbox-layout/]}pdfbox layout{link} "
                + "sed diam nonumy eirmod invidunt ut labore et dolore magna "
                + "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
                + "duo dolores et ea rebum.\n\nStet clita kasd gubergren, no sea takimata "
                + "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
                + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
                + "At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

        text = new TextFlow();
        text.addMarkup(textBlock, 8, BaseFont.COURIER);
        text.setMaxWidth(200);
        xOffset = TextSequenceUtil.getOffset(text, pageWidth, Alignment.CENTER);
        text.drawText(contentStream, new Position(xOffset, pageHeight - 100),
                Alignment.JUSTIFY, annotationDrawListener);

        // draw a round rect box with text
        text.setMaxWidth(350);
        float x = 50;
        float y = pageHeight - 300;
        float paddingX = 20;
        float paddingY = 15;
        float boxWidth = text.getWidth() + 2 * paddingX;
        float boxHeight = text.getHeight() + 2 * paddingY;

        Shape shape = new RoundRect(20);
        shape.fill(pdDocument, contentStream, new Position(x, y), boxWidth,
                boxHeight, Color.pink, null);
        shape.draw(pdDocument, contentStream, new Position(x, y), boxWidth,
                boxHeight, Color.blue, new Stroke(3), null);
        // now the text
        text.drawText(contentStream, new Position(x + paddingX, y - paddingY),
                Alignment.CENTER, annotationDrawListener);

        annotationDrawListener.afterPage(null);
        contentStream.close();
        annotationDrawListener.afterRender();
        pdDocument.save(new FileOutputStream("build/lowleveltext.pdf"));
        pdDocument.close();
    }
}
