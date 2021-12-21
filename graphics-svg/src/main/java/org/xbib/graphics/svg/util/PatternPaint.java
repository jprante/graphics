package org.xbib.graphics.svg.util;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class PatternPaint implements Paint {

    BufferedImage source;

    AffineTransform xform;

    public PatternPaint(BufferedImage source, AffineTransform xform) {
        this.source = source;
        this.xform = xform;
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        try {
            return new PatternPaintContext(source, deviceBounds, xform, this.xform);
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTransparency() {
        return source.getColorModel().getTransparency();
    }

}
