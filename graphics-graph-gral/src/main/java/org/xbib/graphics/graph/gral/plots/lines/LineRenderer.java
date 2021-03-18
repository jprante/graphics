package org.xbib.graphics.graph.gral.plots.lines;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;

import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.plots.DataPoint;


/**
 * <p>Interface that provides functions for rendering a line in two dimensional
 * space.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Punching data points out of the line's shape</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public interface LineRenderer {
	/**
	 * Returns the geometric shape for this line.
	 * @param points Points used for creating the line.
	 * @return Geometric shape for this line.
	 */
	Shape getLineShape(List<DataPoint> points);

	/**
	 * Returns a graphical representation for the line defined by
	 * {@code points}.
	 * @param points Points to be used for creating the line.
	 * @param shape Geometric shape for this line.
	 * @return Representation of the line.
	 */
	Drawable getLine(List<DataPoint> points, Shape shape);

	/**
	 * Returns the stroke to be used to define the line shape.
	 * @return Stroke used for drawing.
	 */
	Stroke getStroke();

	/**
	 * Sets the stroke to be used to define the line shape.
	 * @param stroke Stroke used for drawing.
	 */
	void setStroke(Stroke stroke);

	/**
	 * Returns the value for the gap between the line and a point.
	 * If the gap value is equal to or smaller than 0 no gap will be used.
	 * @return Gap size between drawn line and connected points in pixels.
	 */
	double getGap();

	/**
	 * Sets the value for the gap between the line and a point.
	 * If the gap value is equal to or smaller than 0 no gap will be used.
	 * @param gap Gap size between drawn line and connected points in pixels.
	 */
	void setGap(double gap);

	/**
	 * Returns whether the gaps should have rounded corners.
	 * @return {@code true} if the gap corners should be rounded.
	 */
	boolean isGapRounded();

	/**
	 * Sets whether the gaps should have rounded corners.
	 * @param gapRounded {@code true} if the gap corners should be rounded.
	 */
	void setGapRounded(boolean gapRounded);

	/**
	 * Returns the paint to be used to paint the line shape.
	 * @return Paint for line drawing.
	 */
	Paint getColor();

	/**
	 * Sets the paint to be used to paint the line shape.
	 * @param color Paint for line drawing.
	 */
	void setColor(Paint color);
}
