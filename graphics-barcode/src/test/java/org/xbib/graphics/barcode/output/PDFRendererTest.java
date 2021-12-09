package org.xbib.graphics.barcode.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.xbib.graphics.barcode.Code93;
import org.xbib.graphics.barcode.MaxiCode;
import org.xbib.graphics.barcode.AbstractSymbol;
import org.xbib.graphics.barcode.render.BarcodeGraphicsRenderer;
import org.xbib.graphics.io.vector.pdf.PDFGraphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

@DisabledOnOs(OS.MAC)
public class PDFRendererTest {

    private Locale originalDefaultLocale;

    @BeforeEach
    public void before() {
        // ensure use of correct decimal separator (period), regardless of default locale
        originalDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.GERMANY);
    }

    @AfterEach
    public void after() {
        Locale.setDefault(originalDefaultLocale);
    }

    @Test
    public void testCode93Basic() throws IOException {
        Code93 code93 = new Code93();
        code93.setContent("123456789");
        test(code93, 1, Color.WHITE, Color.BLACK, 5, "code93-basic.pdf");
    }

    @Test
    public void testCode93Margin() throws IOException {
        Code93 code93 = new Code93();
        code93.setContent("123456789");
        test(code93, 1, Color.WHITE, Color.BLACK, 20, "code93-margin-size-20.pdf");
    }

    @Test
    public void testCode93Magnification() throws IOException {
        Code93 code93 = new Code93();
        code93.setContent("123456789");
        test(code93, 2, Color.WHITE, Color.BLACK, 5, "code93-magnification-2.pdf");
    }

    @Test
    public void testCode93Colors() throws IOException {
        Code93 code93 = new Code93();
        code93.setContent("123456789");
        test(code93, 1, Color.GREEN, Color.RED, 5, "code93-colors.pdf");
    }

    @Test
    public void testCode93CustomFont() throws IOException {
        Code93 code93 = new Code93();
        code93.setFontName("Arial");
        code93.setFontSize(26);
        code93.setContent("123456789");
        test(code93, 1, Color.WHITE, Color.BLACK, 5, "code93-custom-font.pdf");
    }

    @Test
    public void testMaxiCodeBasic() throws IOException {
        MaxiCode maxicode = new MaxiCode();
        maxicode.setMode(4);
        maxicode.setContent("123456789");
        test(maxicode, 5, Color.WHITE, Color.BLACK, 5, "maxicode-basic.pdf");
    }

    private void test(AbstractSymbol symbol,
                      double scale,
                      Color paper,
                      Color ink,
                      int margin,
                      String expectationFile) throws IOException {
        symbol.setQuietZoneHorizontal(margin);
        symbol.setQuietZoneVertical(margin);
        int width = (int) (symbol.getWidth() * scale);
        int height = (int) (symbol.getHeight() * scale);
        Rectangle rectangle = new Rectangle(0, 0, width, height);
        PDFGraphics2D pdfGraphics2D = new PDFGraphics2D(rectangle);
        pdfGraphics2D.setFont(Font.decode("Dialog")); // Helvetica
        BarcodeGraphicsRenderer barcodeGraphicsRenderer = new BarcodeGraphicsRenderer(pdfGraphics2D,
                rectangle, scale, scale, paper, ink, false, false);
        barcodeGraphicsRenderer.render(symbol);
        barcodeGraphicsRenderer.close();
        byte[] actualBytes = pdfGraphics2D.getBytes();
        String actual = new String(actualBytes, StandardCharsets.UTF_8);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("build/" + expectationFile))) {
            bufferedWriter.write(actual);
        }
        BufferedReader actualReader = new BufferedReader(new StringReader(actual));
        InputStream is = getClass().getResourceAsStream(expectationFile);
        if (is != null) {
            byte[] expectedBytes = new byte[is.available()];
            is.read(expectedBytes);
            String expected = new String(expectedBytes, StandardCharsets.UTF_8);
            BufferedReader expectedReader = new BufferedReader(new StringReader(expected));
            int line = 1;
            String actualLine = actualReader.readLine();
            String expectedLine = expectedReader.readLine();
            while (actualLine != null && expectedLine != null) {
                assertEquals(expectedLine, actualLine, "Line " + line);
                actualLine = actualReader.readLine();
                expectedLine = expectedReader.readLine();
                line++;
            }
        }
    }
}
