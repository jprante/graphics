package org.xbib.graphics.graph.gral.io;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class that provides the basic functions to store capabilities of
 * a reader or a writer implementation.
 */
public abstract class IOCapabilitiesStorage {
	/** Set of all registered capabilities. */
	private static final Set<IOCapabilities> capabilities
		= new HashSet<>();

	/**
	 * Initializes a new storage instance.
	 */
	protected IOCapabilitiesStorage() {
	}

	/**
	 * Returns a {@code Set} with capabilities for all supported formats.
	 * @return Capabilities.
	 */
	public static Set<IOCapabilities> getCapabilities() {
		return Collections.unmodifiableSet(capabilities);
	}

	/**
	 * Adds the specified capabilities to the Set of supported formats.
	 * @param capabilities Capabilities to be added.
	 */
	protected static void addCapabilities(IOCapabilities capabilities) {
		IOCapabilitiesStorage.capabilities.add(capabilities);
	}
}
