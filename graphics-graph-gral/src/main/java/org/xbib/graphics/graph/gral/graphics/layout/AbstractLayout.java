package org.xbib.graphics.graph.gral.graphics.layout;

public abstract class AbstractLayout implements Layout {

	/** Horizontal spacing of components. */
	private double gapX;
	/** Vertical spacing of components. */
	private double gapY;

	public AbstractLayout(double gapX, double gapY) {
		this.gapX = gapX;
		this.gapY = gapY;
	}

	@Override
	public double getGapX() {
		return gapX;
	}

	@Override
	public void setGapX(double gapX) {
		this.gapX = gapX;
	}

	@Override
	public double getGapY() {
		return gapY;
	}

	@Override
	public void setGapY(double gapY) {
		this.gapY = gapY;
	}
}
