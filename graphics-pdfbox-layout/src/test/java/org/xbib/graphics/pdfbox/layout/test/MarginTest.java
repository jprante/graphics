package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.element.Document;
import org.xbib.graphics.pdfbox.layout.element.Paragraph;
import org.xbib.graphics.pdfbox.layout.element.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.FileOutputStream;

public class MarginTest {

    @Test
    public void test() throws Exception {
        String text1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                + "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo "
                + "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata "
                + "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
                + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
                + "eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

        String text2 = "short text, right aligned with some margin";

        String text3 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                + "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo "
                + "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata "
                + "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
                + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
                + "eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

        Document document = new Document(40, 60, 40, 60);
        Paragraph paragraph = new Paragraph();
        paragraph.addText(text1, 11, BaseFont.HELVETICA);
        document.add(paragraph, new VerticalLayoutHint(Alignment.LEFT, 0, 100,
                100, 100));

        paragraph = new Paragraph();
        paragraph.addText(text2, 11, BaseFont.HELVETICA);
        document.add(paragraph, new VerticalLayoutHint(Alignment.RIGHT, 0, 50,
                0, 0));

        paragraph = new Paragraph();
        paragraph.addText(text3, 11, BaseFont.HELVETICA);
        document.add(paragraph, new VerticalLayoutHint(Alignment.RIGHT, 150,
                150, 20, 0));
        document.render().save(new FileOutputStream("build/margin.pdf")).close();
    }
}
