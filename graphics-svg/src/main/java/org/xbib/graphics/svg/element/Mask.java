package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.element.Group;
import org.xbib.graphics.svg.element.RenderableElement;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.util.PaintUtil;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.List;

public class Mask extends Group {

    public static final String TAG_NAME = "mask";

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public void render(Graphics2D g) {
    }

    public Composite createMaskComposite() {
        return new MaskComposite();
    }

    @Override
    public void pick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) {
    }

    @Override
    public void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) {
    }

    public void pickElement(Point2D point, boolean boundingBox,
                            List<List<SVGElement>> retVec, RenderableElement element) throws SVGException, IOException {
        if (boundingBox) {
            element.doPick(point, true, retVec);
        } else {
            Rectangle pickPoint = new Rectangle((int) point.getX(), (int) point.getY(), 1, 1);
            BufferedImage img = PaintUtil.paintToBuffer(null, new AffineTransform(), pickPoint, this, Color.BLACK);
            if (luminanceToAlpha(img.getRGB(0, 0)) > 0) {
                element.doPick(point, false, retVec);
            }
        }
    }

    public void pickElement(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox,
                            List<List<SVGElement>> retVec, RenderableElement element) throws SVGException, IOException {
        if (pickArea.isEmpty()) {
            return;
        }
        if (boundingBox) {
            element.doPick(pickArea, ltw, true, retVec);
        } else {
            Area transformedBounds = new Area(ltw.createTransformedShape(element.getBoundingBox()));
            transformedBounds.intersect(new Area(pickArea));
            if (transformedBounds.isEmpty()) {
                return;
            }
            Rectangle pickRect = transformedBounds.getBounds();
            if (pickRect.isEmpty()) {
                return;
            }
            BufferedImage maskArea = PaintUtil.paintToBuffer(null, ltw, pickRect, this, Color.BLACK);
            if (hasVisiblePixel(maskArea)) {
                element.doPick(pickArea, ltw, false, retVec);
            }
        }
    }

    private boolean hasVisiblePixel(BufferedImage img) {
        Raster raster = img.getRaster();
        int x = raster.getMinX();
        int w = raster.getWidth();
        int y = raster.getMinY();
        int h = raster.getHeight();
        int[] srcPix = raster.getPixels(x, y, w, h, (int[]) null);
        boolean hasVisiblePixel = false;
        for (int i = 0; i < srcPix.length; i += 4) {
            int sr = srcPix[i];
            int sg = srcPix[i + 1];
            int sb = srcPix[i + 2];
            if (luminanceToAlpha(sr, sg, sb) > 0) {
                hasVisiblePixel = true;
                break;
            }
        }
        return hasVisiblePixel;
    }

    private static double luminanceToAlpha(int rgb) {
        return luminanceToAlpha((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    private static double luminanceToAlpha(int r, int g, int b) {
        return 0.2125 * r + 0.7154 * g + 0.0721 * b;
    }

    private static class MaskComposite implements Composite, CompositeContext {

        @Override
        public CompositeContext createContext(ColorModel srcColorModel,
                                              ColorModel dstColorModel, RenderingHints hints) {
            return this;
        }

        @Override
        public void dispose() {
        }

        public void composeRGB(int[] src, int[] dst) {
            int w = src.length;

            for (int i = 0; i < w; i += 4) {
                int sr = src[i];
                int sg = src[i + 1];
                int sb = src[i + 2];
                int da = dst[i + 3];
                double luminance = luminanceToAlpha(sr, sg, sb) / 255d;
                da *= luminance;
                dst[i + 3] = Math.min(255, Math.max(0, da));
            }
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            assert dstIn == dstOut;
            assert src.getNumBands() == dstIn.getNumBands();
            int x = dstOut.getMinX();
            int w = dstOut.getWidth();
            int y = dstOut.getMinY();
            int h = dstOut.getHeight();
            int[] srcPix = src.getPixels(x, y, w, h, (int[]) null);
            int[] dstPix = dstIn.getPixels(x, y, w, h, (int[]) null);
            composeRGB(srcPix, dstPix);
            dstOut.setPixels(x, y, w, h, dstPix);
        }
    }
}
