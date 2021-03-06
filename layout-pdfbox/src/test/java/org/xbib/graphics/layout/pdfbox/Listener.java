package org.xbib.graphics.layout.pdfbox;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.layout.pdfbox.elements.Document;
import org.xbib.graphics.layout.pdfbox.elements.Paragraph;
import org.xbib.graphics.layout.pdfbox.elements.render.RenderContext;
import org.xbib.graphics.layout.pdfbox.elements.render.RenderListener;
import org.xbib.graphics.layout.pdfbox.text.Alignment;
import org.xbib.graphics.layout.pdfbox.text.BaseFont;
import org.xbib.graphics.layout.pdfbox.text.Position;
import org.xbib.graphics.layout.pdfbox.text.TextFlow;
import org.xbib.graphics.layout.pdfbox.text.TextFlowUtil;
import org.xbib.graphics.layout.pdfbox.text.TextSequenceUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Listener {

    @Test
    public void test() throws Exception {
        String text1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                + "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
                + "duo dolores et ea rebum.\n\nStet clita kasd gubergren, no sea takimata "
                + "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
                + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
                + "At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n";

        String text2 = "At *vero eos et accusam* et justo duo dolores et ea rebum."
                + "Stet clita kasd gubergren, no sea takimata\n\n"
                + "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
                + "_consetetur sadipscing elitr_, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
                + "eos et _accusam et *justo* duo dolores_ et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";


        Document document = new Document(40, 60, 40, 60);
        document.addRenderListener(new RenderListener() {

            @Override
            public void beforePage(RenderContext renderContext) {

            }

            @Override
            public void afterPage(RenderContext renderContext)
                    throws IOException {
                String content = String.format("Page %s",
                        renderContext.getPageIndex() + 1);
                TextFlow text = TextFlowUtil.createTextFlow(content, 11,
                        PDType1Font.TIMES_ROMAN);
                float offset = renderContext.getPageFormat().getMarginLeft()
                        + TextSequenceUtil.getOffset(text,
                        renderContext.getWidth(), Alignment.Right);
                text.drawText(renderContext.getContentStream(), new Position(
                        offset, 30), Alignment.Right, null);
            }
        });

        Paragraph paragraph = new Paragraph();
        paragraph.addMarkup(text1, 11, BaseFont.Times);
        paragraph.addMarkup(text2, 12, BaseFont.Helvetica);
        paragraph.addMarkup(text1, 8, BaseFont.Courier);

        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);

        final OutputStream outputStream = new FileOutputStream("build/listener.pdf");
        document.save(outputStream);

    }
}
