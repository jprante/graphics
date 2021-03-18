package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Color;
import java.awt.Paint;

import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Class that generates the colors of a rainbow.
 */
public class RainbowColors extends ScaledContinuousColorMapper {

	/**
	 * Returns the Paint according to the specified value.
	 * @param value Value of color.
	 * @return Paint.
	 */
	@Override
	public Paint get(double value) {
		Double v = scale(value);
		v = applyMode(v, 0.0, 1.0);
		if (!MathUtils.isCalculatable(v)) {
			return null;
		}

		float hue = v.floatValue();
		return Color.getHSBColor(hue, 1f, 1f);
	}

	@Override
	public void setMode(Mode mode) {
		super.setMode(mode);
	}
}
