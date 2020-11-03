package org.xbib.graphics.layout.pdfbox;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.layout.pdfbox.elements.Document;
import org.xbib.graphics.layout.pdfbox.elements.Paragraph;
import org.xbib.graphics.layout.pdfbox.elements.render.VerticalLayoutHint;
import org.xbib.graphics.layout.pdfbox.text.Alignment;
import org.xbib.graphics.layout.pdfbox.util.WordBreakerFactory;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Aligned {

	@Test
    public void test() throws Exception {
        System.setProperty(WordBreakerFactory.WORD_BREAKER_CLASS_PROPERTY,
                WordBreakerFactory.LEGACY_WORD_BREAKER_CLASS_NAME);
        Document document = new Document(40, 60, 40, 60);
        Paragraph paragraph = new Paragraph();
        paragraph.addText("This is some left aligned text", 11,
                PDType1Font.HELVETICA);
        paragraph.setAlignment(Alignment.Left);
        paragraph.setMaxWidth(40);
        document.add(paragraph, VerticalLayoutHint.LEFT);
        paragraph = new Paragraph();
        paragraph.addText("This is some centered text", 11,
                PDType1Font.HELVETICA);
        paragraph.setAlignment(Alignment.Center);
        paragraph.setMaxWidth(40);
        document.add(paragraph, VerticalLayoutHint.CENTER);
        paragraph = new Paragraph();
        paragraph.addText("This is some right aligned text", 11,
                PDType1Font.HELVETICA);
        paragraph.setAlignment(Alignment.Right);
        paragraph.setMaxWidth(40);
        document.add(paragraph, VerticalLayoutHint.RIGHT);
        paragraph = new Paragraph();
        paragraph.addText("Text is right aligned, and paragraph centered", 11,
                PDType1Font.HELVETICA);
        paragraph.setAlignment(Alignment.Right);
        paragraph.setMaxWidth(40);
        document.add(paragraph, VerticalLayoutHint.CENTER);
        OutputStream outputStream = new FileOutputStream("build/aligned.pdf");
        document.save(outputStream);
    }
}
