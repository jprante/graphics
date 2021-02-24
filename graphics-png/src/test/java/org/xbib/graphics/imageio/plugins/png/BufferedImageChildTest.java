package org.xbib.graphics.imageio.plugins.png;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.imageio.plugins.png.pngj.FilterType;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class BufferedImageChildTest {

    BufferedImage getSample() {
        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = bi.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 25, 25);
        graphics.setColor(Color.BLUE);
        graphics.fillRect(25, 0, 25, 25);
        graphics.setColor(Color.YELLOW);
        graphics.fillRect(0, 25, 25, 25);
        graphics.setColor(Color.RED);
        graphics.fillRect(25, 25, 25, 25);
        graphics.dispose();
        return bi;
    }

    @Test
    public void testSmallerSameOrigin() throws Exception {
        testSubImage(0, 0, 25, 25);
    }

    @Test
    public void testSmallerTranslateX() throws Exception {
        testSubImage(25, 0, 25, 25);
    }

    @Test
    public void testSmallerTranslateY() throws Exception {
        testSubImage(0, 25, 25, 25);
    }

    @Test
    public void testSmallerTranslateXY() throws Exception {
        testSubImage(25, 25, 25, 25);
    }

    private void testSubImage(int x, int y, int w, int h) throws Exception {
        BufferedImage bi = getSample();
        // ImageAssert.showImage("Original", 2000, bi);
        BufferedImage subimage = bi.getSubimage(x, y, w, h);
        // ImageAssert.showImage("Subimage", 2000, subimage);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        float quality = 4f / 9 - 1;
        new PNGWriter().writePNG(subimage, bos, -quality, FilterType.FILTER_NONE);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        BufferedImage readBack = ImageIO.read(bis);
        // ImageAssert.showImage("ReadBack", 2000, readBack);
        ImageAssert.assertImagesEqual(subimage, readBack);
    }
}
