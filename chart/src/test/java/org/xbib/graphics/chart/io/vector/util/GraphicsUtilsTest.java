package org.xbib.graphics.chart.io.vector.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.awt.Font;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.lang.reflect.InvocationTargetException;

/**
 * On Linux, the package msttcorefonts need to be installed
 */
public class GraphicsUtilsTest {
    private static final double DELTA = 1e-15;

    private static void assertShapeEquals(Shape expected, Shape actual) {
        if ((expected instanceof Line2D) && (actual instanceof Line2D)) {
            assertEquals(((Line2D) expected).getP1(), ((Line2D) actual).getP1());
            assertEquals(((Line2D) expected).getP2(), ((Line2D) actual).getP2());
        } else if ((expected instanceof Polygon) && (actual instanceof Polygon)) {
            int n = ((Polygon) actual).npoints;
            assertEquals(((Polygon) expected).npoints, n);
            if (n > 0) {
                assertArrayEquals(((Polygon) expected).xpoints, ((Polygon) actual).xpoints);
                assertArrayEquals(((Polygon) expected).ypoints, ((Polygon) actual).ypoints);
            }
        } else if ((expected instanceof QuadCurve2D) && (actual instanceof QuadCurve2D)) {
            assertEquals(((QuadCurve2D) expected).getP1(), ((QuadCurve2D) actual).getP1());
            assertEquals(((QuadCurve2D) expected).getCtrlPt(), ((QuadCurve2D) actual).getCtrlPt());
            assertEquals(((QuadCurve2D) expected).getP2(), ((QuadCurve2D) actual).getP2());
        } else if ((expected instanceof CubicCurve2D) && (actual instanceof CubicCurve2D)) {
            assertEquals(((CubicCurve2D) expected).getP1(), ((CubicCurve2D) actual).getP1());
            assertEquals(((CubicCurve2D) expected).getCtrlP1(), ((CubicCurve2D) actual).getCtrlP1());
            assertEquals(((CubicCurve2D) expected).getCtrlP2(), ((CubicCurve2D) actual).getCtrlP2());
            assertEquals(((CubicCurve2D) expected).getP2(), ((CubicCurve2D) actual).getP2());
        } else if ((expected instanceof Path2D) && (actual instanceof Path2D)) {
            PathIterator itExpected = expected.getPathIterator(null);
            PathIterator itActual = actual.getPathIterator(null);
            double[] segmentExpected = new double[6];
            double[] segmentActual = new double[6];
            for (; !itExpected.isDone() || !itActual.isDone(); itExpected.next(), itActual.next()) {
                assertEquals(itExpected.getWindingRule(), itActual.getWindingRule());
                itExpected.currentSegment(segmentExpected);
                itActual.currentSegment(segmentActual);
                assertArrayEquals(segmentExpected, segmentActual, DELTA);
            }
        } else {
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testToBufferedImage() {
        Image[] images = {
                new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB),
                new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB),
                Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(
                        new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB).getSource(),
                        new RGBImageFilter() {
                            @Override
                            public int filterRGB(int x, int y, int rgb) {
                                return rgb & 0xff;
                            }
                        }
                ))
        };

        for (Image image : images) {
            BufferedImage bimage = GraphicsUtils.toBufferedImage(image);
            assertNotNull(bimage);
            assertEquals(BufferedImage.class, bimage.getClass());
            assertEquals(image.getWidth(null), bimage.getWidth());
            assertEquals(image.getHeight(null), bimage.getHeight());
        }
    }

    @Test
    public void testHasAlpha() {
        Image image;
        image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
        assertTrue(GraphicsUtils.hasAlpha(image));
        image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
        assertFalse(GraphicsUtils.hasAlpha(image));
    }

    @Test
    public void testPhysicalFont() {
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        assertNotSame(font, GraphicsUtils.getPhysicalFont(font));
    }

    @Test
    public void testCloneShape()
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?>[] shapeClasses = {
                Line2D.Float.class,
                Line2D.Double.class,
                Rectangle.class,
                Rectangle2D.Float.class,
                Rectangle2D.Double.class,
                RoundRectangle2D.Float.class,
                RoundRectangle2D.Double.class,
                Ellipse2D.Float.class,
                Ellipse2D.Double.class,
                Arc2D.Float.class,
                Arc2D.Double.class,
                Polygon.class,
                CubicCurve2D.Float.class,
                CubicCurve2D.Double.class,
                QuadCurve2D.Float.class,
                QuadCurve2D.Double.class,
                Path2D.Float.class,
                Path2D.Double.class
        };
        for (Class<?> shapeClass : shapeClasses) {
            Shape shape = (Shape) shapeClass.getDeclaredConstructor().newInstance();
            Shape clone = GraphicsUtils.clone(shape);
            assertNotNull(clone);
            assertShapeEquals(shape, clone);
        }
    }
}
