package org.xbib.graphics.svg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BufferPainter {
    public static final boolean DEBUG_PAINT = false;

    public static class Cache {
        private final BufferedImage img;
        private final Rectangle bounds;
        private final AffineTransform transform;

        public Cache(BufferedImage img, Rectangle bounds, AffineTransform transform) {
            this.img = img;
            this.bounds = bounds;
            this.transform = transform;
        }

        boolean isCompatible(AffineTransform tx) {
            return tx.getScaleX() == transform.getScaleX()
                    && tx.getScaleY() == transform.getScaleY()
                    && tx.getShearX() == transform.getShearX()
                    && tx.getShearY() == transform.getShearY();
        }

        Rectangle getBoundsForTransform(AffineTransform tx) {
            double dx = tx.getTranslateX() - transform.getTranslateX();
            double dy = tx.getTranslateY() - transform.getTranslateY();
            return new Rectangle((int) (bounds.x + dx), (int) (bounds.y + dy),
                    bounds.width, bounds.height);
        }
    }

    public static void paintElement(Graphics2D g, RenderableElement element) throws SVGException, IOException {
        if (element.cachedMask != null
                || (element.filter != null && !element.filter.filterEffects.isEmpty())) {
            renderElement(g, element);
        } else {
            element.doRender(g);
        }
    }

    private static float getTransformScale(Point2D.Float origin, Point2D.Float testPoint,
                                           AffineTransform transform) {
        transform.transform(testPoint, testPoint);
        float dx = testPoint.x - origin.x;
        float dy = testPoint.y - origin.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static void renderElement(Graphics2D g, RenderableElement element) throws SVGException, IOException {
        AffineTransform transform = g.getTransform();
        Graphics2D gg = (Graphics2D) g.create();
        Rectangle elementBounds = element.getBoundingBox().getBounds();
        Rectangle transformedBounds = transform.createTransformedShape(elementBounds).getBounds();
        Rectangle dstBounds = new Rectangle(transformedBounds);
        Cache cache = element.getBufferCache();
        BufferedImage elementImage;
        if (cache == null || !cache.isCompatible(transform)) {
            elementImage = renderToBuffer(gg, element, transform, transformedBounds, dstBounds);
        } else {
            elementImage = cache.img;
            dstBounds.setBounds(cache.getBoundsForTransform(transform));
        }
        gg.setTransform(new AffineTransform());
        gg.drawImage(elementImage, dstBounds.x, dstBounds.y, null);
        if (DEBUG_PAINT) {
            gg.setColor(Color.GREEN);
            gg.drawRect(dstBounds.x, dstBounds.y, dstBounds.width, dstBounds.height);
            if (!dstBounds.equals(transformedBounds)) {
                gg.setColor(Color.PINK);
                gg.drawRect(transformedBounds.x, transformedBounds.y, transformedBounds.width, transformedBounds.height);
            }
        }
        gg.dispose();
    }

    private static BufferedImage renderToBuffer(Graphics2D gg, RenderableElement element,
                                                AffineTransform transform, Rectangle transformedBounds,
                                                Rectangle dstBounds) throws SVGException, IOException {
        Point2D.Float origin = new Point2D.Float(0, 0);
        transform.transform(origin, origin);

        // As filter operations are commonly implemented using convolutions they need to be
        // aware of any possible scaling to compensate for it in their kernel size.
        Point2D.Float testPoint = new Point2D.Float(1, 0);
        float xScale = getTransformScale(origin, testPoint, transform);
        testPoint.setLocation(0, 1);
        float yScale = getTransformScale(origin, testPoint, transform);

        List<FilterEffects.FilterOp> filterOps = element.filter == null
                ? Collections.emptyList()
                : element.filter.filterEffects.stream()
                .flatMap(f -> f.getOperations(dstBounds, xScale, yScale).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (FilterEffects.FilterOp filterOp : filterOps) {
            int right = Math.max(dstBounds.x + dstBounds.width,
                    filterOp.requiredImageBounds.x + filterOp.requiredImageBounds.width);
            int bottom = Math.max(dstBounds.y + dstBounds.height,
                    filterOp.requiredImageBounds.y + filterOp.requiredImageBounds.height);
            dstBounds.x = Math.min(dstBounds.x, filterOp.requiredImageBounds.x);
            dstBounds.y = Math.min(dstBounds.y, filterOp.requiredImageBounds.y);
            dstBounds.width = right - dstBounds.x;
            dstBounds.height = bottom - dstBounds.y;
        }


        BufferedImage elementImage = BufferPainter.paintToBuffer(gg, transform, dstBounds, transformedBounds,
                element, null, true);

        for (FilterEffects.FilterOp filterOp : filterOps) {
            elementImage = filterOp.op.filter(elementImage, null);
        }

        if (element.cachedMask != null) {
            BufferedImage maskImage = BufferPainter.paintToBuffer(gg, transform, dstBounds, transformedBounds,
                    element.cachedMask, Color.BLACK, false);
            Graphics2D elementGraphics = (Graphics2D) elementImage.getGraphics();
            elementGraphics.setRenderingHints(gg.getRenderingHints());
            elementGraphics.setComposite(element.cachedMask.createMaskComposite());
            elementGraphics.drawImage(maskImage, 0, 0, null);
            elementGraphics.dispose();
        }
        return elementImage;
    }

    public static BufferedImage paintToBuffer(Graphics2D g, AffineTransform transform,
                                              Rectangle srcBounds, RenderableElement element,
                                              Color bgColor) throws SVGException, IOException {
        return paintToBuffer(g, transform, srcBounds, srcBounds, element, bgColor, false);
    }

    public static BufferedImage paintToBuffer(Graphics2D g, AffineTransform transform,
                                              Rectangle dstBounds, Rectangle srcBounds,
                                              RenderableElement element,
                                              Color bgColor, boolean preMultiplied) throws SVGException, IOException {
        int type = preMultiplied
                ? BufferedImage.TYPE_INT_ARGB_PRE
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage img = new BufferedImage(dstBounds.width, dstBounds.height, type);
        Graphics2D imgGraphics = (Graphics2D) img.getGraphics();
        if (g != null) {
            imgGraphics.setRenderingHints(g.getRenderingHints());
        } else if (bgColor != null) {
            imgGraphics.setColor(bgColor);
            imgGraphics.fillRect(0, 0, img.getWidth(), img.getHeight());
        }
        int xRelative = srcBounds.x - dstBounds.x;
        int yRelative = srcBounds.y - dstBounds.y;
        imgGraphics.translate(xRelative, yRelative);
        imgGraphics.clipRect(0, 0, srcBounds.width, srcBounds.height);
        imgGraphics.translate(-srcBounds.x, -srcBounds.y);
        imgGraphics.transform(transform);
        element.doRender(imgGraphics);
        imgGraphics.dispose();
        return img;
    }
}
