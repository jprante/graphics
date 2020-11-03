package org.xbib.graphics.chart.io.vector.util;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class AlphaToMaskOp implements BufferedImageOp {
    private final boolean inverted;

    public AlphaToMaskOp(boolean inverted) {
        this.inverted = inverted;
    }

    public AlphaToMaskOp() {
        this(false);
    }

    public boolean isInverted() {
        return inverted;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        ColorModel cm = src.getColorModel();

        if (dest == null) {
            dest = createCompatibleDestImage(src, cm);
        } else if (dest.getWidth() != src.getWidth() || dest.getHeight() != src.getHeight()) {
            throw new IllegalArgumentException("Source and destination images have different dimensions.");
        } else if (dest.getColorModel() != cm) {
            throw new IllegalArgumentException("Color models don't match.");
        }

        if (cm.hasAlpha()) {
            Raster srcRaster = src.getRaster();
            WritableRaster destRaster = dest.getRaster();

            for (int y = 0; y < srcRaster.getHeight(); y++) {
                for (int x = 0; x < srcRaster.getWidth(); x++) {
                    int argb = cm.getRGB(srcRaster.getDataElements(x, y, null));
                    int alpha = argb >>> 24;
                    if (alpha >= 127 && !isInverted() || alpha < 127 && isInverted()) {
                        argb |= 0xff000000;
                    } else {
                        argb &= 0x00ffffff;
                    }
                    destRaster.setDataElements(x, y, cm.getDataElements(argb, null));
                }
            }
        }

        return dest;
    }

    public Rectangle2D getBounds2D(BufferedImage src) {
        Rectangle2D bounds = new Rectangle2D.Double();
        bounds.setRect(src.getRaster().getBounds());
        return bounds;
    }

    public BufferedImage createCompatibleDestImage(BufferedImage src,
                                                   ColorModel destCM) {
        if (destCM == null) {
            destCM = src.getColorModel();
        }
        WritableRaster raster = destCM.createCompatibleWritableRaster(
                src.getWidth(), src.getHeight());
        boolean isRasterPremultiplied = destCM.isAlphaPremultiplied();
        Hashtable<String, Object> properties = null;
        if (src.getPropertyNames() != null) {
            properties = new Hashtable<String, Object>();
            for (String key : src.getPropertyNames()) {
                properties.put(key, src.getProperty(key));
            }
        }

        BufferedImage bimage = new BufferedImage(destCM, raster,
                isRasterPremultiplied, properties);
        src.copyData(raster);
        return bimage;
    }

    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt);
        return dstPt;
    }

    public RenderingHints getRenderingHints() {
        return null;
    }

}

