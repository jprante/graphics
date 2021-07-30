package org.xbib.graphics.graph.gral.io.plots;

import java.io.IOException;
import java.io.OutputStream;

import org.xbib.graphics.graph.gral.graphics.Drawable;

/**
 * Interface providing functions for rendering {@code Drawable}
 * instances and writing them to an output stream. As an example: a plot
 * can be saved into a bitmap file.
 * @see DrawableWriterFactory
 */
public interface DrawableWriter {
	/**
	 * Returns the output format of this writer.
	 * @return String representing the MIME-Type.
	 */
	String getMimeType();

	/**
	 * Stores the specified {@code Drawable} instance.
	 * @param d {@code Drawable} to be written.
	 * @param destination Stream to write to
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	void write(Drawable d, OutputStream destination,
			   double width, double height) throws IOException;

	/**
	 * Stores the specified {@code Drawable} instance.
	 * @param d {@code Drawable} to be written.
	 * @param destination Stream to write to
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	void write(Drawable d, OutputStream destination,
			   double x, double y, double width, double height) throws IOException;
}
