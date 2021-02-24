
package org.xbib.graphics.imageio.plugins.png;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class ImageAssert {

    public static void assertImagesEqual(BufferedImage original, BufferedImage image) {
        assertEquals(original.getWidth(), image.getWidth());
        assertEquals(original.getHeight(), image.getHeight());
        // these tests got disabled, as depending on the reader being used you can get a different
        // structure back
        // assertEquals(original.getSampleModel(), image.getSampleModel());
        // assertEquals(original.getColorModel(), image.getColorModel());
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int rgbOriginal = original.getRGB(x, y);
                int rgbActual = image.getRGB(x, y);
                if (rgbOriginal != rgbActual) {
                    fail("Comparison failed at x:" + x + ", y: " + y + ", expected "
                            + colorToString(rgbOriginal) + ", got " + colorToString(rgbActual));
                }
            }
        }
    }

    private static String colorToString(int rgb) {
        Color c = new Color(rgb);
        return "RGBA[" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ", "
                + c.getAlpha() + "]";
    }

    public static void showImage(String title, long timeOut, final BufferedImage image)
            throws InterruptedException {
        final String headless = System.getProperty("java.awt.headless", "false");
        if (!headless.equalsIgnoreCase("true")) {
            try {
                Frame frame = new Frame(title);
                frame.addWindowListener(new WindowAdapter() {

                    public void windowClosing(WindowEvent e) {
                        e.getWindow().dispose();
                    }
                });

                @SuppressWarnings("serial")
                Panel p = new Panel() {
                    {
                        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
                    }

                    public void paint(Graphics g) {
                        g.drawImage(image, 0, 0, this);
                    }

                };

                frame.add(p);
                frame.pack();
                frame.setVisible(true);

                Thread.sleep(timeOut);
                frame.dispose();
            } catch (HeadlessException exception) {
                // The test is running on a machine without X11 display. Ignore.
            }
        }
    }

}
