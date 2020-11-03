
package org.xbib.graphics.imageio.plugins.png;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.imageio.plugins.png.pngj.FilterType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.imageio.ImageIO;

public class PngSuiteImagesTest {

    private final File sourceFile;

    public PngSuiteImagesTest(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    //@Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        List<Object[]> result = new ArrayList<Object[]>();
        File source = new File("./src/test/resources/pngsuite");
        File[] files = source.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        });
        Arrays.sort(files);
        for (File file : files) {
            result.add(new Object[]{file});
        }

        return result;
    }

    @Test
    public void testRoundTripFilterNone() throws Exception {
        BufferedImage input = ImageIO.read(sourceFile);
        roundTripPNGJ(input);
    }

    @Test
    public void testRoundTripTiledImage() throws Exception {
        BufferedImage input = ImageIO.read(sourceFile);
        roundTripPNGJ(input);
    }

    private void roundTripPNGJ(BufferedImage original) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        float quality = 4f / 9 - 1;
        new PNGWriter().writePNG(original, bos, -quality, FilterType.FILTER_NONE);
        // write the output to file for eventual visual comparison
        byte[] bytes = bos.toByteArray();
        writeToFile(new File("./build/roundTripNone", sourceFile.getName()), bytes);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(bis);
        ImageAssert.assertImagesEqual(original, image);
    }

    private void writeToFile(File file, byte[] bytes) throws IOException {
        File parent = file.getParentFile();
        parent.mkdirs();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

}
