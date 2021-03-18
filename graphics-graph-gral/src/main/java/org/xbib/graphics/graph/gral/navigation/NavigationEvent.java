package org.xbib.graphics.graph.gral.navigation;

/**
 * Data class that describes a navigational event, like zooming or panning.
 *
 * @param <T> Data type of the value that has been changed.
 */
public class NavigationEvent<T> {
	/** Object that has caused the change. */
	private final Navigator source;
	/** Value before the change. */
	private final T valueOld;
	/** Value after the change. */
	private final T valueNew;

	/**
	 * Initializes a new instance.
	 * @param source Navigator object that has caused the change.
	 * @param valueOld Value before the change
	 * @param valueNew Value after the change.
	 */
	public NavigationEvent(Navigator source, T valueOld, T valueNew) {
		this.source = source;
		this.valueOld = valueOld;
		this.valueNew = valueNew;
	}

	/**
	 * Returns the navigator that has caused the change.
	 * @return Navigator object that has caused the change.
	 */
	public Navigator getSource() {
		return source;
	}

	/**
	 * Returns the value before the change.
	 * @return Value before the change.
	 */
	public T getValueOld() {
		return valueOld;
	}

	/**
	 * Returns the value after the change.
	 * @return Value after the change.
	 */
	public T getValueNew() {
		return valueNew;
	}
}
