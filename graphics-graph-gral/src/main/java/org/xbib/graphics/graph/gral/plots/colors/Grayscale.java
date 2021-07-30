package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Color;
import java.awt.Paint;

import org.xbib.graphics.graph.gral.util.GraphicsUtils;
import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Class that generates shades of gray for values between 0.0 and 1.0.
 */
public class Grayscale extends ScaledContinuousColorMapper {

	/**
	 * Returns the Paint object according to the specified value.
	 * @param value Value of color.
	 * @return Paint object.
	 */
	@Override
	public Paint get(double value) {
		Double v = scale(value);
		v = applyMode(v, 0.0, 1.0);
		if (!MathUtils.isCalculatable(v)) {
			return null;
		}
		double lightness = 100.0*v;
		double[] rgb = GraphicsUtils.luv2rgb(new double[] {lightness, 0.0, 0.0}, null);
		return new Color(
			(float) MathUtils.limit(rgb[0], 0.0, 1.0),
			(float) MathUtils.limit(rgb[1], 0.0, 1.0),
			(float) MathUtils.limit(rgb[2], 0.0, 1.0)
		);
	}

	@Override
	public void setMode(Mode mode) {
		super.setMode(mode);
	}
}
