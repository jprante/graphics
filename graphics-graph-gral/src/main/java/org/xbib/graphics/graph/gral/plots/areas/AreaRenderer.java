package org.xbib.graphics.graph.gral.plots.areas;

import java.awt.Paint;
import java.awt.Shape;
import java.util.List;

import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.plots.DataPoint;

/**
 * Interface for renderers that display areas in plots.
 */
public interface AreaRenderer {
	/**
	 * Returns the shape used for rendering the area of a data points.
	 * @param points Data points.
	 * @return Geometric shape for the area of the specified data points.
	 */
	Shape getAreaShape(List<DataPoint> points);

	/**
	 * Returns the graphical representation to be drawn for the specified data
	 * points.
	 * @param points Points that define the shape of the area.
	 * @param shape Geometric shape of the area.
	 * @return Representation of the area.
	 */
	Drawable getArea(List<DataPoint> points, Shape shape);

	// TODO: Mention which unit the Gap property has (pixels?)
	/**
	 * Returns the value for the gap between the area and a data point.
	 * @return Gap between area and data point.
	 */
	double getGap();

	/**
	 * Sets the value for the gap between the area and a data point.
	 * @param gap Gap between area and data point.
	 */
	void setGap(double gap);

	/**
	 * Returns whether the gaps should have rounded corners.
	 * @return {@code true}, if the gaps should have rounded corners.
	 */
	boolean isGapRounded();

	/**
	 * Sets a value which decides whether the gaps should have rounded corners.
	 * @param gapRounded {@code true}, if the gaps should have rounded corners.
	 */
	void setGapRounded(boolean gapRounded);

	/**
	 * Returns the paint used to fill the area shape.
	 * @return Paint for the area shape.
	 */
	Paint getColor();

	/**
	 * Sets the paint used to fill the area shape.
	 * @param color Paint for the area shape.
	 */
	void setColor(Paint color);
}
