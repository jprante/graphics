package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Paint;

import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Class that maps floating point numbers to Paint objects. This can be used to
 * generate colors or gradients for various elements in a plot, e.g. lines,
 * areas, etc.
 */
public abstract class ContinuousColorMapper extends AbstractColorMapper<Double> {

	/**
	 * Returns the Paint object according to the specified value.
	 * @param value Numeric value.
	 * @return Paint object.
	 */
	public abstract Paint get(double value);

	/**
	 * Returns the Paint object according to the specified value. The specified
	 * value will be handled like a double value.
	 * @param value Numeric value object.
	 * @return Paint object.
	 */
	public Paint get(Number value) {
		return get(value.doubleValue());
	}

	@Override
	protected Double applyMode(Double value, Double rangeMin, Double rangeMax) {
		if (value >= rangeMin && value <= rangeMax) {
			return value;
		}
		Mode mode = getMode();
		if (mode == Mode.REPEAT) {
			return MathUtils.limit(value, rangeMin, rangeMax);
		} else if (mode == Mode.CIRCULAR) {
			double range = rangeMax - rangeMin;
			double i = value%range;
			if (i < 0.0) {
				i += range;
			}
			return i + rangeMin;
		}
		return null;
	}
}
