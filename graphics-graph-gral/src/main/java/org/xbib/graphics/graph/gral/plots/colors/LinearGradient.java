package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Linearly blends different colors for values between 0.0 and 1.0.
 */
public class LinearGradient extends ScaledContinuousColorMapper {

	/** Colors that will be used for blending. **/
	private final List<Color> colors;

	/**
	 * Creates a new instance with at least one color.
	 * @param color1 First color.
	 * @param colors Additional colors.
	 */
	public LinearGradient(Color color1, Color... colors) {
		this.colors = new ArrayList<>();
		this.colors.add(color1);
		this.colors.addAll(Arrays.asList(colors));
	}

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

		double x = v;
		int colorMax = colors.size() - 1;
		double pos = MathUtils.limit(x*colorMax, 0.0, colorMax);

		if (pos == 0.0) {
			return colors.get(0);
		}
		if (pos == colorMax) {
			return colors.get(colorMax);
		}

		double fract = pos - (int) pos;
		Color color1 = colors.get((int) pos);

		if (fract == 0.0) {
			return color1;
		}

		double fractInv = 1.0 - fract;
		Color color2 = colors.get((int) pos + 1);

		double r = fractInv*color1.getRed()   + fract*color2.getRed();
		double g = fractInv*color1.getGreen() + fract*color2.getGreen();
		double b = fractInv*color1.getBlue()  + fract*color2.getBlue();
		double a = fractInv*color1.getAlpha() + fract*color2.getAlpha();

		return new Color(
			(int) Math.round(r),
			(int) Math.round(g),
			(int) Math.round(b),
			(int) Math.round(a)
		);
	}

	@Override
	public void setMode(Mode mode) {
		super.setMode(mode);
	}

	/**
	 * Returns the colors that are used for blending.
	 * @return A list of colors in the order they will be used for blending.
	 */
	public List<Color> getColors() {
		return Collections.unmodifiableList(colors);
	}
}
