package org.xbib.graphics.graph.gral.io.data;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.xbib.graphics.graph.gral.io.AbstractIOFactory;

/**
 * <p>A factory class that produces {@code DataReader} instances for a
 * specified format. The produced readers can be used to retrieve data from
 * an {@code InputStream} and to get a {@code DataSource} instance.</p>
 * <p>Example usage:</p>
 * <pre>
 * DataReaderFactory factory = DataReaderFactory.getInstance();
 * DataReader reader = factory.get("text/csv");
 * DataSource = reader.read(new FileInputStream(filename), Double.class);
 * </pre>
 */
public final class DataReaderFactory extends AbstractIOFactory<DataReader> {
	/** Singleton instance. */
	private static DataReaderFactory instance;

	/**
	 * Constructor that initializes the factory.
	 * @throws IOException if the properties file could not be found.
	 */
	private DataReaderFactory() throws IOException {
		super("datareaders.properties"); //$NON-NLS-1$
	}

	/**
	 * Returns the instance of the factory.
	 * @return Instance of the factory.
	 */
	public static DataReaderFactory getInstance() {
		if (instance == null) {
			try {
				instance = new DataReaderFactory();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	@Override
	public DataReader get(String mimeType) {
		DataReader reader = null;
		Class<? extends DataReader> clazz = getTypeClass(mimeType);
		//IOCapabilities capabilities = getCapabilities(mimeType);
		try {
			if (clazz != null) {
				Constructor<? extends DataReader> constructor =
					clazz.getDeclaredConstructor(String.class);
				reader = constructor.newInstance(mimeType);
			}
		} catch (SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException | IllegalArgumentException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (reader == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Unsupported MIME type: {0}", mimeType)); //$NON-NLS-1$
		}

		return reader;
	}
}
