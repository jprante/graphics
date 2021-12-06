package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.ControlElement;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.PageFormat;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.VerticalSpacer;
import org.xbib.graphics.pdfbox.layout.elements.render.ColumnLayout;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayout;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.FileOutputStream;

public class LandscapeTest {

    @Test
    public void main() throws Exception {
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

        Paragraph paragraph1 = new Paragraph();
        paragraph1.addMarkup(text1, 11, BaseFont.TIMES);
        Paragraph paragraph2 = new Paragraph();
        paragraph2.addMarkup(text2, 12, BaseFont.HELVETICA);
        Paragraph paragraph3 = new Paragraph();
        paragraph3.addMarkup(text1, 8, BaseFont.COURIER);

        Paragraph titleA4 = new Paragraph();
        titleA4.addMarkup("*Format A4 in Portrait*", 20, BaseFont.TIMES);
        Paragraph titleA5 = new Paragraph();
        titleA5.addMarkup("*Format A5 in Landscape*", 20, BaseFont.TIMES);

        PageFormat a5_landscape = PageFormat.builder().A5().landscape().margins(10, 50, 0, 30).build();
        PageFormat a4_portrait = PageFormat.builder().margins(40, 50, 40, 60).build();
        Document document = new Document(a4_portrait);

        document.add(titleA4, VerticalLayoutHint.CENTER);
        document.add(new VerticalSpacer(5));
        document.add(new ColumnLayout(2, 10));

        document.add(paragraph2);
        document.add(paragraph1);
        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph2);
        document.add(paragraph2);
        document.add(paragraph3);
        document.add(paragraph2);

        document.add(a5_landscape);
        document.add(ControlElement.NEWPAGE);
        document.add(new VerticalLayout());
        document.add(titleA5, VerticalLayoutHint.CENTER);
        document.add(new VerticalSpacer(5));
        document.add(new ColumnLayout(2, 10));

        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph2);
        document.add(paragraph3);

        document.add(a4_portrait);
        document.add(ControlElement.NEWPAGE);
        document.add(new VerticalLayout());
        document.add(titleA4, VerticalLayoutHint.CENTER);
        document.add(new VerticalSpacer(5));
        document.add(new ColumnLayout(2, 10));

        document.add(paragraph2);
        document.add(paragraph1);
        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph2);
        document.add(paragraph2);
        document.add(paragraph3);
        document.add(paragraph2);

        document.add(a5_landscape);
        document.add(ControlElement.NEWPAGE);
        document.add(new VerticalLayout());
        document.add(titleA5, VerticalLayoutHint.CENTER);
        document.add(new VerticalSpacer(5));
        document.add(new ColumnLayout(2, 10));

        document.add(paragraph1);
        document.add(paragraph3);
        document.add(paragraph2);
        document.add(paragraph3);

        document.render().save(new FileOutputStream("build/landscape.pdf")).close();
    }
}
