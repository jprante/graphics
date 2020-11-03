package org.xbib.graphics.layout.pdfbox;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.layout.pdfbox.elements.Document;
import org.xbib.graphics.layout.pdfbox.elements.Paragraph;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class HelloDoc {

    @Test
    public void test() throws Exception {
        Document document = new Document(40, 60, 40, 60);

        Paragraph paragraph = new Paragraph();
        paragraph.addText("Hello Document", 20,
                PDType1Font.HELVETICA);
        document.add(paragraph);

        final OutputStream outputStream = new FileOutputStream("build/hellodoc.pdf");
        document.save(outputStream);

    }
}
