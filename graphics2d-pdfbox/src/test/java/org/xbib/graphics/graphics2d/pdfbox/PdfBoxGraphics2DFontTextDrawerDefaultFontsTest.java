package org.xbib.graphics.graphics2d.pdfbox;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

import java.awt.Font;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PdfBoxGraphics2DFontTextDrawerDefaultFontsTest {

    @Test
    public void testFontStyleMatching() {
        Font anyFont = Font.decode("Dialog");
        Font anyFontBold = anyFont.deriveFont(Font.BOLD);
        Font anyFontItalic = anyFont.deriveFont(Font.ITALIC);
        Font anyFontBoldItalic = anyFont.deriveFont(Font.BOLD | Font.ITALIC);

        assertEquals(PDType1Font.COURIER, DefaultFontTextDrawerDefaultFonts.chooseMatchingCourier(anyFont));
        assertEquals(PDType1Font.COURIER_BOLD,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingCourier(anyFontBold));
        assertEquals(PDType1Font.COURIER_OBLIQUE,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingCourier(anyFontItalic));
        assertEquals(PDType1Font.COURIER_BOLD_OBLIQUE,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingCourier(anyFontBoldItalic));

        assertEquals(PDType1Font.HELVETICA,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingHelvetica(anyFont));
        assertEquals(PDType1Font.HELVETICA_BOLD,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingHelvetica(anyFontBold));
        assertEquals(PDType1Font.HELVETICA_OBLIQUE,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingHelvetica(anyFontItalic));
        assertEquals(PDType1Font.HELVETICA_BOLD_OBLIQUE,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingHelvetica(anyFontBoldItalic));

        assertEquals(PDType1Font.TIMES_ROMAN, DefaultFontTextDrawerDefaultFonts.chooseMatchingTimes(anyFont));
        assertEquals(PDType1Font.TIMES_BOLD,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingTimes(anyFontBold));
        assertEquals(PDType1Font.TIMES_ITALIC,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingTimes(anyFontItalic));
        assertEquals(PDType1Font.TIMES_BOLD_ITALIC,
                DefaultFontTextDrawerDefaultFonts.chooseMatchingTimes(anyFontBoldItalic));
    }

    @Test
    public void testDefaultFontMapping() {
        assertEquals(PDType1Font.HELVETICA,
                DefaultFontTextDrawerDefaultFonts.mapDefaultFonts(Font.decode(Font.DIALOG)));
        assertEquals(PDType1Font.HELVETICA,
                DefaultFontTextDrawerDefaultFonts.mapDefaultFonts(Font.decode(Font.DIALOG_INPUT)));
        assertEquals(PDType1Font.HELVETICA,
                DefaultFontTextDrawerDefaultFonts.mapDefaultFonts(Font.decode("Arial")));

        assertEquals(PDType1Font.COURIER,
                DefaultFontTextDrawerDefaultFonts.mapDefaultFonts(Font.decode(Font.MONOSPACED)));

        assertEquals(PDType1Font.TIMES_ROMAN,
                DefaultFontTextDrawerDefaultFonts.mapDefaultFonts(Font.decode(Font.SERIF)));

        assertEquals(PDType1Font.ZAPF_DINGBATS,
                DefaultFontTextDrawerDefaultFonts.mapDefaultFonts(Font.decode("Dingbats")));

        assertEquals(PDType1Font.SYMBOL,
                DefaultFontTextDrawerDefaultFonts.mapDefaultFonts(Font.decode("Symbol")));

        assertNull(DefaultFontTextDrawerDefaultFonts.mapDefaultFonts(Font.decode("Georgia")));
    }

}