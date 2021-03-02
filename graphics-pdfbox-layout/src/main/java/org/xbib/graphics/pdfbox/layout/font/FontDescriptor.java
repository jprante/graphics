package org.xbib.graphics.pdfbox.layout.font;

import org.apache.pdfbox.pdmodel.font.PDFont;
import java.util.Objects;

/**
 * Container for a Font and size.
 */
public class FontDescriptor {

    private final Font font;

    private final float size;

    private final boolean regular;

    private final boolean bold;

    private final boolean italic;

    public FontDescriptor(Font font, float size) {
        this(font, size, false, false);
    }

    public FontDescriptor(Font font, float size, boolean bold, boolean italic) {
        this.font = font;
        this.size = size;
        this.regular = !bold && !italic;
        this.bold = bold;
        this.italic = italic;
    }

    public Font getFont() {
        return font;
    }

    public float getSize() {
        return size;
    }

    public PDFont getSelectedFont() {
        if (regular) {
            return font.getRegularFont();
        }
        if (italic) {
            return bold ? font.getBoldItalicFont() : font.getItalicFont();
        }
        if (bold) {
            return font.getBoldFont();
        }
        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return "FontDescriptor [font=" + font + ", size=" + size + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, size, regular, bold, italic);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FontDescriptor)) {
            return false;
        }
        return Objects.hashCode(obj) == Objects.hash(this);
    }
}
