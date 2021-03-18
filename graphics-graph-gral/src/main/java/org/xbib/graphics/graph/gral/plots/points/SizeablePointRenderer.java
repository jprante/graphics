package org.xbib.graphics.graph.gral.plots.points;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import org.xbib.graphics.graph.gral.data.Row;
import org.xbib.graphics.graph.gral.util.DataUtils;
import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Class that provides {@code Drawable}s, which are sized accordingly to
 * the data.
 */
public class SizeablePointRenderer extends DefaultPointRenderer2D {

	/** Index of the column for the point size. */
	private int column;

	/**
	 * Initializes a new object.
	 */
	public SizeablePointRenderer() {
		column = 2;
	}

	/**
	 * Returns the index of the column which is used for point sizes.
	 * @return index of the column which is used for point sizes.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Sets the index of the column which will be used for point sizes.
	 * @param column Index of the column which will be used for point sizes.
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public Shape getPointShape(PointData data) {
		Shape shape = getShape();

		Row row = data.row;
		int colSize = getColumn();
		if (colSize >= row.size() || colSize < 0 || !row.isColumnNumeric(colSize)) {
			return shape;
		}

		Number value = (Number) row.get(colSize);
		double size = DataUtils.getValueOrDefault(value, Double.NaN);
		if (!MathUtils.isCalculatable(size) || size <= 0.0) {
			return null;
		}

		if (size != 1.0) {
			AffineTransform tx = AffineTransform.getScaleInstance(size, size);
			shape = tx.createTransformedShape(shape);
		}
		return shape;
	}
}
