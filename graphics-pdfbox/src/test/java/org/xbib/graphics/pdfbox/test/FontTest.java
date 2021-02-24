package org.xbib.graphics.pdfbox.test;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontTest extends PdfBoxGraphics2DTestBase {

    @Test
    public void testAntonioFont() throws IOException, FontFormatException {
        final Font antonioRegular = Font.createFont(Font.TRUETYPE_FONT,
                        PdfBoxGraphics2dTest.class.getResourceAsStream("antonio/Antonio-Regular.ttf"))
                .deriveFont(15f);
        exportGraphic("fonts", "antonio", gfx -> {
            gfx.setColor(Color.BLACK);
            gfx.setFont(antonioRegular);
            gfx.drawString("Für älter österlich, Umlauts are not always fun.", 10, 50);
        });
    }
}
