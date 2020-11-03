package org.xbib.graphics.layout.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.layout.pdfbox.shape.RoundRect;
import org.xbib.graphics.layout.pdfbox.shape.Shape;
import org.xbib.graphics.layout.pdfbox.shape.Stroke;
import org.xbib.graphics.layout.pdfbox.text.Alignment;
import org.xbib.graphics.layout.pdfbox.text.BaseFont;
import org.xbib.graphics.layout.pdfbox.text.Constants;
import org.xbib.graphics.layout.pdfbox.text.DrawContext;
import org.xbib.graphics.layout.pdfbox.text.Position;
import org.xbib.graphics.layout.pdfbox.text.TextFlow;
import org.xbib.graphics.layout.pdfbox.text.TextFlowUtil;
import org.xbib.graphics.layout.pdfbox.text.TextSequenceUtil;
import org.xbib.graphics.layout.pdfbox.text.annotations.AnnotationDrawListener;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LowLevelText {

    @Test
    public void test() throws Exception {

        final PDDocument test = new PDDocument();
        final PDPage page = new PDPage(Constants.A4);
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();

        test.addPage(page);
        final PDPageContentStream contentStream =
                new PDPageContentStream(test, page, PDPageContentStream.AppendMode.APPEND, true);

        // AnnotationDrawListener is only needed if you use annoation based stuff, e.g. hyperlinks
        AnnotationDrawListener annotationDrawListener = new AnnotationDrawListener(new DrawContext() {

            @Override
            public PDDocument getPdDocument() {
                return test;
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

        TextFlow text = TextFlowUtil
                .createTextFlowFromMarkup(
                        "Hello *bold _italic bold-end* italic-end_. Eirmod\ntempor invidunt ut \\*labore",
                        11, BaseFont.Times);

        text.addText("Spongebob", 11, PDType1Font.COURIER);
        text.addText(" is ", 20, PDType1Font.HELVETICA_BOLD_OBLIQUE);
        text.addText("cool", 7, PDType1Font.HELVETICA);

        text.setMaxWidth(100);
        float xOffset = TextSequenceUtil.getOffset(text, pageWidth,
                Alignment.Right);
        text.drawText(contentStream, new Position(xOffset, pageHeight - 50),
                Alignment.Right, annotationDrawListener);

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
        text.addMarkup(textBlock, 8, BaseFont.Courier);
        text.setMaxWidth(200);
        xOffset = TextSequenceUtil.getOffset(text, pageWidth, Alignment.Center);
        text.drawText(contentStream, new Position(xOffset, pageHeight - 100),
                Alignment.Justify, annotationDrawListener);

        // draw a round rect box with text
        text.setMaxWidth(350);
        float x = 50;
        float y = pageHeight - 300;
        float paddingX = 20;
        float paddingY = 15;
        float boxWidth = text.getWidth() + 2 * paddingX;
        float boxHeight = text.getHeight() + 2 * paddingY;

        Shape shape = new RoundRect(20);
        shape.fill(test, contentStream, new Position(x, y), boxWidth,
                boxHeight, Color.pink, null);
        shape.draw(test, contentStream, new Position(x, y), boxWidth,
                boxHeight, Color.blue, new Stroke(3), null);
        // now the text
        text.drawText(contentStream, new Position(x + paddingX, y - paddingY),
                Alignment.Center, annotationDrawListener);

        annotationDrawListener.afterPage(null);
        contentStream.close();

        annotationDrawListener.afterRender();

        final OutputStream outputStream = new FileOutputStream("build/lowleveltext.pdf");
        test.save(outputStream);
        test.close();

    }
}