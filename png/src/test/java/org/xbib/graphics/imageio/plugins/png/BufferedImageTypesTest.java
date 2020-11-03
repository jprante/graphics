package org.xbib.graphics.imageio.plugins.png;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.imageio.plugins.png.pngj.FilterType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.imageio.ImageIO;

public class BufferedImageTypesTest {

    static final int WIDTH = 1024;

    static final int HEIGTH = 1024;

    static final int STROKE_WIDTH = 30;

    static final int LINES = 200;

    BufferedImage image;

    String name;

    public BufferedImageTypesTest(String name, int imageType) {
        this.name = name;
        image = new BufferedImage(WIDTH, HEIGTH, imageType);
        new SampleImagePainter().paintImage(image);
    }

    //@Parameters(name = "{0}")
    public static Collection<Object[]> parameters() throws Exception {
        String[] types = new String[]{"4BYTE_ABGR", "INT_ARGB", "3BYTE_BGR", "INT_BGR",
                "INT_RGB", "BYTE_INDEXED", "BYTE_GRAY"};
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (String type : types) {
            Field field = BufferedImage.class.getDeclaredField("TYPE_" + type);
            int imageType = (Integer) field.get(null);
            parameters.add(new Object[]{type.toLowerCase(), imageType});
        }
        return parameters;
    }

    @Test
    public void compareImage() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        float quality = 4f / 9 - 1;
        new PNGWriter().writePNG(image, bos, -quality, FilterType.FILTER_NONE);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        BufferedImage readBack = ImageIO.read(bis);

        boolean success = false;
        try {
            ImageAssert.assertImagesEqual(image, readBack);
            success = true;
        } finally {
            if (!success) {
                ImageIO.write(image, "PNG", new File("./build/" + name + "_expected.png"));
                ImageIO.write(readBack, "PNG", new File("./build/" + name + "_actual.png"));
            }
        }
    }
}
