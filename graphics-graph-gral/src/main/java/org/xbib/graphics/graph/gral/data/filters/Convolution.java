package org.xbib.graphics.graph.gral.data.filters;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.xbib.graphics.graph.gral.data.DataSource;
import org.xbib.graphics.graph.gral.util.DataUtils;
import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * <p>Class that applies a specified kernel to a data source to convolve it.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Getting and setting the {@code Kernel} used for convolution</li>
 * </ul>
 */
public class Convolution extends Filter2D {

	/** Kernel that provides the values to convolve the data source. */
	private final Kernel kernel;

	/**
	 * Initialized a new instance with the specified data source, convolution
	 * kernel, edge handling mode, and columns to be filtered.
	 * @param original DataSource to be filtered.
	 * @param kernel Kernel to be used.
	 * @param mode Mode of filtering.
	 * @param cols Column indexes.
	 */
	public Convolution(DataSource original, Kernel kernel, Mode mode, int... cols) {
		super(original, mode, cols);
		this.kernel = kernel;
		filter();
	}

	/**
	 * Returns the kernel.
	 * @return Kernel used for convolution.
	 */
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	protected void filter() {
		clear();
		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			Double[] filteredRow = new Double[getColumnCountFiltered()];
			for (int colIndex = 0; colIndex < filteredRow.length; colIndex++) {
				int colIndexOriginal = getIndexOriginal(colIndex);
				filteredRow[colIndex] = convolve(colIndexOriginal, rowIndex);
			}
			add(filteredRow);
		}
	}

	/**
	 * Calculates the convolved value of the data with the specified column
	 * and row.
	 * @param col Column index.
	 * @param row Row index.
	 * @return Convolved value using the set kernel.
	 */
	private double convolve(int col, int row) {
		Kernel kernel = getKernel();
		if (kernel == null) {
			Comparable<?> original = getOriginal(col, row);
			return DataUtils.getValueOrDefault((Number) original, Double.NaN);
		}
		double sum = 0.0;
		for (int k = kernel.getMinIndex(); k <= kernel.getMaxIndex(); k++) {
			int r = row + k;
			Comparable<?> original = getOriginal(col, r);
			double v = DataUtils.getValueOrDefault((Number) original, Double.NaN);
			if (!MathUtils.isCalculatable(v)) {
				return v;
			}
			sum += kernel.get(k) * v;
		}
		return sum;
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

		// Update caches
		dataUpdated(this);
	}
}
