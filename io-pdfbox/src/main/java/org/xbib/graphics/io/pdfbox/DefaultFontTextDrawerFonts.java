package org.xbib.graphics.io.pdfbox;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

/**
 * Like {@link DefaultFontTextDrawer}, but tries to use default fonts
 * whenever possible. Default fonts are not embedded. You can register
 * additional font files. If no font mapping is found, Helvetica is used.
 * This will fallback to vectorized text if any kind of RTL text is rendered
 * and/or any other not supported feature is used.
 */
public class DefaultFontTextDrawerFonts extends DefaultFontTextDrawer {

    @Override
    protected PDFont mapFont(Font font, FontTextDrawerEnv env) throws IOException, FontFormatException {
        PDFont pdFont = mapDefaultFonts(font);
        if (pdFont != null) {
            return pdFont;
        }
        pdFont = super.mapFont(font, env);
        if (pdFont != null) {
            return pdFont;
        }
        return chooseMatchingHelvetica(font);
    }

}
