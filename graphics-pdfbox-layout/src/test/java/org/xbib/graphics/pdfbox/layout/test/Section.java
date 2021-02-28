package org.xbib.graphics.pdfbox.layout.test;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import java.io.IOException;

public class Section extends Paragraph {
    private final int number;

    public Section(int number) throws IOException {
        super();
        this.number = number;
        addMarkup(String.format("*Section %d", number), 16, BaseFont.TIMES);
    }

    public int getNumber() {
        return number;
    }

}
