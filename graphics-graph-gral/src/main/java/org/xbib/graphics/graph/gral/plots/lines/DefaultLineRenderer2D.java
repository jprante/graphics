package org.xbib.graphics.graph.gral.plots.lines;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import org.xbib.graphics.graph.gral.graphics.AbstractDrawable;
import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.graphics.DrawingContext;
import org.xbib.graphics.graph.gral.plots.DataPoint;
import org.xbib.graphics.graph.gral.util.GraphicsUtils;

/**
 * Class that connects two dimensional data points with a straight line.
 */
public class DefaultLineRenderer2D extends AbstractLineRenderer2D {
	/** Number of line segments which will be reserved to avoid unnecessary
	copying of array data. */
	private static final int INITIAL_LINE_CAPACITY = 10000;

	/**
	 * Initializes a new {@code DefaultLineRenderer2D} instance.
	 */
	public DefaultLineRenderer2D() {
	}

	/**
	 * Returns a graphical representation for the line defined by
	 * {@code e points}.
	 * @param points Points used for creating the line.
	 * @param shape Geometric shape for this line.
	 * @return Representation of the line.
	 */
	public Drawable getLine(final List<DataPoint> points, final Shape shape) {
		return new AbstractDrawable() {

			/**
			 * Draws the {@code Drawable} with the specified drawing context.
			 * @param context Environment used for drawing
			 */
			public void draw(DrawingContext context) {
				// Draw line
				Paint paint = DefaultLineRenderer2D.this.getColor();
				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), shape, paint, null);
			}
		};
	}

	/**
	 * Returns the geometric shape for this line.
	 * @param points Points used for creating the line.
	 * @return Geometric shape for this line.
	 */
	public Shape getLineShape(List<DataPoint> points) {
		// Construct shape
		Path2D shape = new Path2D.Double(
			Path2D.WIND_NON_ZERO, INITIAL_LINE_CAPACITY);
		for (DataPoint point : points) {
			Point2D pos = point.position.getPoint2D();
			if (shape.getCurrentPoint() == null) {
				shape.moveTo(pos.getX(), pos.getY());
			} else {
				shape.lineTo(pos.getX(), pos.getY());
			}
		}
		return stroke(shape);
	}
}
