package org.xbib.graphics.graph.gral.navigation;

import org.xbib.graphics.graph.gral.util.PointND;

/**
 * An interface for classes that want to be notified on navigation changes like
 * panning or zooming.
 *
 * @see Navigator
 */
public interface NavigationListener {
	/**
	 * A method that gets called after the center of an object in the
	 * {@code PlotNavigator} has changed.
	 * @param event An object describing the change event.
	 */
	void centerChanged(NavigationEvent<PointND<? extends Number>> event);

	/**
	 * A method that gets called after the zoom level of an object in the
	 * {@code PlotNavigator} has changed.
	 * @param event An object describing the change event.
	 */
	void zoomChanged(NavigationEvent<Double> event);
}
