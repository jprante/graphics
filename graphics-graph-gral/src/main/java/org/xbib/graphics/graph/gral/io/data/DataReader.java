package org.xbib.graphics.graph.gral.io.data;

import java.io.IOException;
import java.io.InputStream;

import org.xbib.graphics.graph.gral.data.DataSource;

/**
 * Interface that provides a function to retrieve a data source.
 */
public interface DataReader {
	/**
	 * Returns a data source that contains the imported data.
	 * @param input Input to be read.
	 * @param types Types for the columns of the data source.
	 * @return Imported data.
	 * @throws IOException when the file format is not valid or when
	 *         experiencing an error during file operations.
	 */
	@SuppressWarnings("unchecked")
	DataSource read(InputStream input, Class<? extends Comparable<?>>... types)
		throws IOException;

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
