package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.ControlElement;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.render.ColumnLayout;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LineSpacingTest {

    @Test
    public void test() throws Exception {
        String text = "*Lorem ipsum* dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                + "aliquyam erat, _sed diam_ voluptua. At vero eos et _accusam et justo_ "
                + "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata "
                + "sanctus est _Lorem ipsum dolor sit_ amet. Lorem ipsum dolor sit amet, "
                + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
                + "ut labore et dolore magna aliquyam erat, sed diam.";

        // create document without margins
        Document document = new Document();
        document.add(new ColumnLayout(2, 5));

        Paragraph left = new Paragraph();
        // no line spacing for the first line
        left.setApplyLineSpacingToFirstLine(false);
        // use a bigger line spacing to visualize the effects of line spacing more drastically
        left.setLineSpacing(1.5f);
        left.setMaxWidth(document.getPageWidth() / 2);
        left.addMarkup(text, 11, BaseFont.TIMES);
        document.add(left);
        document.add(left);
        document.add(left);
        document.add(ControlElement.NEWCOLUMN);
        Paragraph right = new Paragraph();
        right.setLineSpacing(1.5f);
        right.setMaxWidth(document.getPageWidth() / 2);
        right.addMarkup(text, 11, BaseFont.TIMES);
        document.add(right);

        document.add(right);
        document.add(right);

        final OutputStream outputStream = new FileOutputStream("build/linespacing.pdf");
        document.render().save(outputStream);

    }
}
