package org.xbib.graphics.graph.gral.plots.areas;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import org.xbib.graphics.graph.gral.graphics.AbstractDrawable;
import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.graphics.DrawingContext;
import org.xbib.graphics.graph.gral.plots.DataPoint;
import org.xbib.graphics.graph.gral.plots.axes.Axis;
import org.xbib.graphics.graph.gral.plots.axes.AxisRenderer;
import org.xbib.graphics.graph.gral.util.GraphicsUtils;
import org.xbib.graphics.graph.gral.util.MathUtils;
import org.xbib.graphics.graph.gral.util.PointND;

/**
 * Default two-dimensional implementation of the {@code AreaRenderer}
 * interface.
 */
public class DefaultAreaRenderer2D extends AbstractAreaRenderer {

	/**
	 * Returns the graphical representation to be drawn for the specified
	 * data points.
	 * @param points Points to be used for creating the area.
	 * @param shape Geometric shape of the area.
	 * @return Representation of the area.
	 */
	public Drawable getArea(final List<DataPoint> points, final Shape shape) {
		return new AbstractDrawable() {

			/**
			 * Draws the {@code Drawable} with the specified drawing context.
			 * @param context Environment used for drawing
			 */
			public void draw(DrawingContext context) {
				Paint paint = DefaultAreaRenderer2D.this.getColor();
				GraphicsUtils.fillPaintedShape(context.getGraphics(),
					shape, paint, null);
			}
		};
	}

	/**
	 * Returns the shape used for rendering the area of a data points.
	 * @param points Data points.
	 * @return Geometric shape for the area of the specified data points.
	 */
	public Shape getAreaShape(List<DataPoint> points) {
		if (points.isEmpty() || points.get(0) == null) {
			return null;
		}

		Axis axisY = points.get(0).data.axes.get(1);
		AxisRenderer axisRendererY = points.get(0).data.axisRenderers.get(1);

		double axisYMin = axisY.getMin().doubleValue();
		double axisYMax = axisY.getMax().doubleValue();
		double axisYOrigin = MathUtils.limit(0.0, axisYMin, axisYMax);

		PointND<Double> posOrigin = null;
		if (axisRendererY != null) {
			posOrigin = axisRendererY.getPosition(
					axisY, axisYOrigin, true, false);
		}

		Path2D shape = new Path2D.Double();
		if (posOrigin == null) {
			return shape;
		}

		double posYOrigin = posOrigin.get(PointND.Y);
		double x = 0.0;
		double y = 0.0;

		for (DataPoint p: points) {
			Point2D pos = p.position.getPoint2D();
			x = pos.getX();
			y = pos.getY();
			if (shape.getCurrentPoint() == null) {
				shape.moveTo(x, posYOrigin);
			}
			shape.lineTo(x, y);
		}

		if (shape.getCurrentPoint() != null) {
			shape.lineTo(x, posYOrigin);
			shape.closePath();
		}

		return shape;
	}
}
