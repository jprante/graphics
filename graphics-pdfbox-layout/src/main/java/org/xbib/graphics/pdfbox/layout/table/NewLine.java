package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;

public class NewLine implements ParagraphProcessor {

    private final Font font;

    private final float fontSize;

    NewLine(Font font, float fontSize) {
        this.font = font;
        this.fontSize = fontSize;
    }

    @Override
    public void process(Paragraph paragraph, Settings settings) {
        paragraph.add(new org.xbib.graphics.pdfbox.layout.text.NewLine(new FontDescriptor(font, fontSize)));
    }

}
