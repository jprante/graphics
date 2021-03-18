package org.xbib.graphics.graph.gral.io.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.xbib.graphics.graph.gral.data.DataSource;
import org.xbib.graphics.graph.gral.data.DataTable;
import org.xbib.graphics.graph.gral.io.IOCapabilities;
import org.xbib.graphics.graph.gral.util.Messages;

/**
 * Class that reads a data source from a binary image file. This class
 * shouldn't be used directly but using the {@link DataReaderFactory}.
 */
public class ImageReader extends AbstractDataReader {
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
	public ImageReader(String mimeType) {
		super(mimeType);
		setDefault("factor", 1.0);
		setDefault("offset", 0.0);
	}

	/**
	 * Returns a data source that was imported.
	 * @param input Input to be read.
	 * @param types Number types for the columns of the data source.
	 * @return DataSource Imported data.
	 * @throws IOException when the file format is not valid or when
	 *         experiencing an error during file operations.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataSource read(InputStream input, Class<? extends Comparable<?>>... types)
			throws IOException {
		BufferedImage image = ImageIO.read(input);

		int w = image.getWidth();
		int h = image.getHeight();

		Class[] colTypes = new Class[w];
		Arrays.fill(colTypes, Double.class);
		DataTable data = new DataTable(colTypes);

		double factor = this.<Number>getSetting("factor").doubleValue();
		double offset = this.<Number>getSetting("offset").doubleValue();

		int[] pixelData = new int[w];
		Double[] rowData = new Double[w];
		for (int y = 0; y < h; y++) {
			image.getRGB(0, y, pixelData.length, 1, pixelData, 0, 0);
			for (int x = 0; x < pixelData.length; x++) {
				//double a = (pixelData[x] >> 24) & 0xFF;
				double r = (pixelData[x] >> 16) & 0xFF;
				//double g = (pixelData[x] >>  8) & 0xFF;
				//double b = (pixelData[x] >>  0) & 0xFF;
				rowData[x] = r*factor + offset;
			}
			data.add(rowData);
		}

		return data;
	}

}
