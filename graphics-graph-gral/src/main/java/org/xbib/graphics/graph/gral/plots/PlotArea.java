package org.xbib.graphics.graph.gral.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import org.xbib.graphics.graph.gral.graphics.AbstractDrawable;
import org.xbib.graphics.graph.gral.graphics.DrawingContext;
import org.xbib.graphics.graph.gral.util.GraphicsUtils;
import org.xbib.graphics.graph.gral.graphics.Insets2D;

/**
 * Abstract class that represents a canvas on which plot data will be drawn.
 * It serves as base for specialized implementations for different plot types.
 * Derived classes have to implement how the actual drawing is done.
 */
public abstract class PlotArea extends AbstractDrawable {

	/** Default font used for sub-components and the calculation of relative
	sizes. */
	private Font baseFont;
	/** Paint to fill the background. */
	private Paint background;
	/** Stroke to draw the border.
	Property will be serialized using a wrapper. */
	private transient Stroke borderStroke;
	/** Paint to fill the border. */
	private Paint borderColor;
	/** Offset to clip plot graphics in pixels, specified relative to the
	outline of the plot area. */
	private Insets2D clippingOffset;

	/**
	 * Initializes a new instance with default background color and border.
	 */
	public PlotArea() {
		baseFont = null;
		background = Color.WHITE;
		borderStroke = new BasicStroke(1f);
		borderColor = Color.BLACK;
		clippingOffset = new Insets2D.Double(0.0);
	}

	/**
	 * Draws the background of this legend with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected void drawBackground(DrawingContext context) {
		// FIXME duplicate code! See de.erichseifert.gral.Legend
		Paint paint = getBackground();
		if (paint != null) {
			GraphicsUtils.fillPaintedShape(context.getGraphics(),
					getBounds(), paint, null);
		}
	}

	/**
	 * Draws the border of this Legend with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected void drawBorder(DrawingContext context) {
		// FIXME duplicate code! See de.erichseifert.gral.Legend
		Stroke stroke = getBorderStroke();
		if (stroke != null) {
			Paint borderColor = getBorderColor();
			GraphicsUtils.drawPaintedShape(context.getGraphics(),
					getBounds(), borderColor, null, stroke);
		}
	}

	/**
	 * Draws the data using the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected abstract void drawPlot(DrawingContext context);

	/**
	 * Returns the current font used as a default for sub-components ans for
	 * calculation of relative sizes.
	 * @return Current base font.
	 */
	public Font getBaseFont() {
		return baseFont;
	}

	/**
	 * Sets the new font that will be used as a default for sub-components and
	 * for calculation of relative sizes. This method is only used internally
	 * to propagate the base font and shouldn't be used manually.
	 * @param baseFont New base font.
	 */
	public void setBaseFont(Font baseFont) {
		this.baseFont = baseFont;
	}

	/**
	 * Returns the paint which is used to draw the background of the plot area.
	 * @return Paint which is used to fill the background.
	 */
	public Paint getBackground() {
		return background;
	}

	/**
	 * Sets the paint which will be used to fill the background of the plot
	 * area.
	 * @param background Paint which should be used to fill the background.
	 */
	public void setBackground(Paint background) {
		this.background = background;
	}

	/**
	 * Returns the stroke which is used to draw the border of the plot area.
	 * @return Stroke which is used to draw the border.
	 */
	public Stroke getBorderStroke() {
		return borderStroke;
	}

	/**
	 * Sets the stroke which will be used to draw the border of the plot area.
	 * @param stroke Stroke which should be used to draw the border.
	 */
	public void setBorderStroke(Stroke stroke) {
		this.borderStroke = stroke;
	}

	/**
	 * Returns the paint which is used to fill the border of the plot area.
	 * @return Paint which is used to fill the border.
	 */
	public Paint getBorderColor() {
		return borderColor;
	}

	/**
	 * Sets the paint which will be used to fill the border of the plot area.
	 * @param color Paint which should be used to fill the border.
	 */
	public void setBorderColor(Paint color) {
		this.borderColor = color;
	}

	/**
	 * Returns the clipping offset of the plotted data relative to the plot
	 * area. Positive inset values result in clipping inside the plot area,
	 * negative values result in clipping outside the plot area.
	 * Specifying a {@code null} values will turn off clipping.
	 * @return Clipping offset in pixels relative to the outline of the plot
	 * area.
	 */
	public Insets2D getClippingOffset() {
		return clippingOffset;
	}

	/**
	 * Sets the clipping offset of the plotted data relative to the plot area.
	 * Positive inset values result in clipping inside the plot area,
	 * negative values result in clipping outside the plot area.
	 * Specifying a {@code null} values will turn off clipping.
	 * @param offset Clipping offset in pixels relative to the outline of the
	 * plot area.
	 */
	public void setClippingArea(Insets2D offset) {
		this.clippingOffset = offset;
	}
}
