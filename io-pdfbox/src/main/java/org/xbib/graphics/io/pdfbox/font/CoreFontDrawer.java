package org.xbib.graphics.io.pdfbox.font;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

/**
 * Like {@link DefaultFontDrawer}, but tries to use core fonts
 * whenever possible. Default fonts are not embedded. You can register
 * additional font files. If no font mapping is found, Helvetica is used.
 * This will fallback to vectorized text if any kind of RTL text is rendered
 * and/or any other not supported feature is used.
 */
public class CoreFontDrawer extends DefaultFontDrawer {

    @Override
    protected PDFont mapFont(Font font, FontDrawerEnvironment env) throws IOException, FontFormatException {
        PDFont pdFont = mapToCoreFonts(font);
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
