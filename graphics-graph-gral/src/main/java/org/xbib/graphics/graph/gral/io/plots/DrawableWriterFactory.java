package org.xbib.graphics.graph.gral.io.plots;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.xbib.graphics.graph.gral.io.AbstractIOFactory;

/**
 * <p>Class that provides {@code DrawableWriter} implementations for
 * different file formats.</p>
 *
 * <p>Example Usage:</p>
 * <pre>
 * DrawableWriterFactory factory = DrawableWriterFactory.getInstance();
 * DrawableWriter writer = factory.get("application/pdf");
 * writer.write(plot, new FileOutputStream(filename));
 * </pre>
 *
 * @see DrawableWriter
 */
public final class DrawableWriterFactory extends AbstractIOFactory<DrawableWriter> {
	/** Singleton instance. */
	private static DrawableWriterFactory instance;

	/**
	 * Constructor that initializes the factory.
	 * @throws IOException if the properties file could not be found.
	 */
	private DrawableWriterFactory() throws IOException {
		super("drawablewriters.properties"); //$NON-NLS-1$
	}

	/**
	 * Returns an instance of this DrawableWriterFactory.
	 * @return Instance.
	 */
	public static DrawableWriterFactory getInstance() {
		if (instance == null) {
			try {
				instance = new DrawableWriterFactory();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	@Override
	public DrawableWriter get(String mimeType) {
		DrawableWriter writer = null;
		Class<? extends DrawableWriter> clazz = getTypeClass(mimeType);
		//IOCapabilities capabilities = getCapabilities(mimeType);
		try {
			if (clazz != null) {
				Constructor<? extends DrawableWriter> constructor =
					clazz.getDeclaredConstructor(String.class);
				writer = constructor.newInstance(mimeType);
			}
		} catch (SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException | IllegalArgumentException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (writer == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Unsupported MIME type: {0}", mimeType)); //$NON-NLS-1$
		}

		return writer;
	}
}
