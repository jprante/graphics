package org.xbib.graphics.graph.gral.graphics.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.xbib.graphics.graph.gral.graphics.Container;
import org.xbib.graphics.graph.gral.graphics.Drawable;
import org.xbib.graphics.graph.gral.graphics.Insets2D;
import org.xbib.graphics.graph.gral.graphics.Location;

/**
 * Implementation of Layout that arranges a {@link Container}'s components
 * according to a certain grid. This is similar to Java's
 * {@link java.awt.BorderLayout}, but also allows components to be placed in
 * each of the corners.
 */
public class EdgeLayout extends AbstractLayout {

	/**
	 * Initializes a layout manager object with the specified space between the
	 * components.
	 * @param gapH Horizontal gap.
	 * @param gapV Vertical gap.
	 */
	public EdgeLayout(double gapH, double gapV) {
		super(gapH, gapV);
	}

	/**
	 * Initializes a layout manager object without space between the
	 * components.
	 */
	public EdgeLayout() {
		this(0.0, 0.0);
	}

	/**
	 * Arranges the components of the specified container according to this
	 * layout.
	 * @param container Container to be laid out.
	 */
	public void layout(Container container) {
		// Fetch components
		Map<Location, Drawable> comps = getComponentsByLocation(container);
		Drawable north = comps.get(Location.NORTH);
		Drawable northEast = comps.get(Location.NORTH_EAST);
		Drawable east = comps.get(Location.EAST);
		Drawable southEast = comps.get(Location.SOUTH_EAST);
		Drawable south = comps.get(Location.SOUTH);
		Drawable southWest = comps.get(Location.SOUTH_WEST);
		Drawable west = comps.get(Location.WEST);
		Drawable northWest = comps.get(Location.NORTH_WEST);
		Drawable center = comps.get(Location.CENTER);

		// Calculate maximum widths and heights
		double widthWest   = getMaxWidth(northWest,  west,   southWest);
		double widthEast   = getMaxWidth(northEast,  east,   southEast);
		double heightNorth = getMaxHeight(northWest, north,  northEast);
		double heightSouth = getMaxHeight(southWest, south,  southEast);

		double gapWest  = (widthWest > 0.0 && center != null) ? getGapX() : 0.0;
		double gapEast  = (widthEast > 0.0 && center != null) ? getGapX() : 0.0;
		double gapNorth = (heightNorth > 0.0 && center != null) ? getGapY() : 0.0;
		double gapSouth = (heightSouth > 0.0 && center != null) ? getGapY() : 0.0;

		Rectangle2D bounds = container.getBounds();
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}

		double xWest   = bounds.getMinX() + insets.getLeft();
		double xCenter = xWest + widthWest + gapWest;
		double xEast   = bounds.getMaxX() - insets.getRight() - widthEast;
		double yNorth  = bounds.getMinY() + insets.getTop();
		double yCenter = yNorth + heightNorth + gapNorth;
		double ySouth  = bounds.getMaxY() - insets.getBottom() - heightSouth;

		double widthAll = widthWest + widthEast;
		double heightAll = heightNorth + heightSouth;
		double gapHAll = gapWest + gapEast;
		double gapVAll = gapNorth - gapSouth;

		layoutComponent(northWest,
			xWest, yNorth,
			widthWest, heightNorth
		);

		layoutComponent(north,
			xCenter, yNorth,
			bounds.getWidth() - insets.getHorizontal() - widthAll - gapHAll,
			heightNorth
		);

		layoutComponent(northEast,
			xEast, yNorth,
			widthEast, heightNorth
		);

		layoutComponent(east,
			xEast, yCenter,
			widthEast,
			bounds.getHeight() - insets.getVertical() - heightAll - gapVAll
		);

		layoutComponent(southEast,
			xEast, ySouth,
			widthEast,
			heightSouth
		);

		layoutComponent(south,
			xCenter, ySouth,
			bounds.getWidth() - insets.getHorizontal() - widthAll - gapHAll,
			heightSouth
		);

		layoutComponent(southWest,
			xWest, ySouth,
			widthWest,
			heightSouth
		);

		layoutComponent(west,
			xWest, yCenter,
			widthWest,
			bounds.getHeight() - insets.getVertical() - heightAll - gapVAll
		);

		layoutComponent(center,
			xCenter, yCenter,
				bounds.getWidth()
					- insets.getLeft() - widthAll
					- insets.getRight() - gapHAll,
				bounds.getHeight()
					- insets.getTop() - heightAll
					- insets.getBottom() - gapVAll
		);
	}

	/**
	 * Returns the preferred size of the specified container using this layout.
	 * @param container Container whose preferred size is to be returned.
	 * @return Preferred extent of the specified container.
	 */
	public Dimension2D getPreferredSize(Container container) {
		// Fetch components
		Map<Location, Drawable> comps = getComponentsByLocation(container);
		Drawable north = comps.get(Location.NORTH);
		Drawable northEast = comps.get(Location.NORTH_EAST);
		Drawable east = comps.get(Location.EAST);
		Drawable southEast = comps.get(Location.SOUTH_EAST);
		Drawable south = comps.get(Location.SOUTH);
		Drawable southWest = comps.get(Location.SOUTH_WEST);
		Drawable west = comps.get(Location.WEST);
		Drawable northWest = comps.get(Location.NORTH_WEST);
		Drawable center = comps.get(Location.CENTER);

		// Calculate maximum widths and heights
		double widthWest    = getMaxWidth(northWest,  west,   southWest);
		double widthCenter  = getMaxWidth(north,      center, south);
		double widthEast    = getMaxWidth(northEast,  east,   southEast);
		double heightNorth  = getMaxHeight(northWest, north,  northEast);
		double heightCenter = getMaxHeight(west,      center, east);
		double heightSouth  = getMaxHeight(southWest, south,  southEast);

		double gapEast  = (widthEast > 0.0 && center != null) ? getGapX() : 0.0;
		double gapWest  = (widthWest > 0.0 && center != null) ? getGapX() : 0.0;
		double gapNorth = (heightNorth > 0.0 && center != null) ? getGapY() : 0.0;
		double gapSouth = (heightSouth > 0.0 && center != null) ? getGapY() : 0.0;

		// Calculate preferred dimensions
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}
		double width = insets.getLeft() + widthEast + gapEast + widthCenter +
			gapWest + widthWest + insets.getRight();
		double height = insets.getTop() + heightNorth + gapNorth + heightCenter +
			gapSouth + heightSouth + insets.getBottom();

		return new org.xbib.graphics.graph.gral.graphics.Dimension2D.Double(
			width, height
		);
	}

	/**
	 * Returns a map all components which are stored with a {@code Location}
	 * constraint in the specified container.
	 * @param container Container which stores the components
	 * @return A map of all components (values) and their constraints (keys) in
	 *         the specified container.
	 */
	private static Map<Location, Drawable> getComponentsByLocation(Container container) {
		Map<Location, Drawable> drawablesByLocation = new HashMap<>();
		for (Drawable d: container) {
			Object constraints = container.getConstraints(d);
			if (constraints instanceof Location) {
				drawablesByLocation.put((Location) constraints, d);
			}
		}
		return drawablesByLocation;
	}

	/**
	 * Returns the maximum width of an array of Drawables.
	 * @param drawables Drawables to be measured.
	 * @return Maximum horizontal extent.
	 */
	private static double getMaxWidth(Drawable... drawables) {
		double width = 0.0;
		for (Drawable d : drawables) {
			if (d == null) {
				continue;
			}
			width = Math.max(width, d.getPreferredSize().getWidth());
		}

		return width;
	}

	/**
	 * Returns the maximum height of an array of Drawables.
	 * @param drawables Drawables to be measured.
	 * @return Maximum vertical extent.
	 */
	private static double getMaxHeight(Drawable... drawables) {
		double height = 0.0;
		for (Drawable d : drawables) {
			if (d == null) {
				continue;
			}
			height = Math.max(height, d.getPreferredSize().getHeight());
		}

		return height;
	}

	/**
	 * Sets the bounds of the specified {@code Drawable} to the specified
	 * values.
	 * @param component {@code Drawable} that should be resized.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param w Width.
	 * @param h Height.
	 */
	private static void layoutComponent(Drawable component,
			double x, double y, double w, double h) {
		if (component == null) {
			return;
		}
		component.setBounds(x, y, w, h);
	}
}
