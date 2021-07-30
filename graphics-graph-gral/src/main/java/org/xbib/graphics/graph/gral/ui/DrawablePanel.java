package org.xbib.graphics.graph.gral.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;

import javax.swing.JPanel;

import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.graphics.DrawingContext;

/**
 * A class that represents an adapter between the components of this library
 * and Swing. It displays a single {@code Drawable} in a {@code JPanel}.
 */
@SuppressWarnings("serial")
public class DrawablePanel extends JPanel {

	/** Drawable that should be displayed. */
	private final Drawable drawable;

	/** Defines whether this panel uses antialiasing. */
	private boolean antialiased;

	/**
	 * Initializes a new instance with the specified {@code Drawable}.
	 * Antialiasing is enabled by default.
	 * @param drawable {@code Drawable} to be displayed
	 */
	public DrawablePanel(Drawable drawable) {
		this.drawable = drawable;
		setOpaque(false);
		antialiased = true;
	}

	/**
	 * Returns the {@code Drawable} instance that is displayed by this
	 * panel.
	 * @return {@code Drawable} instance
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (isVisible()) {
			Graphics2D graphics = (Graphics2D) g;
			if (isAntialiased()) {
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}

			getDrawable().draw(new DrawingContext(graphics));
		}
	}

	@Override
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		getDrawable().setBounds(bounds);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		getDrawable().setBounds(0.0, 0.0, width, height);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dims = super.getPreferredSize();
		Dimension2D dimsPlot = getDrawable().getPreferredSize();
		dims.setSize(dimsPlot);
		return dims;
	}

	@Override
	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}

	/**
	 * Returns whether antialiasing is applied.
	 * @return {@code true} if the panel uses antialiasing, {@code false} otherwise.
	 */
	public boolean isAntialiased() {
		return antialiased;
	}

	/**
	 * Sets whether antialiasing should be applied.
	 * @param antialiased {@code true} if the panel should use antialiasing, {@code false} otherwise.
	 */
	public void setAntialiased(boolean antialiased) {
		this.antialiased = antialiased;
	}
}
