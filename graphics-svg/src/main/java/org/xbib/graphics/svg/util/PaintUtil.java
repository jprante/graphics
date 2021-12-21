package org.xbib.graphics.svg.util;

import org.xbib.graphics.svg.element.filtereffects.FilterOp;
import org.xbib.graphics.svg.element.RenderableElement;
import org.xbib.graphics.svg.SVGException;

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

public class PaintUtil {

    public static final boolean DEBUG_PAINT = false;

    public static void paintElement(Graphics2D g, RenderableElement element) throws SVGException, IOException {
        if (element.getCachedMask() != null
                || (element.getFilter() != null && !element.getFilter().getFilterEffects().isEmpty())) {
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
        PaintCache cache = element.getBufferCache();
        BufferedImage elementImage;
        if (cache == null || !cache.isCompatible(transform)) {
            elementImage = renderToBuffer(gg, element, transform, transformedBounds, dstBounds);
        } else {
            elementImage = cache.getImage();
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
        Point2D.Float testPoint = new Point2D.Float(1, 0);
        float xScale = getTransformScale(origin, testPoint, transform);
        testPoint.setLocation(0, 1);
        float yScale = getTransformScale(origin, testPoint, transform);
        List<FilterOp> filterOps = element.getFilter() == null
                ? Collections.emptyList()
                : element.getFilter().getFilterEffects().stream()
                .flatMap(f -> f.getOperations(dstBounds, xScale, yScale).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (FilterOp filterOp : filterOps) {
            int right = Math.max(dstBounds.x + dstBounds.width,
                    filterOp.getRequiredImageBounds().x + filterOp.getRequiredImageBounds().width);
            int bottom = Math.max(dstBounds.y + dstBounds.height,
                    filterOp.getRequiredImageBounds().y + filterOp.getRequiredImageBounds().height);
            dstBounds.x = Math.min(dstBounds.x, filterOp.getRequiredImageBounds().x);
            dstBounds.y = Math.min(dstBounds.y, filterOp.getRequiredImageBounds().y);
            dstBounds.width = right - dstBounds.x;
            dstBounds.height = bottom - dstBounds.y;
        }
        BufferedImage elementImage = PaintUtil.paintToBuffer(gg, transform, dstBounds, transformedBounds,
                element, null, true);
        for (FilterOp filterOp : filterOps) {
            elementImage = filterOp.getOp().filter(elementImage, null);
        }
        if (element.getCachedMask() != null) {
            BufferedImage maskImage = PaintUtil.paintToBuffer(gg, transform, dstBounds, transformedBounds,
                    element.getCachedMask(), Color.BLACK, false);
            Graphics2D elementGraphics = (Graphics2D) elementImage.getGraphics();
            elementGraphics.setRenderingHints(gg.getRenderingHints());
            elementGraphics.setComposite(element.getCachedMask().createMaskComposite());
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
