package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Frame;
import org.xbib.graphics.pdfbox.layout.elements.PageFormats;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.shape.Ellipse;
import org.xbib.graphics.pdfbox.layout.shape.Rect;
import org.xbib.graphics.pdfbox.layout.shape.RoundRect;
import org.xbib.graphics.pdfbox.layout.shape.Stroke;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FramesTest {

    @Test
    public void test() throws Exception {
        String text1 = "{color:#ffffff}Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                + "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
                + "duo dolores et ea rebum.\n\nStet clita kasd gubergren, no sea takimata "
                + "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
                + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
                + "At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

        String text2 = "At *vero eos et accusam* et justo duo dolores et ea rebum. "
                + "Stet clita kasd gubergren, no sea takimata.\n\n"
                + "Sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
                + "_consetetur sadipscing elitr_, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
                + "eos et _accusam et *justo* duo dolores_ et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

        Document document = new Document(PageFormats.A5_PORTRAIT);

        Paragraph paragraph = new Paragraph();
        paragraph.addMarkup("Am I living in a box?", 11, BaseFont.TIMES);
        Frame frame = new Frame(paragraph);
        frame.setShape(new Rect());
        frame.setBorder(Color.black, new Stroke());
        frame.setPadding(10, 10, 5, 5);
        frame.setMargin(40, 40, 20, 10);
        document.add(frame, VerticalLayoutHint.CENTER);

        paragraph = new Paragraph();
        paragraph.addMarkup(text1, 11, BaseFont.TIMES);
        frame = new Frame(paragraph, 200f, null);
        frame.setShape(new Rect());
        frame.setBackgroundColor(Color.black);
        frame.setPadding(10, 10, 5, 5);
        frame.setMargin(40, 40, 20, 10);
        document.add(frame);

        paragraph = new Paragraph();
        paragraph.addMarkup("{color:#aa00aa}*Ain't no rectangle*", 22, BaseFont.HELVETICA);
        paragraph.setAlignment(Alignment.CENTER);
        frame = new Frame(paragraph, 300f, 100f);
        frame.setShape(new Ellipse());
        frame.setBorder(Color.green, new Stroke(2));
        frame.setBackgroundColor(Color.pink);
        frame.setPadding(50, 0, 35, 0);
//	frame.setMargin(30, 30, 20, 10);
        document.add(frame);

        paragraph = new Paragraph();
        paragraph.addMarkup("Frames also paginate, see here:\n\n", 13, BaseFont.TIMES);
        paragraph.addMarkup(text2, 11, BaseFont.TIMES);
        paragraph.addMarkup(text2, 11, BaseFont.TIMES);
        frame = new Frame(paragraph, null, null);
        frame.setShape(new RoundRect(10));
        frame.setBorder(Color.magenta, new Stroke(3));
        frame.setBackgroundColor(new Color(255, 240, 180));
        frame.setPadding(20, 15, 10, 15);
        frame.setMargin(50, 50, 20, 10);

        paragraph = new Paragraph();
        paragraph.addMarkup(text2, 11, BaseFont.TIMES);
        paragraph.addMarkup(text2, 11, BaseFont.TIMES);
        frame.add(paragraph);

        document.add(frame);

        final OutputStream outputStream = new FileOutputStream("build/frames.pdf");
        document.save(outputStream);

    }

}
