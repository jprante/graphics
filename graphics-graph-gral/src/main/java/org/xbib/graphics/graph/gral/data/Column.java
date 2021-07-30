package org.xbib.graphics.graph.gral.data;

import org.xbib.graphics.graph.gral.data.statistics.Statistics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Class for accessing a specific column of a data source. The data of the
 * column can be accessed using the {@code get(int)} method.</p>
 *
 * <p>Example for accessing value at column 2, row 3 of a data source:</p>
 * <pre>
 * Column col = new Column(dataSource, 2);
 * Number v = col.get(3);
 * </pre>
 *
 * @see DataSource
 */
public class Column<T extends Comparable<T>> implements Iterable<T> {

	private final Class<T> dataType;
	private final List<T> data;

	@SuppressWarnings("unchecked")
	public Column(Class<T> dataType, T... data) {
		this(dataType, Arrays.asList(data));
	}

	public Column(Class<T> dataType, Iterable<T> data) {
		this.dataType = dataType;
		this.data = new ArrayList<>();
		for (T item : data) {
			this.data.add(item);
		}
	}

	public T get(int row) {
		return row >= data.size() ? null : data.get(row);
	}

	public int size() {
		return data.size();
	}

	/**
	 * Returns whether this column only contains numbers.
	 * @return {@code true} if this column is numeric, otherwise {@code false}.
	 */
	public boolean isNumeric() {
		return Number.class.isAssignableFrom(getType());
	}

	public Class<? extends Comparable<?>> getType() {
		return dataType;
	}

	public double getStatistics(String key) {
		return new Statistics(data).get(key);
	}

	@Override
	public int hashCode() {
		return dataType.hashCode() ^ data.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Column)) {
			return false;
		}
		Column<?> column = (Column<?>) obj;
		return getType().equals(column.getType()) && data.equals(column.data);
	}

	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}
}
