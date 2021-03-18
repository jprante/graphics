package org.xbib.graphics.graph.gral.plots;

import org.xbib.graphics.graph.gral.plots.points.PointData;
import org.xbib.graphics.graph.gral.util.PointND;


/**
 * Class for storing points of a plot.
 */
public class DataPoint {
	/** Axes and data values that were used to create the data point. */
	public final PointData data;
	/** Position of the data point (n-dimensional). */
	public final PointND<Double> position;

	/**
	 * Creates a new {@code DataPoint} object with the specified position,
	 * {@code Drawable}, and shape.
	 * @param data Data that this point was created from.
	 * @param position Coordinates in view/screen units.
	 */
	public DataPoint(PointData data, PointND<Double> position) {
		this.data = data;
		this.position = position;
	}
}
