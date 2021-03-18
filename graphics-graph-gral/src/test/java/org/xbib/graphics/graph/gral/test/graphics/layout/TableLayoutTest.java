package org.xbib.graphics.graph.gral.test.graphics.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.xbib.graphics.graph.gral.graphics.AbstractDrawable;
import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.graphics.DrawableContainer;
import org.xbib.graphics.graph.gral.graphics.DrawingContext;
import org.junit.Before;
import org.junit.Test;
import org.xbib.graphics.graph.gral.graphics.layout.Layout;
import org.xbib.graphics.graph.gral.graphics.layout.TableLayout;

public class TableLayoutTest {
	private static final double DELTA = 1e-15;
	private static final double GAP_X = 5.0;
	private static final double GAP_Y = 10.0;
	private static final double COMP_WIDTH = 10.0;
	private static final double COMP_HEIGHT = 5.0;

	private DrawableContainer container;
	private Drawable a, b, c;

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
		container = new DrawableContainer(null);

		a = new TestDrawable();
		b = new TestDrawable();
		c = new TestDrawable();

		container.add(a);
		container.add(b);
		container.add(c);
	}

	@Test
	public void testCreate() {
		TableLayout noGap = new TableLayout(1);
		assertEquals(0.0, noGap.getGapX(), DELTA);
		assertEquals(0.0, noGap.getGapY(), DELTA);

		TableLayout gapped = new TableLayout(1, GAP_X, GAP_Y);
		assertEquals(GAP_X, gapped.getGapX(), DELTA);
		assertEquals(GAP_Y, gapped.getGapY(), DELTA);
	}

	@Test
	public void testCreateInvalid() {
		try {
			new TableLayout(-1, GAP_X, GAP_Y);
			fail("Expected IllegalArgumentException because of negative column number.");
		} catch (IllegalArgumentException e) {
		}

		try {
			new TableLayout(0, GAP_X, GAP_Y);
			fail("Expected IllegalArgumentException because column number was zero.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testPreferredSizeVertical() {
		Layout layout = new TableLayout(1, GAP_X, GAP_Y);
		Dimension2D size = layout.getPreferredSize(container);
		assertEquals(COMP_WIDTH, size.getWidth(), DELTA);
		assertEquals(3.0*COMP_HEIGHT + 2.0*GAP_Y, size.getHeight(), DELTA);
	}

	@Test
	public void testPreferredSizeHorizontal() {
		Layout layout = new TableLayout(3, GAP_X, GAP_Y);
		Dimension2D size = layout.getPreferredSize(container);
		assertEquals(3.0*COMP_WIDTH + 2.0*GAP_X, size.getWidth(), DELTA);
		assertEquals(COMP_HEIGHT, size.getHeight(), DELTA);
	}

	@Test
	public void testLayoutVertical() {
		Layout layout = new TableLayout(1, GAP_X, GAP_Y);
		Rectangle2D bounds = new Rectangle2D.Double(5.0, 5.0, 50.0, 50.0);
		container.setBounds(bounds);
		layout.layout(container);

		// Test x coordinates
		assertEquals(bounds.getMinX(), a.getX(), DELTA);
		assertEquals(bounds.getMinX(), b.getX(), DELTA);
		assertEquals(bounds.getMinX(), c.getX(), DELTA);
		// Test y coordinates
		double meanCompHeight = (bounds.getHeight() - 2.0*GAP_Y)/3.0;
		assertEquals(bounds.getMinY() + 0.0*meanCompHeight + 0.0*GAP_Y, a.getY(), DELTA);
		assertEquals(bounds.getMinY() + 1.0*meanCompHeight + 1.0*GAP_Y, b.getY(), DELTA);
		assertEquals(bounds.getMinY() + 2.0*meanCompHeight + 2.0*GAP_Y, c.getY(), DELTA);

		// TODO Test width and height
	}

	@Test
	public void testLayoutHorizontal() {
		Layout layout = new TableLayout(3, GAP_X, GAP_Y);
		Rectangle2D bounds = new Rectangle2D.Double(5.0, 5.0, 50.0, 50.0);
		container.setBounds(bounds);
		layout.layout(container);

		// Test x coordinates
		double meanCompWidth = (bounds.getWidth() - 2.0*GAP_X)/3.0;
		assertEquals(bounds.getMinX() + 0.0*meanCompWidth + 0.0*GAP_X, a.getX(), DELTA);
		assertEquals(bounds.getMinX() + 1.0*meanCompWidth + 1.0*GAP_X, b.getX(), DELTA);
		assertEquals(bounds.getMinX() + 2.0*meanCompWidth + 2.0*GAP_X, c.getX(), DELTA);
		// Test y coordinates
		assertEquals(bounds.getMinY(), a.getY(), DELTA);
		assertEquals(bounds.getMinY(), b.getY(), DELTA);
		assertEquals(bounds.getMinY(), c.getY(), DELTA);

		// TODO Test width and height
	}
}
