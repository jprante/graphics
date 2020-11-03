package org.xbib.graphics.layout.pdfbox;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.layout.pdfbox.elements.ControlElement;
import org.xbib.graphics.layout.pdfbox.elements.Document;
import org.xbib.graphics.layout.pdfbox.elements.PageFormat;
import org.xbib.graphics.layout.pdfbox.elements.Paragraph;
import org.xbib.graphics.layout.pdfbox.elements.VerticalSpacer;
import org.xbib.graphics.layout.pdfbox.elements.render.ColumnLayout;
import org.xbib.graphics.layout.pdfbox.elements.render.VerticalLayout;
import org.xbib.graphics.layout.pdfbox.elements.render.VerticalLayoutHint;
import org.xbib.graphics.layout.pdfbox.text.BaseFont;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Landscape {

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
        paragraph1.addMarkup(text1, 11, BaseFont.Times);
        Paragraph paragraph2 = new Paragraph();
        paragraph2.addMarkup(text2, 12, BaseFont.Helvetica);
        Paragraph paragraph3 = new Paragraph();
        paragraph3.addMarkup(text1, 8, BaseFont.Courier);

        Paragraph titleA4 = new Paragraph();
        titleA4.addMarkup("*Format A4 in Portrait*", 20, BaseFont.Times);
        Paragraph titleA5 = new Paragraph();
        titleA5.addMarkup("*Format A5 in Landscape*", 20, BaseFont.Times);

        PageFormat a5_landscape = PageFormat.with().A5().landscape().margins(10, 50, 0, 30).build();
        PageFormat a4_portrait = PageFormat.with().margins(40, 50, 40, 60).build();
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

        final OutputStream outputStream = new FileOutputStream("build/landscape.pdf");
        document.save(outputStream);

    }
}
