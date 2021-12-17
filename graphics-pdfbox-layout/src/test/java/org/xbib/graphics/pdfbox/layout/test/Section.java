package org.xbib.graphics.pdfbox.layout.test;

import org.xbib.graphics.pdfbox.layout.element.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;

public class Section extends Paragraph {
    private final int number;

    public Section(int number) {
        super();
        this.number = number;
        addMarkup(String.format("*Section %d", number), 16, BaseFont.TIMES);
    }

    public int getNumber() {
        return number;
    }

}
