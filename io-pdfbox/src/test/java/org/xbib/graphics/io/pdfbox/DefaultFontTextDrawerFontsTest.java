package org.xbib.graphics.io.pdfbox;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

import java.awt.Font;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DefaultFontTextDrawerFontsTest {

    @Test
    public void testFontStyleMatching() {
        Font anyFont = Font.decode("Dialog");
        Font anyFontBold = anyFont.deriveFont(Font.BOLD);
        Font anyFontItalic = anyFont.deriveFont(Font.ITALIC);
        Font anyFontBoldItalic = anyFont.deriveFont(Font.BOLD | Font.ITALIC);

        assertEquals(PDType1Font.COURIER, DefaultFontTextDrawerFonts.chooseMatchingCourier(anyFont));
        assertEquals(PDType1Font.COURIER_BOLD,
                DefaultFontTextDrawerFonts.chooseMatchingCourier(anyFontBold));
        assertEquals(PDType1Font.COURIER_OBLIQUE,
                DefaultFontTextDrawerFonts.chooseMatchingCourier(anyFontItalic));
        assertEquals(PDType1Font.COURIER_BOLD_OBLIQUE,
                DefaultFontTextDrawerFonts.chooseMatchingCourier(anyFontBoldItalic));

        assertEquals(PDType1Font.HELVETICA,
                DefaultFontTextDrawerFonts.chooseMatchingHelvetica(anyFont));
        assertEquals(PDType1Font.HELVETICA_BOLD,
                DefaultFontTextDrawerFonts.chooseMatchingHelvetica(anyFontBold));
        assertEquals(PDType1Font.HELVETICA_OBLIQUE,
                DefaultFontTextDrawerFonts.chooseMatchingHelvetica(anyFontItalic));
        assertEquals(PDType1Font.HELVETICA_BOLD_OBLIQUE,
                DefaultFontTextDrawerFonts.chooseMatchingHelvetica(anyFontBoldItalic));

        assertEquals(PDType1Font.TIMES_ROMAN, DefaultFontTextDrawerFonts.chooseMatchingTimes(anyFont));
        assertEquals(PDType1Font.TIMES_BOLD,
                DefaultFontTextDrawerFonts.chooseMatchingTimes(anyFontBold));
        assertEquals(PDType1Font.TIMES_ITALIC,
                DefaultFontTextDrawerFonts.chooseMatchingTimes(anyFontItalic));
        assertEquals(PDType1Font.TIMES_BOLD_ITALIC,
                DefaultFontTextDrawerFonts.chooseMatchingTimes(anyFontBoldItalic));
    }

    @Test
    public void testDefaultFontMapping() {
        assertEquals(PDType1Font.HELVETICA,
                DefaultFontTextDrawerFonts.mapDefaultFonts(Font.decode(Font.DIALOG)));
        assertEquals(PDType1Font.HELVETICA,
                DefaultFontTextDrawerFonts.mapDefaultFonts(Font.decode(Font.DIALOG_INPUT)));
        assertEquals(PDType1Font.HELVETICA,
                DefaultFontTextDrawerFonts.mapDefaultFonts(Font.decode("Arial")));
        assertEquals(PDType1Font.COURIER,
                DefaultFontTextDrawerFonts.mapDefaultFonts(Font.decode(Font.MONOSPACED)));
        assertEquals(PDType1Font.TIMES_ROMAN,
                DefaultFontTextDrawerFonts.mapDefaultFonts(Font.decode(Font.SERIF)));
        assertEquals(PDType1Font.ZAPF_DINGBATS,
                DefaultFontTextDrawerFonts.mapDefaultFonts(Font.decode("Dingbats")));
        assertEquals(PDType1Font.SYMBOL,
                DefaultFontTextDrawerFonts.mapDefaultFonts(Font.decode("Symbol")));
        assertNull(DefaultFontTextDrawerFonts.mapDefaultFonts(Font.decode("Georgia")));
    }

}
