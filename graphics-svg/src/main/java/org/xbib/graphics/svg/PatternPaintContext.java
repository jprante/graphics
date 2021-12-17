package org.xbib.graphics.svg;

import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class PatternPaintContext implements PaintContext {

    BufferedImage source;

    Rectangle deviceBounds;

    AffineTransform xform;

    int sourceWidth;

    int sourceHeight;

    BufferedImage buf;

    public PatternPaintContext(BufferedImage source, Rectangle deviceBounds, AffineTransform userXform, AffineTransform distortXform) throws NoninvertibleTransformException {
        this.source = source;
        this.deviceBounds = deviceBounds;
        xform = distortXform.createInverse();
        xform.concatenate(userXform.createInverse());
        sourceWidth = source.getWidth();
        sourceHeight = source.getHeight();
    }

    @Override
    public void dispose() {
    }

    @Override
    public ColorModel getColorModel() {
        return source.getColorModel();
    }

    @Override
    public Raster getRaster(int x, int y, int w, int h) {
        if (buf == null || buf.getWidth() != w || buf.getHeight() != h) {
            buf = new BufferedImage(w, h, source.getType());
        }
        Point2D.Float srcPt = new Point2D.Float(), destPt = new Point2D.Float();
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                destPt.setLocation(i + x, j + y);
                xform.transform(destPt, srcPt);
                int ii = ((int) srcPt.x) % sourceWidth;
                if (ii < 0) ii += sourceWidth;
                int jj = ((int) srcPt.y) % sourceHeight;
                if (jj < 0) jj += sourceHeight;
                buf.setRGB(i, j, source.getRGB(ii, jj));
            }
        }
        return buf.getData();
    }
}
