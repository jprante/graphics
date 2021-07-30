package org.xbib.graphics.graph.gral.plots.legends;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.xbib.graphics.graph.gral.data.DataSource;
import org.xbib.graphics.graph.gral.graphics.Drawable;

/**
 * A legend implementation that displays an item for each data series that are
 * added to the legend.
 */
public abstract class SeriesLegend extends AbstractLegend {

	/** Mapping of data rows to drawable components. */
	private final Map<DataSource, Drawable> drawableByDataSource;

	public SeriesLegend() {
		drawableByDataSource = new HashMap<>();
	}

	@Override
	public void add(DataSource source) {
		super.add(source);
		String label = getLabel(source);
		Font font = getFont();
		Item item = new Item(getSymbol(source), label, font);
		add(item);
		drawableByDataSource.put(source, item);
	}

	@Override
	public void remove(DataSource source) {
		super.remove(source);
		Drawable drawable = drawableByDataSource.remove(source);
		if (drawable != null) {
			remove(drawable);
		}
	}

	/**
	 * Returns the label text for the specified data source.
	 * @param data Data source.
	 * @return Label text.
	 */
	protected String getLabel(DataSource data) {
		return data.getName();
	}

	/**
	 * Returns a symbol for rendering a legend item.
	 * @param data Data source.
	 * @return A drawable object that can be used to display the symbol.
	 */
	protected abstract Drawable getSymbol(DataSource data);
}
