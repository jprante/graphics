package org.xbib.graphics.graph.gral.data;

import org.xbib.graphics.graph.gral.data.statistics.Statistics;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Locale;

/**
 * Abstract base for reading substructures of a data source, i.e. columns or
 * rows. {@code DataAccessor}s are iterable and provide utility methods
 * for statistics and array conversion.
 * @see DataSource
 */
public abstract class DataAccessor implements Iterable<Comparable<?>> {

	/** Data source that provides the values that should be accessed. */
	private final DataSource source;
	/** Index of current column or row. */
	private final int index;

	/**
	 * Initializes a new instance with the specified data source and an access
	 * index.
	 * @param source Data source.
	 * @param index Column index.
	 */
	public DataAccessor(DataSource source, int index) {
		this.source = source;
		this.index = index;
	}

	/**
	 * Returns the data source containing this column.
	 * @return Data source containing this column.
	 */
	public DataSource getSource() {
		return source;
	}

	/**
	 * Returns the index to access the data source.
	 * @return Data index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the value of the data source for the specified index.
	 * @param index Index.
	 * @return Value of the accessed cell.
	 */
	public abstract Comparable<?> get(int index);

	/**
	 * Returns the number of elements in this column.
	 * @return Number of elements
	 */
	public abstract int size();

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DataAccessor)) {
			return false;
		}
		DataAccessor accessor = (DataAccessor) obj;
		int size = size();
		if (accessor.size() != size) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			Comparable<?> foreignValue = accessor.get(i);
			Comparable<?> thisValue = get(i);
			if (foreignValue == null) {
				if (thisValue != null) {
					return false;
				}
				continue;
			}
			if (!foreignValue.equals(thisValue)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return source.hashCode() ^ index;
	}

	@Override
	public String toString() {
		return String.format(Locale.US,
			"%s[source=%s,index=%d]", //$NON-NLS-1$
			getClass().getName(), getSource(), getIndex());
	}

	/**
	 * Converts the data column to an array.
	 * @param data Optional array as data sink.
	 *             If array is {@code null} a new array will be created.
	 * @return Array with row data;
	 */
	public Comparable<?>[] toArray(Comparable<?>[] data) {
		if (data == null) {
			data = new Comparable<?>[size()];
		}
		if (data.length != size()) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Array of size {0,number,integer} does not match {1,number,integer} elements.", //$NON-NLS-1$
				data.length, size()));
		}
		for (int i = 0; i < data.length; i++) {
			data[i] = get(i);
		}
		return data;
	}

	/**
	 * Returns the specified statistical information for this data.
	 * @param key Requested Statistical information.
	 * @return Calculated value.
	 */
	public double getStatistics(String key) {
		Statistics statistics = new Statistics(this);
		return statistics.get(key);
	}

    /**
     * Returns an iterator over the elements of this object.
     * @return an iterator.
     */
	public Iterator<Comparable<?>> iterator() {
		return new Iterator<Comparable<?>>() {
			private int i;

			public boolean hasNext() {
				return i < size();
			}

			public Comparable<?> next() {
				return get(i++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
