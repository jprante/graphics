package org.xbib.graphics.graph.gral.io.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

import org.xbib.graphics.graph.gral.data.DataSource;
import org.xbib.graphics.graph.gral.io.IOCapabilities;
import org.xbib.graphics.graph.gral.util.MathUtils;
import org.xbib.graphics.graph.gral.util.Messages;

/**
 * Class that writes a data source to a binary image file. This class
 * shouldn't be used directly but using the {@link DataWriterFactory}.
 */
public class ImageWriter extends AbstractDataWriter {
	static {
		addCapabilities(new IOCapabilities(
			"BMP",
			Messages.getString("ImageIO.bmpDescription"),
			"image/bmp",
			new String[] {"bmp", "dib"}
		));

		addCapabilities(new IOCapabilities(
			"GIF",
			Messages.getString("ImageIO.gifDescription"),
			"image/gif",
			new String[] {"gif"}
		));

		addCapabilities(new IOCapabilities(
			"JPEG/JFIF",
			Messages.getString("ImageIO.jpegDescription"),
			"image/jpeg",
			new String[] {
				"jpg", "jpeg", "jpe",
				"jif", "jfif", "jfi"}
		));

		addCapabilities(new IOCapabilities(
			"PNG",
			Messages.getString("ImageIO.pngDescription"),
			"image/png",
			new String[] {"png"}
		));

		addCapabilities(new IOCapabilities(
			"WBMP",
			Messages.getString("ImageIO.wbmpDescription"),
			"image/vnd.wap.wbmp",
			new String[] {"wbmp"}
		));
	}

	/**
	 * Creates a new instance with the specified MIME type.
	 * @param mimeType MIME type of the file format to be read.
	 */
	public ImageWriter(String mimeType) {
		super(mimeType);
		setDefault("factor", 1.0); //$NON-NLS-1$
		setDefault("offset", 0.0); //$NON-NLS-1$
	}

	/**
	 * Stores the specified data source.
	 * @param data DataSource to be stored.
	 * @param output OutputStream to be written to.
	 * @throws IOException if writing the data failed
	 */
	public void write(DataSource data, OutputStream output) throws IOException {
		int w = data.getColumnCount();
		int h = data.getRowCount();

		double factor = this.<Number>getSetting("factor").doubleValue(); //$NON-NLS-1$
		double offset = this.<Number>getSetting("offset").doubleValue(); //$NON-NLS-1$

		byte[] pixelData = new byte[w*h];
		int pos = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Comparable<?> cell = data.get(x, y);
				if (!(cell instanceof Number)) {
					continue;
				}
				Number numericCell = (Number) cell;
				double value = numericCell.doubleValue()*factor + offset;
				byte v = (byte) Math.round(MathUtils.limit(value, 0.0, 255.0));
				pixelData[pos++] = v;
			}
		}

        BufferedImage image =
        	new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0, 0, w, h, pixelData);

        Iterator<javax.imageio.ImageWriter> writers =
        	ImageIO.getImageWritersByMIMEType(getMimeType());
        try {
        	javax.imageio.ImageWriter writer = writers.next();
        	writer.setOutput(ImageIO.createImageOutputStream(output));
        	writer.write(image);
        } catch (NoSuchElementException e) {
        	throw new IOException(MessageFormat.format(
        			"No writer found for MIME type {0}.", getMimeType())); //$NON-NLS-1$
        }
	}

}
