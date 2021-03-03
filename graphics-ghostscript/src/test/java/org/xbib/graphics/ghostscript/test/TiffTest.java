package org.xbib.graphics.ghostscript.test;

import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.ghostscript.PDFRasterizer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TiffTest {

    private static final Logger logger = Logger.getLogger(TiffTest.class.getName());

    @Test
    public void readTiff() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("00000002.tif");
        BufferedImage bufferedImage1 = ImageIO.read(inputStream);
        assertTrue(bufferedImage1.getHeight() > 0);
        assertTrue(bufferedImage1.getWidth() > 0);
        inputStream = getClass().getResourceAsStream("00000003.tif");
        BufferedImage bufferedImage2 = ImageIO.read(inputStream);
        assertTrue(bufferedImage2.getHeight() > 0);
        assertTrue(bufferedImage2.getWidth() > 0);
    }

    @Test
    public void mergeTiff() throws IOException {
        Path sourceDir = Paths.get("src/test/resources/org/xbib/graphics/ghostscript/test/");
        Path targetFile = Paths.get("build/tmp.pdf");
        PDFRasterizer pdfRasterizer = new PDFRasterizer();
        int pagecount = pdfRasterizer.mergeImagesToPDF(sourceDir, targetFile, "**/*.tif");
        logger.info("pagecount = " + pagecount);
        assertEquals(2, pagecount);
    }
}
