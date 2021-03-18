package org.xbib.graphics.pdfbox.layout.script.commands;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.script.Command;

public class ParagraphCommand extends Command<Paragraph> {

    public ParagraphCommand(Paragraph paragraph) {
        super(paragraph);
    }

    @Override
    public String getKey() {
        return null;
    }
}
