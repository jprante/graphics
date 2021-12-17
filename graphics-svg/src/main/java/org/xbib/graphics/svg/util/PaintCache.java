package org.xbib.graphics.svg.util;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class PaintCache {

    private final BufferedImage img;

    private final Rectangle bounds;

    private final AffineTransform transform;

    public PaintCache(BufferedImage img, Rectangle bounds, AffineTransform transform) {
        this.img = img;
        this.bounds = bounds;
        this.transform = transform;
    }

    public BufferedImage getImage() {
        return img;
    }

    public boolean isCompatible(AffineTransform tx) {
        return tx.getScaleX() == transform.getScaleX()
                && tx.getScaleY() == transform.getScaleY()
                && tx.getShearX() == transform.getShearX()
                && tx.getShearY() == transform.getShearY();
    }

    public Rectangle getBoundsForTransform(AffineTransform tx) {
        double dx = tx.getTranslateX() - transform.getTranslateX();
        double dy = tx.getTranslateY() - transform.getTranslateY();
        return new Rectangle((int) (bounds.x + dx), (int) (bounds.y + dy),
                bounds.width, bounds.height);
    }
}
