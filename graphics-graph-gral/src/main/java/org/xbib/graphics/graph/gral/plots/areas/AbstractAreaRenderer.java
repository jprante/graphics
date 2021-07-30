package org.xbib.graphics.graph.gral.plots.areas;

import java.awt.Color;
import java.awt.Paint;

/**
 * <p>Abstract class that renders an area in two-dimensional space.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Punching data points out of the area's shape</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public abstract class AbstractAreaRenderer implements AreaRenderer {

	/** Gap between points and the area. */
	private double gap;
	/** Decides whether the shape of the gap between points and the area is
	 * rounded. */
	private boolean gapRounded;
	/** Paint to fill the area. */
	private Paint color;

	/**
	 * Initializes a new instance with default settings.
	 */
	public AbstractAreaRenderer() {
		gap = 0.0;
		gapRounded = false;
		color = Color.GRAY;
	}

	@Override
	public double getGap() {
		return gap;
	}

	@Override
	public void setGap(double gap) {
		this.gap = gap;
	}

	@Override
	public boolean isGapRounded() {
		return gapRounded;
	}

	@Override
	public void setGapRounded(boolean gapRounded) {
		this.gapRounded = gapRounded;
	}

	@Override
	public Paint getColor() {
		return color;
	}

	@Override
	public void setColor(Paint color) {
		this.color = color;
	}
}
