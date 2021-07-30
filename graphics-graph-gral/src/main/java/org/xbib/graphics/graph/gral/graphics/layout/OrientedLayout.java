package org.xbib.graphics.graph.gral.graphics.layout;

import org.xbib.graphics.graph.gral.graphics.Orientation;

/**
 * Represents a layout with a specific orientation.
 * @see Orientation
 */
public interface OrientedLayout extends Layout {
	/**
	 * Returns the layout direction.
	 * @return Layout orientation.
	 */
	Orientation getOrientation();

	/**
	 * Sets the layout direction.
	 * @param orientation Layout orientation.
	 */
	void setOrientation(Orientation orientation);
}
