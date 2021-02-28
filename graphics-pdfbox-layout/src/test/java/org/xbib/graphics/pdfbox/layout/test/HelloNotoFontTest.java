package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.PageFormats;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.NotoSansFont;
import org.xbib.graphics.pdfbox.layout.text.Indent;
import org.xbib.graphics.pdfbox.layout.text.SpaceUnit;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class HelloNotoFontTest {

    @Test
    public void test() throws Exception {
        Document document = new Document(PageFormats.A4_PORTRAIT);
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Indent(32, SpaceUnit.pt));
        paragraph.addMarkup("Hello Noto Regular\n", 12, new NotoSansFont(document.getPDDocument()));
        paragraph.addMarkup("*Hello Noto Bold*\n", 12, new NotoSansFont(document.getPDDocument()));
        paragraph.addMarkup("_Hello Noto Italic_\n", 12, new NotoSansFont(document.getPDDocument()));
        paragraph.addMarkup("*_Hello Noto Bold Italic_*\n", 12, new NotoSansFont(document.getPDDocument()));
        document.add(paragraph);
        final OutputStream outputStream = new FileOutputStream("build/hellonotofont.pdf");
        document.save(outputStream);
    }
}
