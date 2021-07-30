package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Color;
import java.awt.Paint;

import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Class that generates different color shades for values between 0.0 and 1.0.
 */
public class HeatMap extends ScaledContinuousColorMapper {

	private static final Color[] COLORS = {
	    new Color(0.0f, 0.0f, 0.0f),
	    new Color(0.0f, 0.0f, 1.0f),
	    new Color(1.0f, 0.0f, 0.0f),
	    new Color(1.0f, 1.0f, 0.0f),
	    new Color(1.0f, 1.0f, 1.0f)
	};

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
		double xInv = 1.0 - x;
		double xInv2 = xInv*xInv;
		double x2 = x*x;

		// Bernstein coefficients
		double[] coeffs = {
			xInv2*xInv2,
			4.0*x*xInv2*xInv,
			6.0*x2*xInv2,
			4.0*x*x2*xInv,
			x2*x2
		};

		double r = 0.0, g = 0.0, b = 0.0, a = 0.0;
		for (int i = 0; i < COLORS.length; i++) {
			r += coeffs[i]*COLORS[i].getRed();
			g += coeffs[i]*COLORS[i].getGreen();
			b += coeffs[i]*COLORS[i].getBlue();
			a += coeffs[i]*COLORS[i].getAlpha();
		}

		return new Color(
			(float) MathUtils.limit(r, 0.0, 255.0)/255f,
			(float) MathUtils.limit(g, 0.0, 255.0)/255f,
			(float) MathUtils.limit(b, 0.0, 255.0)/255f,
			(float) MathUtils.limit(a, 0.0, 255.0)/255f
		);
	}

	@Override
	public void setMode(Mode mode) {
		super.setMode(mode);
	}
}
