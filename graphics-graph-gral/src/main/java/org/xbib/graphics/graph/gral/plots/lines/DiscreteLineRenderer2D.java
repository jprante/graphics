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
import org.xbib.graphics.graph.gral.graphics.Orientation;

/**
 * Class that connects {@code DataPoint}s with a stair-like line.
 */
public class DiscreteLineRenderer2D extends AbstractLineRenderer2D {

	/** Primary direction of the "steps". */
	private Orientation ascentDirection;
	/** Relative distance between points, where the orientation changes. */
	private Number ascendingPoint;

	/**
	 * Initializes a new {@code DiscreteLineRenderer2D} instance with default
	 * settings.
	 */
	public DiscreteLineRenderer2D() {
		ascentDirection = Orientation.HORIZONTAL;
		ascendingPoint = 0.5;
	}

	/**
	 * Returns a graphical representation for the line defined by
	 * {@code points}.
	 * @param points Points to be used for creating the line.
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
				// Draw path
				Paint paint = DiscreteLineRenderer2D.this.getColor();
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
		Orientation dir = getAscentDirection();
		double ascendingPoint = getAscendingPoint().doubleValue();

		// Construct shape
		Path2D shape = new Path2D.Double();
		for (DataPoint point : points) {
			Point2D pos = point.position.getPoint2D();
			if (shape.getCurrentPoint() == null) {
				shape.moveTo(pos.getX(), pos.getY());
			} else {
				Point2D posPrev = shape.getCurrentPoint();
				if (dir == Orientation.HORIZONTAL) {
					double ascendingX = posPrev.getX() +
						(pos.getX() - posPrev.getX()) * ascendingPoint;
					shape.lineTo(ascendingX,  posPrev.getY());
					shape.lineTo(ascendingX,  pos.getY());
				} else {
					double ascendingY = posPrev.getY() +
						(pos.getY() - posPrev.getY()) * ascendingPoint;
					shape.lineTo(posPrev.getX(), ascendingY);
					shape.lineTo(pos.getX(), ascendingY);
				}
				shape.lineTo(pos.getX(), pos.getY());
			}
		}

		return stroke(shape);
	}

	/**
	 * Returns the primary direction of the "steps".
	 * @return Orientation of the "steps".
	 */
	public Orientation getAscentDirection() {
		return ascentDirection;
	}

	/**
	 * Sets the primary direction of the "steps".
	 * @param ascentDirection Orientation of the "steps".
	 */
	public void setAscentDirection(Orientation ascentDirection) {
		this.ascentDirection = ascentDirection;
	}

	/**
	 * Returns the relative distance between two points, {@literal i.e.} the "step" of a stair.
	 * @return Relative point distance.
	 */
	public Number getAscendingPoint() {
		return ascendingPoint;
	}

	/**
	 * Sets the relative distance between two points, {@literal i.e.} the "step" of a stair.
	 * @param ascendingPoint Relative point distance.
	 */
	public void setAscendingPoint(Number ascendingPoint) {
		this.ascendingPoint = ascendingPoint;
	}
}
