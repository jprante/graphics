package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.element.Document;
import org.xbib.graphics.pdfbox.layout.element.Paragraph;
import org.xbib.graphics.pdfbox.layout.element.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.FileOutputStream;

public class MarkupTest {

    @Test
    public void test() throws Exception {
        String text1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                + "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
                + "duo dolores et ea rebum.\nStet clita kasd gubergren, no sea takimata "
                + "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
                + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n"
                + "At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n";

        Document document = new Document(40, 60, 40, 60);

        Paragraph paragraph = new Paragraph();
        paragraph.addMarkup(text1, 11, BaseFont.TIMES);
        document.add(paragraph);

        paragraph = new Paragraph();
        paragraph.addMarkup("Markup supports *bold*, _italic_, and *even _mixed* markup_.\n\n",
                11, BaseFont.TIMES);
        paragraph.addMarkup("Escape \\* with \\\\\\* and \\_ with \\\\\\_ in markup.\n\n",
                11, BaseFont.TIMES);
        paragraph.addMarkup("And now also {color:#ff0000}c{color:#00ff00}o{color:#0000ff}l{color:#00cccc}o{color:#cc00cc}r{color:#000000}",
                11, BaseFont.TIMES);
        paragraph.addMarkup(" , {_}subscript{_} and {^}superscript{^}.\n\n",
                11, BaseFont.TIMES);
        paragraph.addMarkup("You can alternate the position and thickness of an __underline__, "
                                + "so you may also use this to __{0.25:}strike through__ or blacken __{0.25:20}things__ out\n\n",
                        11, BaseFont.TIMES);
        document.add(paragraph, new VerticalLayoutHint(Alignment.LEFT, 0, 0, 30, 0));

        paragraph = new Paragraph();
        paragraph.addMarkup("And here comes a link to an internal anchor name {color:#ff5000}{link[#hello]}hello{link}{color:#000000}.\n\n", 11, BaseFont.TIMES);
        paragraph.addMarkup("\n\n{anchor:hello}Here{anchor} comes the internal anchor named *hello*\n\n", 11, BaseFont.COURIER);
        document.add(paragraph);

        paragraph = new Paragraph();
        text1 = "\nAlso, you can do all that indentation stuff much easier with markup, e.g. simple indentation\n"
                + "--At vero eos et accusam\n\n"
                + "-!And end the indentation. Now a list:\n"
                + "-+This is a list item\n"
                + "-+Another list item\n"
                + " -+A sub list item\n"
                + "-+And yet another one\n\n"
                + "-!Even enumeration is supported:\n"
                + "-#This is a list item\n"
                + "-#Another list item\n"
                + " -#{a:}A sub list item\n"
                + "-#And yet another one\n\n"
                + "-!And you can customize it:\n"
                + "-#{I ->:5}This is a list item\n"
                + "-#{I ->:5}Another list item\n"
                + " -#{a ~:30pt}A sub list item\n"
                + "-#{I ->:5}And yet another one\n\n";
        paragraph.addMarkup(text1, 11, BaseFont.TIMES);
        document.add(paragraph);

        document.render().save( new FileOutputStream("build/markup.pdf")).close();
    }
}
