package org.xbib.graphics.pdfbox.layout.font;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

/**
 * In order to easy handling with fonts, this enum bundles the
 * plain/italic/bold/bold-italic variants of the three standard font types
 * {@link PDType1Font#TIMES_ROMAN Times},{@link PDType1Font#COURIER Courier} and
 * {@link PDType1Font#HELVETICA Helveticy}.
 */
public enum BaseFont implements Font {

    TIMES(PDType1Font.TIMES_ROMAN, PDType1Font.TIMES_BOLD,
            PDType1Font.TIMES_ITALIC, PDType1Font.TIMES_BOLD_ITALIC),

    COURIER(PDType1Font.COURIER, PDType1Font.COURIER_BOLD,
            PDType1Font.COURIER_OBLIQUE, PDType1Font.COURIER_BOLD_OBLIQUE),

    HELVETICA(PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD,
            PDType1Font.HELVETICA_OBLIQUE, PDType1Font.HELVETICA_BOLD_OBLIQUE);

    private final PDFont plainFont;

    private final PDFont boldFont;

    private final PDFont italicFont;

    private final PDFont boldItalicFont;

    BaseFont(PDFont plainFont, PDFont boldFont, PDFont italicFont, PDFont boldItalicFont) {
        this.plainFont = plainFont;
        this.boldFont = boldFont;
        this.italicFont = italicFont;
        this.boldItalicFont = boldItalicFont;
    }

    @Override
    public PDFont getRegularFont() {
        return plainFont;
    }

    @Override
    public PDFont getBoldFont() {
        return boldFont;
    }

    @Override
    public PDFont getItalicFont() {
        return italicFont;
    }

    @Override
    public PDFont getBoldItalicFont() {
        return boldItalicFont;
    }
}
