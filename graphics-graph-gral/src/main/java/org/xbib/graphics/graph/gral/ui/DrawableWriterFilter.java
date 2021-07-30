package org.xbib.graphics.graph.gral.ui;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.filechooser.FileFilter;

import org.xbib.graphics.graph.gral.io.IOCapabilities;
import org.xbib.graphics.graph.gral.util.Messages;

/**
 * File filter that extracts files that can be read with a certain set of
 * {@link org.xbib.graphics.graph.gral.io.IOCapabilities}.
 */
public class DrawableWriterFilter extends FileFilter {
	/** Capabilities that describe the data formats that can be processed by
	this filter. */
	private final IOCapabilities capabilities;

	/**
	 * Creates a new instance and initializes it with an
	 * {@link org.xbib.graphics.graph.gral.io.IOCapabilities} object.
	 * @param capabilities writer capabilities.
	 */
	public DrawableWriterFilter(IOCapabilities capabilities) {
		this.capabilities = capabilities;
	}

	@Override
	public boolean accept(File f) {
		if (f == null) {
			return false;
		}
		if (f.isDirectory()) {
			return true;
		}
		String ext = getExtension(f).toLowerCase();
		for (String extension : capabilities.getExtensions()) {
			if (extension.equals(ext)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return MessageFormat.format(Messages.getString("IO.formatDescription"), //$NON-NLS-1$
				capabilities.getFormat(), capabilities.getName());
	}

	/**
	 * Returns the capabilities filtered by this instance.
	 * @return writer capabilities.
	 */
	public IOCapabilities getWriterCapabilities() {
		return capabilities;
	}

	private static String getExtension(File f) {
		String name = f.getName();
		int lastDot = name.lastIndexOf('.');
		if ((lastDot <= 0) || (lastDot == name.length() - 1)) {
			return ""; //$NON-NLS-1$
		}
		return name.substring(lastDot + 1);
	}
}
