package org.xbib.graphics.pdfbox.test;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.font.CoreFontDrawer;

import java.awt.Font;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FontDrawerTest {

    @Test
    public void testFontStyleMatching() {
        Font anyFont = Font.decode("Dialog");
        Font anyFontBold = anyFont.deriveFont(Font.BOLD);
        Font anyFontItalic = anyFont.deriveFont(Font.ITALIC);
        Font anyFontBoldItalic = anyFont.deriveFont(Font.BOLD | Font.ITALIC);

        Assertions.assertEquals(PDType1Font.COURIER, CoreFontDrawer.chooseMatchingCourier(anyFont));
        assertEquals(PDType1Font.COURIER_BOLD,
                CoreFontDrawer.chooseMatchingCourier(anyFontBold));
        assertEquals(PDType1Font.COURIER_OBLIQUE,
                CoreFontDrawer.chooseMatchingCourier(anyFontItalic));
        assertEquals(PDType1Font.COURIER_BOLD_OBLIQUE,
                CoreFontDrawer.chooseMatchingCourier(anyFontBoldItalic));

        assertEquals(PDType1Font.HELVETICA,
                CoreFontDrawer.chooseMatchingHelvetica(anyFont));
        assertEquals(PDType1Font.HELVETICA_BOLD,
                CoreFontDrawer.chooseMatchingHelvetica(anyFontBold));
        assertEquals(PDType1Font.HELVETICA_OBLIQUE,
                CoreFontDrawer.chooseMatchingHelvetica(anyFontItalic));
        assertEquals(PDType1Font.HELVETICA_BOLD_OBLIQUE,
                CoreFontDrawer.chooseMatchingHelvetica(anyFontBoldItalic));

        assertEquals(PDType1Font.TIMES_ROMAN, CoreFontDrawer.chooseMatchingTimes(anyFont));
        assertEquals(PDType1Font.TIMES_BOLD,
                CoreFontDrawer.chooseMatchingTimes(anyFontBold));
        assertEquals(PDType1Font.TIMES_ITALIC,
                CoreFontDrawer.chooseMatchingTimes(anyFontItalic));
        assertEquals(PDType1Font.TIMES_BOLD_ITALIC,
                CoreFontDrawer.chooseMatchingTimes(anyFontBoldItalic));
    }

    @Test
    public void testDefaultFontMapping() {
        assertEquals(PDType1Font.HELVETICA,
                CoreFontDrawer.mapToCoreFonts(Font.decode(Font.DIALOG)));
        assertEquals(PDType1Font.HELVETICA,
                CoreFontDrawer.mapToCoreFonts(Font.decode(Font.DIALOG_INPUT)));
        assertEquals(PDType1Font.HELVETICA,
                CoreFontDrawer.mapToCoreFonts(Font.decode("Arial")));
        assertEquals(PDType1Font.COURIER,
                CoreFontDrawer.mapToCoreFonts(Font.decode(Font.MONOSPACED)));
        assertEquals(PDType1Font.TIMES_ROMAN,
                CoreFontDrawer.mapToCoreFonts(Font.decode(Font.SERIF)));
        assertEquals(PDType1Font.ZAPF_DINGBATS,
                CoreFontDrawer.mapToCoreFonts(Font.decode("Dingbats")));
        assertEquals(PDType1Font.SYMBOL,
                CoreFontDrawer.mapToCoreFonts(Font.decode("Symbol")));
        assertNull(CoreFontDrawer.mapToCoreFonts(Font.decode("Georgia")));
    }

}
