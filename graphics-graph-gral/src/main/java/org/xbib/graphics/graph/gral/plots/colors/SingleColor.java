package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Paint;

/**
 * Class that represents a ColorMapper with a single color.
 */
public class SingleColor extends IndexedColorMapper {

	/** The color that will be returned in any case. */
	private Paint color;

	/**
	 * Creates a new instance with the specified color.
	 * @param color Color to use.
	 */
	public SingleColor(Paint color) {
		this.color = color;
	}

	/**
	 * Returns the Paint according to the specified value.
	 * @param value Numeric index.
	 * @return Paint.
	 */
	@Override
	public Paint get(int value) {
		return getColor();
	}

	/**
	 * Returns the color of this ColorMapper.
	 * @return Color.
	 */
	public Paint getColor() {
		return color;
	}

	/**
	 * Sets the color of this ColorMapper.
	 * @param color Color to be set.
	 */
	public void setColor(Paint color) {
		this.color = color;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SingleColor)) {
			return false;
		}
		SingleColor cm = (SingleColor) obj;
		return color.equals(cm.color) && getMode() == cm.getMode();
	}

	@Override
	public int hashCode() {
		long bits = getColor().hashCode();
		bits ^= getMode().hashCode() * 31;
		return ((int) bits) ^ ((int) (bits >> 32));
	}
}
