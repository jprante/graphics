package org.xbib.graphics.chart.xy;

import org.xbib.graphics.chart.ChartBuilder;

public class XYChartBuilder extends ChartBuilder<XYChartBuilder, XYChart> {

    private String xAxisTitle;
    private String yAxisTitle;

    public XYChartBuilder() {
        this.xAxisTitle = "";
        this.yAxisTitle = "";
    }

    public XYChartBuilder xAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
        return this;
    }

    public XYChartBuilder yAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
        return this;
    }

    public String getxAxisTitle() {
        return xAxisTitle;
    }

    public String getyAxisTitle() {
        return yAxisTitle;
    }

    @Override
    public XYChart build() {
        return new XYChart(this);
    }
}
