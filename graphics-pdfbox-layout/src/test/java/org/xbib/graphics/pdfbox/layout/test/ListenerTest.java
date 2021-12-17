package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.element.Document;
import org.xbib.graphics.pdfbox.layout.element.Paragraph;
import org.xbib.graphics.pdfbox.layout.element.render.RenderContext;
import org.xbib.graphics.pdfbox.layout.element.render.RenderListener;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.TextFlow;
import org.xbib.graphics.pdfbox.layout.text.TextFlowUtil;
import org.xbib.graphics.pdfbox.layout.util.TextSequenceUtil;
import java.io.FileOutputStream;

public class ListenerTest {

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
            public void afterPage(RenderContext renderContext) {
                String content = String.format("Page %s", renderContext.getPageIndex() + 1);
                FontDescriptor fontDescriptor = new FontDescriptor(BaseFont.HELVETICA, 11);
                TextFlow text = TextFlowUtil.createTextFlow(content, fontDescriptor);
                float offset = renderContext.getPageFormat().getMarginLeft()
                        + TextSequenceUtil.getOffset(text,
                        renderContext.getWidth(), Alignment.RIGHT);
                text.drawText(renderContext.getContentStream(), new Position(
                        offset, 30), Alignment.RIGHT, null);
            }
        });

        Paragraph paragraph = new Paragraph();
        paragraph.addMarkup(text1, 11, BaseFont.TIMES);
        paragraph.addMarkup(text2, 12, BaseFont.HELVETICA);
        paragraph.addMarkup(text1, 8, BaseFont.COURIER);

        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.render().save(new FileOutputStream("build/listener.pdf")).close();
    }
}
