package org.xbib.graphics.graph.gral.navigation;

/**
 * Interface for classes that can provide a {@code Navigator} which translates
 * navigational actions.
 */
public interface Navigable {
	/**
	 * Returns a navigator instance that can control the current object.
	 * @return A navigator instance.
	 */
	Navigator getNavigator();
}
