package org.xbib.graphics.graph.gral.plots.colors;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.xbib.graphics.graph.gral.util.HaltonSequence;
import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Class that generates seemingly random colors for specified index values.
 */
public class QuasiRandomColors extends IndexedColorMapper {

	/** Object for mapping a plot value to a hue. */
	private final HaltonSequence seqHue = new HaltonSequence(3);
	/** Object for mapping a plot value to a saturation. */
	private final HaltonSequence seqSat = new HaltonSequence(5);
	/** Object for mapping a plot value to a brightness. */
	private final HaltonSequence seqBrightness = new HaltonSequence(2);
	/** Cache for colors that have already been generated. */
	private final Map<Integer, Color> colorCache;
	/** Variance settings for hue, saturation and brightness. */
	//FIXME duplicate code! See RandomColors
	private float[] colorVariance;

	/**
	 * Creates a new QuasiRandomColors object with default color variance.
	 */
	public QuasiRandomColors() {
		colorCache = new HashMap<>();
		colorVariance = new float[] {
			0.00f, 1.00f,  // Hue
			0.75f, 0.25f,  // Saturation
			0.25f, 0.75f   // Brightness
		};
	}

	/**
	 * Returns the Paint associated to the specified index value.
	 * @param index Numeric index.
	 * @return Paint object.
	 */
	@Override
	public Paint get(int index) {
		Integer key = index;
		if (colorCache.containsKey(key)) {
			return colorCache.get(key);
		}
		float[] colorVariance = getColorVariance();
		float hue = colorVariance[0] + colorVariance[1]*seqHue.next().floatValue();
		float saturation = colorVariance[2] + colorVariance[3]*seqSat.next().floatValue();
		float brightness = colorVariance[4] + colorVariance[5]*seqBrightness.next().floatValue();
		Color color = Color.getHSBColor(
			hue,
			MathUtils.limit(saturation, 0f, 1f),
			MathUtils.limit(brightness, 0f, 1f)
		);
		colorCache.put(key, color);
		return color;
	}

	/**
	 * Returns the current color variance.
	 * @return Range of hue, saturation and brightness a color can have.
	 */
	public float[] getColorVariance() {
		return colorVariance;
	}

	/**
	 * Sets the current color variance.
	 * @param colorVariance Range of hue, saturation and brightness a color
	 *        can have.
	 */
	public void setColorVariance(float[] colorVariance) {
		this.colorVariance = colorVariance;
	}
}
