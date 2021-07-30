package org.xbib.graphics.graph.gral.test.graphics.layout;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.xbib.graphics.graph.gral.graphics.AbstractDrawable;
import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.graphics.DrawableContainer;
import org.xbib.graphics.graph.gral.graphics.DrawingContext;
import org.junit.Before;
import org.junit.Test;

import org.xbib.graphics.graph.gral.graphics.Location;
import org.xbib.graphics.graph.gral.graphics.layout.EdgeLayout;

public class EdgeLayoutTest {
	private static final double DELTA = 1e-15;
	private static final double GAP_H = 5.0;
	private static final double GAP_V = 10.0;
	private static final double COMP_WIDTH = 10.0;
	private static final double COMP_HEIGHT = 5.0;

	private DrawableContainer container;
	private EdgeLayout layout;
	private Drawable nn, nw, ww, sw, ss, se, ee, ne, ce;

	private static final class TestDrawable extends AbstractDrawable {

		public void draw(DrawingContext context) {
		}

		@Override
		public Dimension2D getPreferredSize() {
			Dimension2D size = super.getPreferredSize();
			size.setSize(COMP_WIDTH, COMP_HEIGHT);
			return size;
		}
	}

	@Before
	public void setUp() {
		layout = new EdgeLayout(GAP_H, GAP_V);

		container = new DrawableContainer(null);

		nn = new TestDrawable();
		nw = new TestDrawable();
		ww = new TestDrawable();
		sw = new TestDrawable();
		ss = new TestDrawable();
		se = new TestDrawable();
		ee = new TestDrawable();
		ne = new TestDrawable();
		ce = new TestDrawable();

		container.add(nn, Location.NORTH);
		container.add(nw, Location.NORTH_WEST);
		container.add(ww, Location.WEST);
		container.add(sw, Location.SOUTH_WEST);
		container.add(ss, Location.SOUTH);
		container.add(se, Location.SOUTH_EAST);
		container.add(ee, Location.EAST);
		container.add(ne, Location.NORTH_EAST);
		container.add(ce, Location.CENTER);
	}

	@Test
	public void testCreate() {
		EdgeLayout noGap = new EdgeLayout();
		assertEquals(0.0, noGap.getGapX(), DELTA);
		assertEquals(0.0, noGap.getGapY(), DELTA);

		EdgeLayout gapped = new EdgeLayout(GAP_H, GAP_V);
		assertEquals(GAP_H, gapped.getGapX(), DELTA);
		assertEquals(GAP_V, gapped.getGapY(), DELTA);
	}

	@Test
	public void testPreferredSize() {
		Dimension2D size = layout.getPreferredSize(container);
		assertEquals(3.0*COMP_WIDTH + 2.0*GAP_H, size.getWidth(), DELTA);
		assertEquals(3.0*COMP_HEIGHT + 2.0*GAP_V, size.getHeight(), DELTA);
	}

	@Test
	public void testLayout() {
		Rectangle2D bounds = new Rectangle2D.Double(5.0, 5.0, 50.0, 50.0);
		container.setBounds(bounds);
		layout.layout(container);

		// Test x coordinates
		assertEquals(bounds.getMinX(), nw.getX(), DELTA);
		assertEquals(bounds.getMinX(), ww.getX(), DELTA);
		assertEquals(bounds.getMinX(), sw.getX(), DELTA);
		assertEquals(bounds.getMinX() + COMP_WIDTH + GAP_H, nn.getX(), DELTA);
		assertEquals(bounds.getMinX() + COMP_WIDTH + GAP_H, ce.getX(), DELTA);
		assertEquals(bounds.getMinX() + COMP_WIDTH + GAP_H, ss.getX(), DELTA);
		assertEquals(bounds.getMaxX() - COMP_WIDTH, ne.getX(), DELTA);
		assertEquals(bounds.getMaxX() - COMP_WIDTH, ee.getX(), DELTA);
		assertEquals(bounds.getMaxX() - COMP_WIDTH, se.getX(), DELTA);
		// Test y coordinates
		assertEquals(bounds.getMinY(), nw.getY(), DELTA);
		assertEquals(bounds.getMinY(), nn.getY(), DELTA);
		assertEquals(bounds.getMinY(), ne.getY(), DELTA);
		assertEquals(bounds.getMinY() + COMP_HEIGHT + GAP_V, ww.getY(), DELTA);
		assertEquals(bounds.getMinY() + COMP_HEIGHT + GAP_V, ce.getY(), DELTA);
		assertEquals(bounds.getMinY() + COMP_HEIGHT + GAP_V, ee.getY(), DELTA);
		assertEquals(bounds.getMaxY() - COMP_HEIGHT, sw.getY(), DELTA);
		assertEquals(bounds.getMaxY() - COMP_HEIGHT, ss.getY(), DELTA);
		assertEquals(bounds.getMaxY() - COMP_HEIGHT, se.getY(), DELTA);

		// TODO Test width and height
	}
}
