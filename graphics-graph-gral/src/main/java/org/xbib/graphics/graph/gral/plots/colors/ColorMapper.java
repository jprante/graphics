package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Paint;

/**
 * Basic interface for classes that map numbers to Paint objects. This can be
 * used to generate colors or gradients for various elements in a plot, e.g.
 * points, lines, areas, etc.
 *
 * {@link ContinuousColorMapper} or {@link IndexedColorMapper} should be used
 * as base classes in most cases.
 */
public interface ColorMapper {
	/** Data type to define how values outside of the mapping range will be
	handled. */
	enum Mode {
		/**	Ignore missing values. */
		OMIT,
		/**	Repeat the last value. */
		REPEAT,
		/**	Repeat the data. */
		CIRCULAR
	}

	/**
	 * Returns the Paint object according to the specified value.
	 * @param value Numeric value.
	 * @return Paint object.
	 */
	Paint get(Number value);

	/**
	 * Returns how values outside of the mapping range will be handled.
	 * @return Handling of values outside of the mapping range.
	 */
	Mode getMode();
}
