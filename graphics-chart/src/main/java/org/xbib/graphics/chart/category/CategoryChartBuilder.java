package org.xbib.graphics.chart.category;

import org.xbib.graphics.chart.ChartBuilder;

public class CategoryChartBuilder extends ChartBuilder<CategoryChartBuilder, CategoryChart> {

    private String xAxisTitle = "";
    private String yAxisTitle = "";

    public CategoryChartBuilder() {
    }

    public CategoryChartBuilder xAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
        return this;
    }

    public CategoryChartBuilder yAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
        return this;
    }

    public String getxAxisTitle() {
        return xAxisTitle;
    }

    public String getyAxisTitle() {
        return yAxisTitle;
    }

    /**
     * return fully built Chart_Category
     *
     * @return a Chart_Category
     */
    @Override
    public CategoryChart build() {
        return new CategoryChart(this);
    }
}
