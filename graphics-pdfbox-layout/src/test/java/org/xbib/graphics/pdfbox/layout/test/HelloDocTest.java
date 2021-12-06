package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.FileOutputStream;

public class HelloDocTest {

    @Test
    public void test() throws Exception {
        Document document = new Document(40, 60, 40, 60);
        Paragraph paragraph = new Paragraph();
        paragraph.addText("Hello Document", 20, BaseFont.HELVETICA);
        document.add(paragraph);
        document.render().save(new FileOutputStream("build/hellodoc.pdf")).close();
    }
}
