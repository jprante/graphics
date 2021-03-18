package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Paint;

import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Class that maps integer numbers to Paint objects. This can be used to
 * generate colors or gradients for various elements in a plot, e.g. lines,
 * areas, etc.
 */
public abstract class IndexedColorMapper
		extends AbstractColorMapper<Integer> {

	/**
	 * Returns the Paint object according to the specified index.
	 * @param value Numeric index.
	 * @return Paint object.
	 */
	public abstract Paint get(int value);

	/**
	 * Returns the Paint object according to the specified index. The specified
	 * value will be handled like an integer index.
	 * @param index Numeric index object.
	 * @return Paint object.
	 */
	public Paint get(Number index) {
		return get(index.intValue());
	}

	@Override
	protected Integer applyMode(Integer index, Integer rangeMin, Integer rangeMax) {
		if (index >= rangeMin && index <= rangeMax) {
			return index;
		}
		Mode mode = getMode();
		if (mode == Mode.REPEAT) {
			return MathUtils.limit(index, rangeMin, rangeMax);
		} else if (mode == Mode.CIRCULAR) {
			int range = rangeMax - rangeMin + 1;
			int i = index%range;
			if (i < 0) {
				i += range;
			}
			return i + rangeMin;
		}
		return null;
	}
}
