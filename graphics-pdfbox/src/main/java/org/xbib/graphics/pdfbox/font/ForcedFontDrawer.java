package org.xbib.graphics.pdfbox.font;

import java.text.AttributedCharacterIterator;

/**
 * Always draw using text, even if we know that we can not map the text correctly.
 */
public class ForcedFontDrawer extends CoreFontDrawer {

    @Override
    public boolean canDrawText(AttributedCharacterIterator iterator, FontDrawerEnvironment env) {
        return true;
    }
}
