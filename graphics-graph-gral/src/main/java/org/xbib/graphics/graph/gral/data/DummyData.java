package org.xbib.graphics.graph.gral.data;

import java.util.Arrays;


/**
 * Class that represents a data source containing the same value in each cell.
 * It can be used for test purposes or for efficiently creating constant data.
 */
public class DummyData extends AbstractDataSource {

	/** Value that will be returned for all positions in this data source. */
	private final Comparable<?> value;
	/** Number of columns. */
	private final int cols;
	/** Number of rows. */
	private final int rows;

	/**
	 * Creates a new instance with the specified number of columns
	 * and rows, which are filled all over with the same specified value.
	 * @param cols Number of columns.
	 * @param rows Number of rows.
	 * @param value Value of the cells.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public DummyData(int cols, int rows, Comparable<?> value) {
		this.cols = cols;
		this.rows = rows;
		this.value = value;

		Class<? extends Comparable<?>>[] types = new Class[cols];
		Arrays.fill(types, value.getClass());
		setColumnTypes(types);
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	public Comparable<?> get(int col, int row) {
		return value;
	}

	@Override
	public int getColumnCount() {
		return cols;
	}

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	public int getRowCount() {
		return rows;
	}

}
