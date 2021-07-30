package org.xbib.graphics.graph.gral.data.comparators;

import  org.xbib.graphics.graph.gral.data.Record;

/**
 * Class that represents a {@code DataComparator} for comparing two records
 * at a defined index for ascending order.
 */
public class Ascending extends DataComparator {

	/**
	 * Creates a new Ascending object for sorting according to the specified
	 * column.
	 * @param col Column index to be compared.
	 */
	public Ascending(int col) {
		super(col);
	}

	/**
	 * <p>Compares the values of two records at the specified column for order and
	 * returns a corresponding integer:</p>
	 * <ul>
	 *   <li>a negative value means {@code record1} is smaller than {@code record2}</li>
	 *   <li>0 means {@code record1} is equal to {@code record2}</li>
	 *   <li>a positive value means {@code record1} is larger than {@code record2}</li>
	 * </ul>
	 * @param record1 First record
	 * @param record2 Second record
	 * @return An integer number describing the order:
	 *         a negative value if {@code record1} is smaller than {@code record2},
	 *         0 if {@code record1} is equal to {@code record2},
	 *         a positive value if {@code record1} is larger than {@code record2},
	 */
	@SuppressWarnings("unchecked")
	public int compare(Record record1, Record record2) {
		Comparable<Object> value1 = record1.get(getColumn());
		Comparable<Object> value2 = record2.get(getColumn());

		// null values sort as if larger than non-null values
		if (value1 == null && value2 == null) {
			return 0;
		} else if (value1 == null) {
			return 1;
		} else if (value2 == null) {
			return -1;
		}

		return value1.compareTo(value2);
	}

}
