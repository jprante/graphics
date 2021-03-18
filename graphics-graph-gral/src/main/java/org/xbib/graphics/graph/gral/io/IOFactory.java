package org.xbib.graphics.graph.gral.io;

import java.util.List;

/**
 * Interface for factories producing input (reader) or output (writer) classes.
 * This is be used to create a extensible plug-in system for reading or writing
 * data.
 * @param <T> Class of the objects produced by the factory.
 */
public interface IOFactory<T> {
	/**
	 * Returns an object for reading or writing the specified format.
	 * @param mimeType MIME type.
	 * @return Reader or writer for the specified MIME type.
	 */
	T get(String mimeType);

	/**
	 * Returns the capabilities for a specific format.
	 * @param mimeType MIME type of the format
	 * @return Capabilities for the specified format.
	 */
	IOCapabilities getCapabilities(String mimeType);

	/**
	 * Returns a list of capabilities for all supported formats.
	 * @return Supported capabilities.
	 */
	List<IOCapabilities> getCapabilities();

	/**
	 * Returns an array of Strings containing all supported formats.
	 * @return Supported formats.
	 */
	String[] getSupportedFormats();

	/**
	 * Returns whether the specified MIME type is supported.
	 * @param mimeType MIME type.
	 * @return {@code true} if supported, otherwise {@code false}.
	 */
	boolean isFormatSupported(String mimeType);
}
