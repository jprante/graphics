package org.xbib.graphics.io.vector.util;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import javax.swing.ImageIcon;

public class ImageUtil {


    /**
     * Converts an arbitrary image to a {@code BufferedImage}.
     *
     * @param image Image that should be converted.
     * @return a buffered image containing the image pixels, or the original
     * instance if the image already was of type {@code BufferedImage}.
     */
    public static BufferedImage toBufferedImage(RenderedImage image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        ColorModel cm = image.getColorModel();
        WritableRaster raster = cm.createCompatibleWritableRaster(image.getWidth(), image.getHeight());
        boolean isRasterPremultiplied = cm.isAlphaPremultiplied();
        Hashtable<String, Object> properties = null;
        if (image.getPropertyNames() != null) {
            properties = new Hashtable<>();
            for (String key : image.getPropertyNames()) {
                properties.put(key, image.getProperty(key));
            }
        }
        BufferedImage bimage = new BufferedImage(cm, raster, isRasterPremultiplied, properties);
        image.copyData(raster);
        return bimage;
    }

    /**
     * This method returns a buffered image with the contents of an image.
     * Taken from http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     *
     * @param image Image to be converted
     * @return a buffered image with the contents of the specified image
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();
        boolean hasAlpha = hasAlpha(image);
        BufferedImage bimage;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.TRANSLUCENT;
            }
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            bimage = null;
        }
        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    /**
     * This method returns {@code true} if the specified image has the
     * possibility to store transparent pixels.
     * Inspired by http://www.exampledepot.com/egs/java.awt.image/HasAlpha.html
     *
     * @param image Image that should be checked for alpha channel.
     * @return {@code true} if the specified image can have transparent pixels,
     * {@code false} otherwise
     */
    public static boolean hasAlpha(Image image) {
        ColorModel cm;
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            cm = bimage.getColorModel();
        } else {
            PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
            try {
                pg.grabPixels();
            } catch (InterruptedException e) {
                return false;
            }
            cm = pg.getColorModel();
        }
        return cm.hasAlpha();
    }
}
