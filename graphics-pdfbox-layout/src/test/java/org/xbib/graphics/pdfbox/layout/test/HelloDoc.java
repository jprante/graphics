package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class HelloDoc {

    @Test
    public void test() throws Exception {
        Document document = new Document(40, 60, 40, 60);

        Paragraph paragraph = new Paragraph();
        paragraph.addText("Hello Document", 20, BaseFont.HELVETICA);
        document.add(paragraph);
        final OutputStream outputStream = new FileOutputStream("build/hellodoc.pdf");
        document.save(outputStream);

    }
}
