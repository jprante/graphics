package org.xbib.graphics.imageio.plugins.png;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.imageio.plugins.png.pngj.FilterType;
import org.xbib.graphics.imageio.plugins.png.pngj.PngReader;
import org.xbib.graphics.imageio.plugins.png.pngj.chunks.PngMetadata;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class PNGWriterTest {

    @Test
    public void testWriter() throws Exception {
        PNGWriter writer = new PNGWriter();
        OutputStream out = null;
        try {
            // read test image
            BufferedImage read = ImageIO.read(new File("sample.jpeg"));
            File pngOut = new File("build/test.png");
            out = new FileOutputStream(pngOut);
            writer.writePNG(read, out, 1, FilterType.FILTER_NONE);
            BufferedImage test = ImageIO.read(pngOut);
            assertNotNull(test);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Test
    public void testTeXt() throws Exception {
        PNGWriter writer = new PNGWriter();
        OutputStream out = null;
        File pngOut = null;
        final String title = "Title";
        final String description = "Sample Description";
        final String software = "ImageIO-Ext";
        final String author = "Me";
        try {
            BufferedImage read = ImageIO.read(new File("sample.jpeg"));
            pngOut = new File("build/test.png");
            out = new FileOutputStream(pngOut);
            Map<String, String> textMetadata = new HashMap<String, String>();
            textMetadata.put("Title", title);
            textMetadata.put("Author", author);
            textMetadata.put("Software", software);
            textMetadata.put("Description", description);

            writer.writePNG(read, out, 1, FilterType.FILTER_NONE, textMetadata);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        BufferedImage test = ImageIO.read(pngOut);
        assertNotNull(test);
        try (PngReader reader = new PngReader(pngOut)) {
            reader.readSkippingAllRows();
            PngMetadata metadata = reader.getMetadata();
            assertNotNull(metadata);
            assertEquals(title, metadata.getTxtForKey("Title"));
            assertEquals(description, metadata.getTxtForKey("Description"));
            assertEquals(author, metadata.getTxtForKey("Author"));
            assertEquals(software, metadata.getTxtForKey("Software"));
        }
    }
}
