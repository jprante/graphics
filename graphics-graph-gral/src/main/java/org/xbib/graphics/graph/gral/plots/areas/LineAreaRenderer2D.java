package org.xbib.graphics.graph.gral.plots.areas;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
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
 * Default two-dimensional implementation of the {@code AreaRenderer} interface
 * that draws lines from data points to the main axis.
 */
public class LineAreaRenderer2D extends AbstractAreaRenderer {

	/** Stroke that is used to draw the lines from the data points to the
	 * axis. */
	private Stroke stroke;

	/**
	 * Standard constructor that initializes a new instance.
	 */
	public LineAreaRenderer2D() {
		stroke = new BasicStroke(1f);
	}

	/**
	 * Returns the graphical representation to be drawn for the specified data
	 * points.
	 * @param points Points that define the shape of the area.
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
				Paint paint = LineAreaRenderer2D.this.getColor();
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
		double posYOrigin = 0.0;
		if (axisRendererY != null) {
			posYOrigin = axisRendererY.getPosition(
					axisY, axisYOrigin, true, false).get(PointND.Y);
		}
		Path2D shape = new Path2D.Double();
		double x = 0.0;
		double y = 0.0;
		for (DataPoint p : points) {
			Point2D pos = p.position.getPoint2D();
			x = pos.getX();
			y = pos.getY();
			shape.moveTo(x, y);
			shape.lineTo(x, posYOrigin);
		}

		Stroke stroke = getStroke();
		return stroke.createStrokedShape(shape);
	}

	/**
	 * Returns the stroke that is used to draw the lines from the
	 * data points to the axis.
	 * @return Stroke for line drawing.
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Set the stroke that is used to draw the lines from the
	 * data points to the axis.
	 * @param stroke Stroke for line drawing.
	 */
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}
}
