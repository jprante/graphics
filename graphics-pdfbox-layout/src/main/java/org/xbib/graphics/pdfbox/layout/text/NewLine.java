package org.xbib.graphics.pdfbox.layout.text;

import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;

/**
 * Control fragment that represents a new line in text. It has a (font and)
 * height in order to specify the height of an empty line.
 */
public class NewLine extends ControlFragment {

    /**
     * Creates a new line with the given font descriptor.
     *
     * @param fontDescriptor the font and size associated with this new line.
     */
    public NewLine(FontDescriptor fontDescriptor) {
        super("\n", fontDescriptor);
    }
}
