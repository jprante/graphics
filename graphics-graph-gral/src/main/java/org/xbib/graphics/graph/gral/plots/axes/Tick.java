package org.xbib.graphics.graph.gral.plots.axes;

import java.awt.Shape;

import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.plots.DataPoint;
import org.xbib.graphics.graph.gral.util.PointND;

/**
 * Class for storing the tick mark of an axis.
 */
public class Tick extends DataPoint {
	/** Type of tick mark. */
	public enum TickType {
		/** Major tick mark. */
		MAJOR,
		/** Minor tick mark. */
		MINOR,
		/** User-defined tick mark. */
		CUSTOM
	}

	/** The type of tick mark (major/minor/custom). */
	public final TickType type;
	/** The normal of the tick mark. */
	public final PointND<Double> normal;
	/** Drawable that will be used to render the tick. */
	public final Drawable drawable;
	/** Shape describing the tick. */
	public final Shape shape;
	/** Label text associated with this tick mark. */
	public final String label;

	/**
	 * Creates a new instance with the specified position, normal,
	 * {@code Drawable}, point and label.
	 * @param type Type of the tick mark.
	 * @param position Coordinates.
	 * @param normal Normal.
	 * @param drawable Representation.
	 * @param point Point.
	 * @param label Description.
	 */
	public Tick(TickType type, PointND<Double> position, PointND<Double> normal,
			Drawable drawable, Shape point, String label) {
		super(null, position);
		this.type = type;
		this.normal = normal;
		this.drawable = drawable;
		this.shape = point;
		this.label = label;
	}
}
