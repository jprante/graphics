package org.xbib.graphics.graph.gral.io.data;

import java.util.HashMap;
import java.util.Map;

import org.xbib.graphics.graph.gral.io.IOCapabilitiesStorage;

/**
 * Base implementation for classes that read data sources from input streams.
 */
public abstract class AbstractDataReader extends IOCapabilitiesStorage
		implements DataReader {
	/** Settings stored as (key, value) pairs. */
	private final Map<String, Object> settings;
	/** Default settings. */
	private final Map<String, Object> defaults;
	/** Data format as MIME type string. */
	private final String mimeType;

	/**
	 * Initializes a new reader with MIME type information.
	 * @param mimeType MIME type
	 */
	public AbstractDataReader(String mimeType) {
		settings = new HashMap<>();
		defaults = new HashMap<>();
		this.mimeType = mimeType;
	}

	/**
	 * Returns the MIME type.
	 * @return MIME type string.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns the setting for the specified key.
	 * @param <T> return type
	 * @param key key of the setting
	 * @return the value of the setting
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSetting(String key) {
		if (!settings.containsKey(key)) {
			return (T) defaults.get(key);
		}
		return (T) settings.get(key);
	}

	/**
	 * Sets the setting for the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 * @param value value of the setting
	 */
	public <T> void setSetting(String key, T value) {
		settings.put(key, value);
	}

	/**
	 * Defines a default value for the setting with the specified key.
	 * @param <T> Data type of value.
	 * @param key Setting key.
	 * @param value Default value.
	 */
	protected <T> void setDefault(String key, T value) {
		defaults.put(key, value);
	}

}
