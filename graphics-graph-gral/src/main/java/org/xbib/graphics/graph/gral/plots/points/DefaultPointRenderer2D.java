package org.xbib.graphics.graph.gral.plots.points;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;

import org.xbib.graphics.graph.gral.data.Row;
import org.xbib.graphics.graph.gral.graphics.AbstractDrawable;
import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.graphics.DrawableContainer;
import org.xbib.graphics.graph.gral.graphics.DrawingContext;
import org.xbib.graphics.graph.gral.graphics.layout.OuterEdgeLayout;
import org.xbib.graphics.graph.gral.graphics.Label;
import org.xbib.graphics.graph.gral.plots.axes.Axis;
import org.xbib.graphics.graph.gral.plots.axes.AxisRenderer;
import org.xbib.graphics.graph.gral.plots.colors.ColorMapper;
import org.xbib.graphics.graph.gral.util.GraphicsUtils;
import org.xbib.graphics.graph.gral.graphics.Location;
import org.xbib.graphics.graph.gral.util.MathUtils;
import org.xbib.graphics.graph.gral.util.PointND;

/**
 * Class that creates {@code Drawable}s for a row of data.
 */
public class DefaultPointRenderer2D extends AbstractPointRenderer {

	@Override
	public Drawable getPoint(final PointData data, final Shape shape) {
		return new AbstractDrawable() {

			public void draw(DrawingContext context) {
				PointRenderer renderer = DefaultPointRenderer2D.this;

				Axis axisY = data.axes.get(1);
				AxisRenderer axisRendererY = data.axisRenderers.get(1);
				Row row = data.row;
				int col = data.col;

				ColorMapper colors = getColor();
				Paint paint = colors.get(data.index);

				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), shape, paint, null);

				if (renderer.isErrorVisible()) {
					int colErrorTop = renderer.getErrorColumnTop();
					int colErrorBottom = renderer.getErrorColumnBottom();
					drawErrorBars(context, shape,
						row, data.index, col, colErrorTop, colErrorBottom,
						axisY, axisRendererY);
				}
			}
		};
	}

	/**
	 * Draws the specified value label for the specified shape.
	 * @param context Environment used for drawing.
	 * @param point Point shape used to layout the label.
	 * @param row Data row containing the point.
	 * @param pointIndex Index number used for coloring.
	 * @param col Index of the column that will be projected on the axis.
	 */
	protected void drawValueLabel(DrawingContext context,
			Shape point, Row row, int pointIndex, int col) {
		Comparable<?> value = row.get(col);

		// Formatting
		Format format = getValueFormat();
		if ((format == null) && row.isColumnNumeric(col)) {
			format = NumberFormat.getInstance();
		}

		// Text to display
		String text = (format != null) ? format.format(value) : value.toString();

		// Visual settings
		ColorMapper colors = getValueColor();
		Paint paint = colors.get(pointIndex);
		Font font = getValueFont();
		double fontSize = font.getSize2D();

		// Layout settings
		Location location = getValueLocation();
		double alignX = getValueAlignmentX();
		double alignY = getValueAlignmentY();
		double rotation = getValueRotation();
		double distance = getValueDistance();
		if (MathUtils.isCalculatable(distance)) {
			distance *= fontSize;
		} else {
			distance = 0.0;
		}

		// Create a label with the settings
		Label label = new Label(text);
		label.setAlignmentX(alignX);
		label.setAlignmentY(alignY);
		label.setRotation(rotation);
		label.setColor(paint);
		label.setFont(font);

		Rectangle2D boundsPoint = point.getBounds2D();
		DrawableContainer labelContainer =
			new DrawableContainer(new OuterEdgeLayout(distance));
		labelContainer.add(label, location);

		labelContainer.setBounds(boundsPoint);
		labelContainer.draw(context);
	}

	/**
	 * Draws error bars.
	 * @param context Environment used for drawing.
	 * @param point Shape of the point.
	 * @param row Data row containing the point.
	 * @param rowIndex Index of the row.
	 * @param col Index of the column that will be projected on the axis.
	 * @param colErrorTop Index of the column that contains the upper error value.
	 * @param colErrorBottom Index of the column that contains the lower error value.
	 * @param axis Axis.
	 * @param axisRenderer Axis renderer.
	 */
	protected void drawErrorBars(DrawingContext context, Shape point,
			Row row, int rowIndex, int col, int colErrorTop, int colErrorBottom,
			Axis axis, AxisRenderer axisRenderer) {
		if (axisRenderer == null) {
			return;
		}

		if (colErrorTop < 0 || colErrorTop >= row.size() ||
				!row.isColumnNumeric(colErrorTop) ||
				colErrorBottom < 0 || colErrorBottom >= row.size() ||
				!row.isColumnNumeric(colErrorBottom)) {
			return;
		}

		Number value = (Number) row.get(col);
		Number errorTop = (Number) row.get(colErrorTop);
		Number errorBottom = (Number) row.get(colErrorBottom);
		if (!MathUtils.isCalculatable(value) ||
				!MathUtils.isCalculatable(errorTop) ||
				!MathUtils.isCalculatable(errorBottom)) {
			return;
		}

		Graphics2D graphics = context.getGraphics();
		AffineTransform txOld = graphics.getTransform();

		// Calculate positions
		PointND<Double> pointValue = axisRenderer.getPosition(axis,
			value, true, false);
		PointND<Double> pointTop = axisRenderer.getPosition(axis,
				value.doubleValue() + errorTop.doubleValue(), true, false);
			PointND<Double> pointBottom = axisRenderer.getPosition(axis,
					value.doubleValue() - errorBottom.doubleValue(), true, false);
		if (pointValue == null || pointTop == null || pointBottom == null) {
			return;
		}
		double posY = pointValue.get(PointND.Y);
		double posYTop = pointTop.get(PointND.Y) - posY;
		double posYBottom = pointBottom.get(PointND.Y) - posY;

		// Draw the error bar
		Line2D errorBar = new Line2D.Double(0.0, posYTop, 0.0, posYBottom);
		ColorMapper colors = getErrorColor();
		Paint errorPaint = colors.get(rowIndex);
		Stroke errorStroke = getErrorStroke();
		GraphicsUtils.drawPaintedShape(
			graphics, errorBar, errorPaint, null, errorStroke);

		// Draw the shapes at the end of the error bars
		Shape endShape = getErrorShape();
		graphics.translate(0.0, posYTop);
		Stroke endShapeStroke = new BasicStroke(1f);
		GraphicsUtils.drawPaintedShape(
			graphics, endShape, errorPaint, null, endShapeStroke);
		graphics.setTransform(txOld);
		graphics.translate(0.0, posYBottom);
		GraphicsUtils.drawPaintedShape(
			graphics, endShape, errorPaint, null, endShapeStroke);
		graphics.setTransform(txOld);
	}

	/**
	 * Returns a {@code Shape} instance that can be used for further
	 * calculations.
	 * @param data Information on axes, renderers, and values.
	 * @return Outline that describes the point's shape.
	 */
	public Shape getPointShape(PointData data) {
		return getShape();
	}

	/**
	 * Returns a graphical representation of the value label to be drawn for
	 * the specified data value.
	 * @param data Information on axes, renderers, and values.
	 * @param shape Outline that describes the bounds for the value label.
	 * @return Component that can be used to draw the value label.
	 */
	public Drawable getValue(final PointData data, final Shape shape) {
		return new AbstractDrawable() {
			/** Version id for serialization. */
			private static final long serialVersionUID1 = -2568531344817590175L;

			public void draw(DrawingContext context) {
				PointRenderer renderer = DefaultPointRenderer2D.this;
				Row row = data.row;

				if (renderer.isValueVisible()) {
					int colValue = renderer.getValueColumn();
					drawValueLabel(context, shape, row, data.index, colValue);
				}
			}
		};
	}
}
