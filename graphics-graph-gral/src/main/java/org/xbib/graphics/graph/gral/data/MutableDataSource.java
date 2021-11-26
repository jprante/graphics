package org.xbib.graphics.graph.gral.data;

import org.xbib.graphics.graph.gral.data.comparators.DataComparator;
import java.util.List;

/**
 * <p>Interface for write access to tabular data. The access includes adding,
 * modifying, and deleting of the data.</p>
 * <p>All data can be sorted row-wise with the method
 * {@code sort(DataComparator...)}. For example, this way column 1 could be
 * sorted ascending and column 3 descending.
 *
 * @see DataSource
 */
public interface MutableDataSource extends DataSource {
	/**
	 * Adds a row with the specified comparable values. The values are added in
	 * the order they are specified. If the types of the data sink columns and
	 * the values do not match, an {@code IllegalArgumentException} is thrown.
	 * @param values values to be added as a row.
	 * @return Index of the row that has been added.
	 */
	int add(Comparable<?>... values);

	/**
	 * Adds a row with the specified container's elements to the data sink. The
	 * values are added in the order they are specified. If the types of the
	 * data sink columns and the values do not match, an
	 * {@code IllegalArgumentException} is thrown.
	 * @param values values to be added as a row.
	 * @return Index of the row that has been added.
	 */
	int add(List<? extends Comparable<?>> values);

	/**
	 * Adds the specified row to the data sink. The values are added in the
	 * order they are specified. If the types of the data sink columns and the
	 * values do not match, an {@code IllegalArgumentException} is thrown.
	 * @param row Row to be added.
	 * @return Index of the row that has been added.
	 */
	int add(Row row);

	/**
	 * Removes a specified row from the data sink.
	 * @param row Index of the row to remove.
	 */
	void remove(int row);

	/**
	 * Removes the last row from the data sink.
	 */
	void removeLast();

	/**
	 * Deletes all rows this data sink contains.
	 */
	void clear();

	/**
	 * Sets the value of a cell specified by its column and row indexes.
	 * @param <T> Data type of the cell.
	 * @param col Column of the cell to change.
	 * @param row Row of the cell to change.
	 * @param value New value to be set.
	 * @return Old value that was replaced.
	 */
	<T> Comparable<T> set(int col, int row, Comparable<T> value);

	/**
	 * Sorts the data sink rows with the specified sorting rules. The row
	 * values are compared in the way the comparators are specified.
	 * @param comparators Comparators used for sorting.
	 */
	void sort(final DataComparator... comparators);

	/**
	 * Sets the name of this series.
	 * @param name name to be set
	 */
	void setName(String name);
}