package org.xbib.graphics.chart.category;

import org.xbib.graphics.chart.axis.DataType;
import org.xbib.graphics.chart.series.AxesChartSeriesCategory;
import org.xbib.graphics.chart.legend.LegendRenderType;

import java.util.List;

/**
 * A Series containing category data to be plotted on a Chart.
 */
public class CategorySeries extends AxesChartSeriesCategory {

    private CategorySeriesRenderStyle categorySeriesRenderStyle = null;

    public CategorySeries(String name, List<?> xData,
                          List<? extends Number> yData,
                          List<? extends Number> errorBars,
                          DataType axisType) {
        super(name, xData, yData, errorBars, axisType);
    }

    public CategorySeriesRenderStyle getCategorySeriesRenderStyle() {
        return categorySeriesRenderStyle;
    }

    public void setCategorySeriesRenderStyle(CategorySeriesRenderStyle chartXYSeriesRenderStyle) {
        this.categorySeriesRenderStyle = chartXYSeriesRenderStyle;
    }

    @Override
    public LegendRenderType getLegendRenderType() {
        return categorySeriesRenderStyle.getLegendRenderType();
    }

}
