package org.xbib.graphics.io.vector;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
import java.lang.reflect.InvocationTargetException;

public class GraphicsStateTest {

    @Test
    public void testInitialStateIsEqualToGraphics2D() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        GraphicsState state = new GraphicsState();
        assertEquals(state.getBackground(), g2d.getBackground());
        assertEquals(state.getColor(), g2d.getColor());
        assertEquals(state.getClip(), g2d.getClip());
        assertEquals(state.getComposite(), g2d.getComposite());
        assertEquals(state.getFont(), g2d.getFont());
        assertEquals(state.getPaint(), g2d.getPaint());
        assertEquals(state.getStroke(), g2d.getStroke());
        assertEquals(state.getTransform(), g2d.getTransform());
    }

    @Test
    public void testEquals() {
        GraphicsState state1 = new GraphicsState();
        state1.setBackground(Color.WHITE);
        state1.setColor(Color.BLACK);
        state1.setClip(new Rectangle2D.Double(0, 0, 10, 10));
        GraphicsState state2 = new GraphicsState();
        state2.setBackground(Color.WHITE);
        state2.setColor(Color.BLACK);
        state2.setClip(new Rectangle2D.Double(0, 0, 10, 10));
        assertEquals(state1, state2);
        state2.setTransform(AffineTransform.getTranslateInstance(5, 5));
        assertNotEquals(state2, state1);
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        GraphicsState state = new GraphicsState();
        state.setBackground(Color.BLUE);
        state.setColor(Color.GREEN);
        state.setClip(new Rectangle2D.Double(2, 3, 4, 2));
        GraphicsState clone = (GraphicsState) state.clone();
        assertNotSame(state, clone);
        assertEquals(state, clone);
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
            Shape clone = GraphicsState.clone(shape);
            assertNotNull(clone);
            assertShapeEquals(shape, clone);
        }
    }

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

    private static final double DELTA = 1e-15;

}
