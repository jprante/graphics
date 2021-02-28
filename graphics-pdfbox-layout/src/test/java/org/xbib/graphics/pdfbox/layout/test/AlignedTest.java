package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.util.WordBreakerFactory;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class AlignedTest {

	@Test
    public void test() throws Exception {
        System.setProperty(WordBreakerFactory.WORD_BREAKER_CLASS_PROPERTY,
                WordBreakerFactory.LEGACY_WORD_BREAKER_CLASS_NAME);
        Document document = new Document(40, 60, 40, 60);
        Paragraph paragraph = new Paragraph();
        paragraph.addText("This is some left aligned text", 11, BaseFont.HELVETICA);
        paragraph.setAlignment(Alignment.LEFT);
        paragraph.setMaxWidth(40);
        document.add(paragraph, VerticalLayoutHint.LEFT);
        paragraph = new Paragraph();
        paragraph.addText("This is some centered text", 11, BaseFont.HELVETICA);
        paragraph.setAlignment(Alignment.CENTER);
        paragraph.setMaxWidth(40);
        document.add(paragraph, VerticalLayoutHint.CENTER);
        paragraph = new Paragraph();
        paragraph.addText("This is some right aligned text", 11, BaseFont.HELVETICA);
        paragraph.setAlignment(Alignment.RIGHT);
        paragraph.setMaxWidth(40);
        document.add(paragraph, VerticalLayoutHint.RIGHT);
        paragraph = new Paragraph();
        paragraph.addText("Text is right aligned, and paragraph centered", 11, BaseFont.HELVETICA);
        paragraph.setAlignment(Alignment.RIGHT);
        paragraph.setMaxWidth(40);
        document.add(paragraph, VerticalLayoutHint.CENTER);
        OutputStream outputStream = new FileOutputStream("build/aligned.pdf");
        document.save(outputStream);
    }
}
