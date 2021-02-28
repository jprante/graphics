package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.ImageElement;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.VerticalSpacer;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.text.Position;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;

public class Letter {

    @Test
    public void test() throws Exception {
        float hMargin = 40;
        float vMargin = 50;
        Document document = new Document(hMargin, hMargin, vMargin, vMargin);

        ImageElement image;
        if (new File("arrow.png").exists()) {
            BufferedImage arrowImage = ImageIO.read(new File("arrow.png"));
            image = new ImageElement(arrowImage);
        } else {
            BufferedImage arrowImage = ImageIO.read(getClass().getResourceAsStream("arrow.png"));
            image = new ImageElement(arrowImage);
        }
        image.setWidth(image.getWidth() / 7);
        image.setHeight(image.getHeight() / 7);
        document.add(image, new VerticalLayoutHint(Alignment.RIGHT, 0, 0, 0, 0, true));

        document.add(new VerticalSpacer(100));

        Paragraph paragraph = new Paragraph();
        paragraph.addText("Blubberhausen, 01.04.2016", 11, BaseFont.HELVETICA);
        document.add(paragraph, new VerticalLayoutHint(Alignment.RIGHT, 0, 0,
                0, 0, true));

        paragraph = new Paragraph();
        String address = "Ralf Stuckert\nAm Hollergraben 24\n67346 Blubberhausen";
        paragraph.addText(address, 11, BaseFont.HELVETICA);
        document.add(paragraph);

        paragraph = new Paragraph();
        paragraph.addMarkup("*Labore et dolore magna aliquyam erat*", 11, BaseFont.HELVETICA);
        document.add(paragraph, new VerticalLayoutHint(Alignment.LEFT, 0, 0,
                40, 20));

        String text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                + "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
                + "duo dolores et ea rebum.\n\nStet clita kasd gubergren, no sea takimata "
                + "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
                + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
                + "At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
                + "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n";
        paragraph = new Paragraph();
        paragraph.addMarkup(text, 11, BaseFont.HELVETICA);
        document.add(paragraph);

        document.add(paragraph);

        paragraph = new Paragraph();
        paragraph.addMarkup("Dolore magna aliquyam erat\nRalf Stuckert", 11,
                BaseFont.HELVETICA);
        document.add(paragraph, new VerticalLayoutHint(Alignment.LEFT, 60, 0,
                40, 0));

        paragraph = new Paragraph();
        paragraph.addMarkup("*Sanctus est:* Lorem ipsum dolor consetetur "
                        + "sadipscing sed diam nonumy eirmod tempor invidunt", 6,
                BaseFont.TIMES);
        paragraph.setAbsolutePosition(new Position(hMargin, vMargin));
        document.add(paragraph);

        final OutputStream outputStream = new FileOutputStream("build/letter.pdf");
        document.save(outputStream);

    }
}
