package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Element;
import org.xbib.graphics.pdfbox.layout.elements.HorizontalRuler;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.render.LayoutHint;
import org.xbib.graphics.pdfbox.layout.elements.render.RenderContext;
import org.xbib.graphics.pdfbox.layout.elements.render.RenderListener;
import org.xbib.graphics.pdfbox.layout.elements.render.Renderer;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.shape.Stroke;
import org.xbib.graphics.pdfbox.layout.shape.Stroke.CapStyle;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.TextFlow;
import org.xbib.graphics.pdfbox.layout.text.TextFlowUtil;
import org.xbib.graphics.pdfbox.layout.text.TextSequenceUtil;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CustomRenderer {

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
        SectionRenderer sectionRenderer = new SectionRenderer();
        document.addRenderer(sectionRenderer);
        document.addRenderListener(sectionRenderer);

        Paragraph paragraph = new Paragraph();
        paragraph.addMarkup(text1, 11, BaseFont.TIMES);
        paragraph.addMarkup(text2, 12, BaseFont.HELVETICA);
        paragraph.addMarkup(text1, 8, BaseFont.COURIER);

        document.add(new Section(1));
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(new Section(2));
        document.add(paragraph);
        document.add(paragraph);
        document.add(paragraph);
        document.add(new Section(3));
        document.add(paragraph);
        document.add(paragraph);

        final OutputStream outputStream = new FileOutputStream("build/customrenderer.pdf");
        document.render().save(outputStream);

    }

    public static class SectionRenderer implements Renderer, RenderListener {

        private int sectionNumber;

        @Override
        public boolean render(RenderContext renderContext, Element element,
                              LayoutHint layoutHint) throws IOException {
            if (element instanceof Section) {
                if (renderContext.getPageIndex() > 0) {
                    renderContext.newPage();
                }
                sectionNumber = ((Section) element).getNumber();
                renderContext.render(renderContext, element, layoutHint);
                Element ruler = new HorizontalRuler(Stroke.builder().lineWidth(2)
                        .capStyle(CapStyle.RoundCap).build(), Color.black);
                renderContext.render(renderContext, ruler, VerticalLayoutHint.builder().marginBottom(10).build());
                return true;
            }
            return false;
        }

        @Override
        public void beforePage(RenderContext renderContext) {
        }

        @Override
        public void afterPage(RenderContext renderContext) {
            String content = String.format("Section %s, Page %s", sectionNumber, renderContext.getPageIndex() + 1);
            FontDescriptor fontDescriptor = new FontDescriptor(BaseFont.TIMES, 11);
            TextFlow text = TextFlowUtil.createTextFlow(content, fontDescriptor);
            float offset = renderContext.getPageFormat().getMarginLeft() +
                    TextSequenceUtil.getOffset(text, renderContext.getWidth(), Alignment.RIGHT);
            text.drawText(renderContext.getContentStream(), new Position(
                    offset, 30), Alignment.RIGHT, null);
        }
    }
}