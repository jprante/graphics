package org.xbib.graphics.graph.gral.plots.lines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;


/**
 * <p>Abstract class that renders a line in two-dimensional space.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Punching data points out of the line's shape</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public abstract class AbstractLineRenderer2D implements LineRenderer {

	/** Stroke to draw the line. */
	private transient Stroke stroke;
	/** Gap between points and the line. */
	private double gap;
	/** Decides whether the shape of the gap between points and the line is
	 * rounded. */
	private boolean gapRounded;
	/** Paint to fill the line. */
	private Paint color;

	/**
	 * Initializes a new {@code AbstractLineRenderer2D} instance with
	 * default settings.
	 */
	public AbstractLineRenderer2D() {
		stroke = new BasicStroke(1.5f);
		gap = 0.0;
		gapRounded = false;
		color = Color.BLACK;
	}

	/**
	 * Returns the stroked shape of the specified line.
	 * @param line Shape of the line.
	 * @return Stroked shape.
	 */
	protected Shape stroke(Shape line) {
		if (line == null) {
			return null;
		}
		Stroke stroke = getStroke();
		return stroke.createStrokedShape(line);
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
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
