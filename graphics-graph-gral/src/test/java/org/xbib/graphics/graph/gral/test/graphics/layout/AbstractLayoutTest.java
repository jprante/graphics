package org.xbib.graphics.graph.gral.test.graphics.layout;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Dimension2D;

import org.xbib.graphics.graph.gral.graphics.Container;
import org.junit.Test;

import org.xbib.graphics.graph.gral.graphics.layout.AbstractLayout;


public class AbstractLayoutTest {
	private static final double DELTA = 1e-15;
	private static final double GAP_H = 5.0;
	private static final double GAP_V = 10.0;

	private static class MockAbstractLayout extends AbstractLayout {

		public MockAbstractLayout(double gapX, double gapY) {
			super(gapX, gapY);
		}

		@Override
		public void layout(Container container) {
		}

		@Override
		public Dimension2D getPreferredSize(Container container) {
			return new org.xbib.graphics.graph.gral.graphics.Dimension2D.Double();
		}
	}

	@Test
	public void testCreate() {
		AbstractLayout gapped = new MockAbstractLayout(GAP_H, GAP_V);
		assertEquals(GAP_H, gapped.getGapX(), DELTA);
		assertEquals(GAP_V, gapped.getGapY(), DELTA);
	}
}
