package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.VerticalSpacer;
import org.xbib.graphics.pdfbox.layout.elements.render.ColumnLayout;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ColumnsTest {

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

        String text2 = "At *vero eos et accusam* et justo duo dolores et ea rebum. "
                + "Stet clita kasd gubergren, no sea takimata\n\n"
                + "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
                + "_consetetur sadipscing elitr_, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
                + "eos et _accusam et *justo* duo dolores_ et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

        Document document = new Document(40, 50, 40, 60);

        Paragraph title = new Paragraph();
        title.addMarkup("*This Text is organized in Colums*", 20, BaseFont.TIMES);
        document.add(title, VerticalLayoutHint.CENTER);
        document.add(new VerticalSpacer(5));

        // use column layout from now on
        document.add(new ColumnLayout(2, 10));

        Paragraph paragraph1 = new Paragraph();
        paragraph1.addMarkup(text1, 11, BaseFont.TIMES);
        document.add(paragraph1);

        Paragraph paragraph2 = new Paragraph();
        paragraph2.addMarkup(text2, 12, BaseFont.HELVETICA);
        document.add(paragraph2);

        Paragraph paragraph3 = new Paragraph();
        paragraph3.addMarkup(text1, 8, BaseFont.COURIER);
        document.add(paragraph3);

        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph1);
        document.add(paragraph2);
        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph2);
        document.add(paragraph1);
        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph2);
        document.add(paragraph2);
        document.add(paragraph3);
        document.add(paragraph1);
        document.add(paragraph1);
        document.add(paragraph2);
        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph2);
        document.add(paragraph3);
        document.add(paragraph1);
        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph2);
        document.add(paragraph2);
        document.render().save(new FileOutputStream("build/columns.pdf")).close();
    }
}
