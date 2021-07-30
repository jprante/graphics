package org.xbib.graphics.graph.gral.ui;

import java.util.List;

import javax.swing.JFileChooser;

import org.xbib.graphics.graph.gral.io.IOCapabilities;

/**
 * A file chooser implementation that can be for export purposes.
 */
@SuppressWarnings("serial")
public class ExportChooser extends JFileChooser {

	/**
	 * Creates a new instance and initializes it with an array of
	 * {@link org.xbib.graphics.graph.gral.io.IOCapabilities}.
	 * @param strict Determines whether this dialog allows only the file formats
	 *               specified in {@code capabilities}.
	 * @param capabilities List of objects describing the file formats that
	 *                     are supported by this dialog.
	 */
	public ExportChooser(boolean strict, List<IOCapabilities> capabilities) {
		setAcceptAllFileFilterUsed(!strict);
		for (IOCapabilities c : capabilities) {
			addChoosableFileFilter(new DrawableWriterFilter(c));
		}
	}

}
