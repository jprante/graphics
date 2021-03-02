package org.xbib.graphics.pdfbox.layout.boxable;

import org.apache.pdfbox.pdmodel.font.PDFont;
import java.io.IOException;

class TextToken extends Token {

    private PDFont cachedWidthFont;

    private float cachedWidth;

    TextToken(TokenType type, String data) {
        super(type, data);
    }

    @Override
    public float getWidth(PDFont font) throws IOException {
        if (font == cachedWidthFont) {
            return cachedWidth;
        }
        cachedWidth = super.getWidth(font);
        cachedWidthFont = font;
        return cachedWidth;
    }
}
