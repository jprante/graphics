package org.xbib.graphics.graph.gral.data.statistics;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.xbib.graphics.graph.gral.data.AbstractDataSource;
import org.xbib.graphics.graph.gral.data.DataChangeEvent;
import org.xbib.graphics.graph.gral.data.DataListener;
import org.xbib.graphics.graph.gral.data.DataSource;

/**
 * Abstract base class for histograms. Derived classes must
 * make sure the {@code getColumnTypes()} method returns a correct array
 * with column types.
 * @see AbstractDataSource#setColumnTypes(Class...)
 */
public abstract class AbstractHistogram2D extends AbstractDataSource
		implements DataListener {

	/** Data source that is used to build the histogram. */
	private final DataSource data;

	/**
	 * Initializes a new histograms with a data source.
	 * @param data Data source to be analyzed.
	 */
	@SuppressWarnings("unchecked")
	public AbstractHistogram2D(DataSource data) {
		this.data = data;
		this.data.addDataListener(this);
	}

	/**
	 * Recalculates the histogram values.
	 */
	protected abstract void rebuildCells();

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been added.
	 */
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
		notifyDataAdded(events);
	}

	/**
	 * Method that is invoked when data has been updated.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been updated.
	 */
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
		notifyDataUpdated(events);
	}

	/**
	 * Method that is invoked when data has been removed.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been removed.
	 */
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
		notifyDataRemoved(events);
	}

	/**
	 * Method that is invoked when data has been added, updated, or removed.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been changed.
	 */
	private void dataChanged(DataSource source, DataChangeEvent... events) {
		rebuildCells();
	}

	/**
	 * Returns the data source associated to this histogram.
	 * @return Data source
	 */
	public DataSource getData() {
		return data;
	}

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the
	 *         input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Normal deserialization
		in.defaultReadObject();

		// Restore listeners
		data.addDataListener(this);
	}
}
