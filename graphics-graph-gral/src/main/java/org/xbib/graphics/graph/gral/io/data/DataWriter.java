package org.xbib.graphics.graph.gral.io.data;

import java.io.IOException;
import java.io.OutputStream;

import org.xbib.graphics.graph.gral.data.DataSource;

/**
 * Interface that provides a function to store a data source.
 */
public interface DataWriter {
	/**
	 * Stores the specified data source.
	 * @param data DataSource to be stored.
	 * @param output OutputStream to be written to.
	 * @throws IOException if writing the data failed
	 */
	void write(DataSource data, OutputStream output) throws IOException;

	/**
	 * Returns the setting for the specified key.
	 * @param <T> return type
	 * @param key key of the setting
	 * @return the value of the setting
	 */
	<T> T getSetting(String key);

	/**
	 * Sets the setting for the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 * @param value value of the setting
	 */
	<T> void setSetting(String key, T value);

}
