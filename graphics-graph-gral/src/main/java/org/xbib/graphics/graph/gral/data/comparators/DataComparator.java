package org.xbib.graphics.graph.gral.data.comparators;

import java.util.Comparator;

import org.xbib.graphics.graph.gral.data.Record;

/**
 * Abstract implementation of a {@code Comparator} for {@code Record} objects.
 * This class allows to specify the index at which the records should be
 * compared.
 */
public abstract class DataComparator implements Comparator<Record> {

	/** Column that should be used for comparing. */
	private final int column;

	/**
	 * Constructor.
	 * @param col index of the column to be compared
	 */
	public DataComparator(int col) {
		this.column = col;
	}

	/**
	 * Returns the column to be compared.
	 * @return column index
	 */
	public int getColumn() {
		return column;
	}
}
