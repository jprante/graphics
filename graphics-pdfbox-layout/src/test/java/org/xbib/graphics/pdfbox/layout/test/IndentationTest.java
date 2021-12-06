package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.text.Indent;
import org.xbib.graphics.pdfbox.layout.text.SpaceUnit;
import org.xbib.graphics.pdfbox.layout.util.Enumerators.AlphabeticEnumerator;
import org.xbib.graphics.pdfbox.layout.util.Enumerators.ArabicEnumerator;
import org.xbib.graphics.pdfbox.layout.util.Enumerators.LowerCaseAlphabeticEnumerator;
import org.xbib.graphics.pdfbox.layout.util.Enumerators.LowerCaseRomanEnumerator;
import org.xbib.graphics.pdfbox.layout.util.Enumerators.RomanEnumerator;
import java.io.FileOutputStream;

public class IndentationTest {

    @Test
    public void test() throws Exception {
        String bulletOdd = getBulletCharacter(1) + " ";
        String bulletEven = getBulletCharacter(2) + " ";
        Document document = new Document(40, 60, 40, 60);
        Paragraph paragraph = new Paragraph();
        paragraph.addMarkup("This is an example for the new indent feature. Let's do some empty space indentation:\n",
                11, BaseFont.TIMES);
        paragraph.add(new Indent(50, SpaceUnit.pt));
        paragraph.addMarkup("Here we go indented.\n", 11, BaseFont.TIMES);
        paragraph.addMarkup("The Indentation holds for the rest of the paragraph, or... \n",
                11, BaseFont.TIMES);
        paragraph.add(new Indent(70, SpaceUnit.pt));
        paragraph.addMarkup("any new indent comes.\n", 11, BaseFont.TIMES);
        document.add(paragraph);
        paragraph = new Paragraph();
        paragraph.addMarkup("New paragraph, now indentation is gone. But we can indent with a label also:\n", 11, BaseFont.TIMES);
        paragraph.addIndent("This is some label", 100, SpaceUnit.pt, 11, BaseFont.TIMES);
        paragraph.addMarkup("Here we go indented.\n", 11, BaseFont.TIMES);
        paragraph.addMarkup("And again, the Indentation holds for the rest of the paragraph, or any new indent comes.\nLabels can be aligned:\n", 11, BaseFont.TIMES);
        paragraph.addIndent("Left", 100, SpaceUnit.pt, 11, BaseFont.TIMES, Alignment.LEFT);
        paragraph.addMarkup("Indent with label aligned to the left.\n", 11, BaseFont.TIMES);
        paragraph.addIndent("Center", 100, SpaceUnit.pt, 11, BaseFont.TIMES, Alignment.CENTER);
        paragraph.addMarkup("Indent with label aligned to the center.\n", 11, BaseFont.TIMES);
        paragraph.addIndent("Right", 100, SpaceUnit.pt, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("Indent with label aligned to the right.\n", 11, BaseFont.TIMES);
        document.add(paragraph);
        paragraph = new Paragraph();
        paragraph.addMarkup("So, what can you do with that? How about lists:\n", 11, BaseFont.TIMES);
        paragraph.addIndent(bulletOdd, 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("This is a list item\n", 11, BaseFont.TIMES);
        paragraph.addIndent(bulletOdd, 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("Another list item\n", 11, BaseFont.TIMES);
        paragraph.addIndent(bulletEven, 8, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("Sub list item\n", 11, BaseFont.TIMES);
        paragraph.addIndent(bulletOdd, 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("And yet another one\n", 11, BaseFont.TIMES);
        document.add(paragraph);

        paragraph = new Paragraph();
        paragraph.addMarkup("Also available with indents: Enumerators:\n", 11, BaseFont.TIMES);
        RomanEnumerator e1 = new RomanEnumerator();
        LowerCaseAlphabeticEnumerator e2 = new LowerCaseAlphabeticEnumerator();
        paragraph.addIndent(e1.next() + ". ", 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("First item\n", 11, BaseFont.TIMES);
        paragraph.addIndent(e1.next() + ". ", 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("Second item\n", 11, BaseFont.TIMES);
        paragraph.addIndent(e2.next() + ") ", 8, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("A sub item\n", 11, BaseFont.TIMES);
        paragraph.addIndent(e2.next() + ") ", 8, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("Another sub item\n", 11, BaseFont.TIMES);
        paragraph.addIndent(e1.next() + ". ", 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("Third item\n", 11, BaseFont.TIMES);
        document.add(paragraph);

        paragraph = new Paragraph();
        paragraph.addMarkup("The following types are built in:\n", 11,
                BaseFont.TIMES);
        paragraph.addIndent(new ArabicEnumerator().next() + " ", 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("ArabicEnumerator\n", 11, BaseFont.TIMES);
        paragraph.addIndent(new RomanEnumerator().next() + " ", 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("RomanEnumerator\n", 11, BaseFont.TIMES);
        paragraph.addIndent(new LowerCaseRomanEnumerator().next() + " ", 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("LowerCaseRomanEnumerator\n", 11, BaseFont.TIMES);
        paragraph.addIndent(new AlphabeticEnumerator().next() + " ", 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("AlphabeticEnumerator\n", 11, BaseFont.TIMES);
        paragraph.addIndent(new LowerCaseAlphabeticEnumerator().next() + " ", 4, SpaceUnit.em, 11, BaseFont.TIMES, Alignment.RIGHT);
        paragraph.addMarkup("LowerCaseAlphabeticEnumerator\n", 11,
                BaseFont.TIMES);
        document.add(paragraph);
        paragraph = new Paragraph();
        String text1 = "For your convenience, you can do all that much easier with markup, e.g. simple indentation\n"
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
        document.render().save(new FileOutputStream("build/indentation.pdf")).close();
    }

    private static String getBulletCharacter(final int level) {
        if (level % 2 == 1) {
            return System.getProperty("pdfbox.layout.bullet.odd", BULLET);
        }
        return System.getProperty("pdfbox.layout.bullet.even", DOUBLE_ANGLE);
    }

    private static final String BULLET = "\u2022";

    private static final String DOUBLE_ANGLE = "\u00bb";
}
