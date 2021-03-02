package org.xbib.graphics.pdfbox.layout.text;

import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;

/**
 * A NewLine introduced by wrapping. This interface is useful for detecting
 * new-lines not contained in the original text.
 */
public class WrappingNewLine extends NewLine {

    /**
     * See {@link NewLine#NewLine(FontDescriptor)}.
     *
     * @param fontDescriptor the font and size associated with this new line.
     */
    public WrappingNewLine(FontDescriptor fontDescriptor) {
        super(fontDescriptor);
    }
}
