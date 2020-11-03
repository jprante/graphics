package org.xbib.graphics.imageio.plugins.png;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.imageio.plugins.png.pngj.FilterType;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;

public class CustomUShortImageTypesTest {

    private final int nbits;
    private final int size;

    public CustomUShortImageTypesTest(int nbits, int size) {
        this.nbits = nbits;
        this.size = size;
    }

    //@Parameters(name = "bits{0}/size{1}")
    public static Collection<Object[]> parameters() {
        List<Object[]> result = new ArrayList<Object[]>();
        for (int nbits : new int[]{1, 2, 4, 8, 16}) {
            for (int size = 1; size <= 32; size++) {
                result.add(new Object[]{nbits, size});
            }
        }

        return result;
    }

    @Test
    public void testCustomUShortImage() throws Exception {
        BufferedImage bi = ImageTypeSpecifier.createGrayscale(nbits, DataBuffer.TYPE_USHORT, false)
                .createBufferedImage(size, size);
        Graphics2D graphics = bi.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 16, 32);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(16, 0, 16, 32);
        graphics.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        float quality = 5f / 9 - 1;
        new PNGWriter().writePNG(bi, bos, -quality, FilterType.FILTER_NONE);

        BufferedImage read = ImageIO.read(new ByteArrayInputStream(bos.toByteArray()));
        ImageAssert.assertImagesEqual(bi, read);
    }
}
