package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Maps index values to a specified color palette.
 */
public class IndexedColors extends IndexedColorMapper {

	/** Color palette that will be used for mapping. **/
	private final List<Color> colors;

	/**
	 * Creates a new instance with at least one color.
	 * @param color1 First color.
	 * @param colors Additional colors.
	 */
	public IndexedColors(Color color1, Color... colors) {
		this.colors = new ArrayList<>();
		this.colors.add(color1);
		this.colors.addAll(Arrays.asList(colors));
	}

	/**
	 * Returns the Paint object associated to the specified index value.
	 * @param index Numeric index.
	 * @return Paint object.
	 */
	@Override
	public Paint get(int index) {
		Integer i = applyMode(index, 0, colors.size() - 1);
		if (!MathUtils.isCalculatable(i)) {
			return null;
		}
		return colors.get(i);
	}

	/**
	 * Returns the colors that are used for mapping.
	 * @return A list of colors in the order they are used as the color palette.
	 */
	public List<Color> getColors() {
		return Collections.unmodifiableList(colors);
	}

	@Override
	public void setMode(org.xbib.graphics.graph.gral.plots.colors.ColorMapper.Mode mode) {
		super.setMode(mode);
	}
}
