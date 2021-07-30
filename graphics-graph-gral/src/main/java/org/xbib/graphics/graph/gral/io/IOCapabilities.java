package org.xbib.graphics.graph.gral.io;

/**
 * Class that stores information on a <i>reader</i> or <i>writer</i>
 * implementation.
 */
public class IOCapabilities {
	/** Short format name. */
	private final String format;
	/** Long format name. */
	private final String name;
	/** MIME type of format. */
	private final String mimeType;
	/** File extensions commonly used for this format. */
	private final String[] extensions;

	/**
	 * Creates a new {@code IOCapabilities} object with the specified
	 * format, name, MIME-Type and filename extensions.
	 * @param format Format.
	 * @param name Name.
	 * @param mimeType MIME-Type
	 * @param extensions Extensions.
	 */
	public IOCapabilities(String format, String name, String mimeType,
			String[] extensions) {
		this.format = format;
		this.name = name;
		this.mimeType = mimeType;
		// TODO Check that there is at least one filename extension
		this.extensions = extensions;
	}

	/**
	 * Returns the format.
	 * @return Format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Returns the name of the format.
	 * @return Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the MIME-Type of the format.
	 * @return Format.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns an array with Strings containing all possible filename
	 * extensions.
	 * @return Filename Extensions.
	 */
	public String[] getExtensions() {
		return extensions;
	}
}
