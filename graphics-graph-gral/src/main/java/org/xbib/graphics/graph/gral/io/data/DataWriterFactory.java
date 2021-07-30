package org.xbib.graphics.graph.gral.io.data;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.xbib.graphics.graph.gral.io.AbstractIOFactory;

/**
 * <p>A factory class that produces {@code DataWriter} instances for a
 * specified format. The produced writers can be used to output a
 * {@code DataSource} to a data sink.</p>
 * <p>Example usage:</p>
 * <pre>
 * DataWriterFactory factory = DataWriterFactory.getInstance();
 * DataWriter writer = factory.get("image/png");
 * writer.write(data);
 * </pre>
 */
public final class DataWriterFactory extends AbstractIOFactory<DataWriter> {
	/** Singleton instance. */
	private static DataWriterFactory instance;

	/**
	 * Constructor that initializes the factory.
	 * @throws IOException if the properties file could not be found.
	 */
	private DataWriterFactory() throws IOException {
		super("datawriters.properties");
	}

	/**
	 * Returns the instance of the factory.
	 * @return Instance of the factory.
	 */
	public static DataWriterFactory getInstance() {
		if (instance == null) {
			try {
				instance = new DataWriterFactory();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	@Override
	public DataWriter get(String mimeType) {
		DataWriter writer = null;
		Class<? extends DataWriter> clazz = getTypeClass(mimeType);
		//IOCapabilities capabilities = getCapabilities(mimeType);
		try {
			if (clazz != null) {
				Constructor<? extends DataWriter> constructor =
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
