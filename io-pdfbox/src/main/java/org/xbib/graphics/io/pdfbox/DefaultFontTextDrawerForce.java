package org.xbib.graphics.io.pdfbox;

import java.text.AttributedCharacterIterator;

/**
 * Always draw using text, even if we know that we can not map the text correctly.
 */
public class DefaultFontTextDrawerForce extends DefaultFontTextDrawerFonts {

    @Override
    public boolean canDrawText(AttributedCharacterIterator iterator, FontTextDrawerEnv env) {
        return true;
    }
}
