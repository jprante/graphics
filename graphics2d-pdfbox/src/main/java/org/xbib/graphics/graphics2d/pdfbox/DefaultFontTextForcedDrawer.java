package org.xbib.graphics.graphics2d.pdfbox;

import java.text.AttributedCharacterIterator;

/**
 * Always draw using text, even if we know that we can not map the text correct
 */
public class DefaultFontTextForcedDrawer extends DefaultFontTextDrawerDefaultFonts {
    @Override
    public boolean canDrawText(AttributedCharacterIterator iterator, IFontTextDrawerEnv env) {
        return true;
    }
}
