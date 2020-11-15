package org.xbib.graphics.io.pdfbox.font;

import org.apache.pdfbox.pdmodel.font.PDFont;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.CharacterIterator;

@SuppressWarnings("serial")
public class DefaultFontMetrics extends FontMetrics {

    private final FontMetrics defaultMetrics;

    private final PDFont pdFont;

    /**
     * Creates a new {@code FontMetrics} object for finding out
     * height and width information about the specified {@code Font}
     * and specific character glyphs in that {@code Font}.
     *
     * @param font the {@code Font}
     * @see Font
     */
    protected DefaultFontMetrics(Font font, FontMetrics defaultMetrics, PDFont pdFont) {
        super(font);
        this.defaultMetrics = defaultMetrics;
        this.pdFont = pdFont;
    }

    @Override
    public int getDescent() {
        return defaultMetrics.getDescent();
    }

    @Override
    public int getHeight() {
        return defaultMetrics.getHeight();
    }

    @Override
    public int getMaxAscent() {
        return defaultMetrics.getMaxAscent();
    }

    @Override
    public int getMaxDescent() {
        return defaultMetrics.getMaxDescent();
    }

    @Override
    public boolean hasUniformLineMetrics() {
        return defaultMetrics.hasUniformLineMetrics();
    }

    @Override
    public LineMetrics getLineMetrics(String str, Graphics context) {
        return defaultMetrics.getLineMetrics(str, context);
    }

    @Override
    public LineMetrics getLineMetrics(String str, int beginIndex, int limit,
                                      Graphics context) {
        return defaultMetrics.getLineMetrics(str, beginIndex, limit, context);
    }

    @Override
    public LineMetrics getLineMetrics(char[] chars, int beginIndex, int limit,
                                      Graphics context) {
        return defaultMetrics.getLineMetrics(chars, beginIndex, limit, context);
    }

    @Override
    public LineMetrics getLineMetrics(CharacterIterator ci, int beginIndex, int limit,
                                      Graphics context) {
        return defaultMetrics.getLineMetrics(ci, beginIndex, limit, context);
    }

    @Override
    public Rectangle2D getStringBounds(String str, Graphics context) {
        return defaultMetrics.getStringBounds(str, context);
    }

    @Override
    public Rectangle2D getStringBounds(String str, int beginIndex, int limit,
                                       Graphics context) {
        return defaultMetrics.getStringBounds(str, beginIndex, limit, context);
    }

    @Override
    public Rectangle2D getStringBounds(char[] chars, int beginIndex, int limit,
                                       Graphics context) {
        return defaultMetrics.getStringBounds(chars, beginIndex, limit, context);
    }

    @Override
    public Rectangle2D getStringBounds(CharacterIterator ci, int beginIndex, int limit,
                                       Graphics context) {
        return defaultMetrics.getStringBounds(ci, beginIndex, limit, context);
    }

    @Override
    public Rectangle2D getMaxCharBounds(Graphics context) {
        return defaultMetrics.getMaxCharBounds(context);
    }

    @Override
    public int getAscent() {
        return defaultMetrics.getAscent();
    }

    @Override
    public int getMaxAdvance() {
        return defaultMetrics.getMaxAdvance();
    }

    @Override
    public int getLeading() {
        return defaultMetrics.getLeading();
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return defaultMetrics.getFontRenderContext();
    }

    @Override
    public int charWidth(char ch) {
        char[] chars = {ch};
        return charsWidth(chars, 0, chars.length);
    }

    @Override
    public int charWidth(int codePoint) {
        char[] data = Character.toChars(codePoint);
        return charsWidth(data, 0, data.length);
    }

    @Override
    public int charsWidth(char[] data, int off, int len) {
        return stringWidth(new String(data, off, len));
    }

    @Override
    public int stringWidth(String str) {
        try {
            return (int) (pdFont.getStringWidth(str) / 1000 * font.getSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            return defaultMetrics.stringWidth(str);
        }
    }

    @Override
    public int[] getWidths() {
        try {
            int[] first256Widths = new int[256];
            for (int i = 0; i < first256Widths.length; i++) {
                first256Widths[i] = (int) (pdFont.getWidth(i) / 1000 * font.getSize());
            }
            return first256Widths;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
