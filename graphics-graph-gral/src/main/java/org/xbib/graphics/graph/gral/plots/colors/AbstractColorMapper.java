package org.xbib.graphics.graph.gral.plots.colors;

/**
 * Interface that maps numbers to Paint objects. This can be used to generate
 * colors or gradients for various elements in a plot, e.g. lines, areas, etc.
 *
 * @param <T> Data type of input values.
 */
public abstract class AbstractColorMapper<T extends Number>
		implements ColorMapper {

	/** Handling of values that are outside the mapping range. */
	private Mode mode;

	/**
	 * Initializes a new instance with default values.
	 */
	public AbstractColorMapper() {
		mode = Mode.REPEAT;
	}

	/**
	 * Returns how values outside of the mapping range will be handled.
	 * @return Handling of values outside of the mapping range.
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Sets how values outside of the mapping range will be handled.
	 * @param mode Handling of values outside of the mapping range.
	 */
	protected void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * Transforms a value outside of the mapping range. If the value is inside
	 * the range, no transformation will be applied.
	 * @param value Value to be handled.
	 * @param rangeMin Lower bounds of range
	 * @param rangeMax Upper bounds of range
	 * @return Transformed value.
	 */
	protected abstract T applyMode(T value, T rangeMin, T rangeMax);
}
