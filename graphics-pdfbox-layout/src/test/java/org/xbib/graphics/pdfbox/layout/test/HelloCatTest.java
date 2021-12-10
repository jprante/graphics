package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.ImageElement;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

public class HelloCatTest {

    @Test
    public void test() throws Exception {
        Document document = new Document();
        Paragraph paragraph = new Paragraph();
        paragraph.addText("Hello Cat", 12, BaseFont.HELVETICA);
        document.add(paragraph);
        ImageElement imageElement = new ImageElement();
        imageElement.setImage(ImageIO.read(getClass().getResourceAsStream("cat.jpg")));
        imageElement.setScaleX(0.1f);
        imageElement.setScaleY(0.1f);
        document.add(imageElement, new VerticalLayoutHint(Alignment.LEFT, 10, 10, 10, 10, true));
        document.render().save(new FileOutputStream("build/hellocat.pdf")).close();
    }
}
