package org.xbib.graphics.pdfbox.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.io.IOException;
import java.text.AttributedCharacterIterator;

/**
 * Draw text using fonts.
 */
public interface FontDrawer {

    /**
     * @param iterator Has the text and all its properties
     * @param env      Environment
     * @return true when the given text can be fully drawn using fonts. return false
     * to have the text drawn as vector shapes
     * @throws IOException         when a font can not be loaded or a paint can't be applied.
     * @throws FontFormatException when the font file can not be loaded
     */
    boolean canDrawText(AttributedCharacterIterator iterator, FontDrawerEnvironment env)
            throws IOException, FontFormatException;

    /**
     * @param iterator The text with all properties
     * @param env      Environment
     * @throws IOException         when a font can not be loaded or a paint can't be applied.
     * @throws FontFormatException when the font file can not be loaded
     */
    void drawText(AttributedCharacterIterator iterator, FontDrawerEnvironment env)
            throws IOException, FontFormatException;

    FontMetrics getFontMetrics(Font f, FontDrawerEnvironment env)
            throws IOException, FontFormatException;

}
