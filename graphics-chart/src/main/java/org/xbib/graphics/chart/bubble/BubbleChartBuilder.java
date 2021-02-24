package org.xbib.graphics.chart.bubble;

import org.xbib.graphics.chart.ChartBuilder;

public class BubbleChartBuilder extends ChartBuilder<BubbleChartBuilder, BubbleChart> {

    String xAxisTitle = "";
    String yAxisTitle = "";

    public BubbleChartBuilder() {
    }

    public BubbleChartBuilder xAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
        return this;
    }

    public BubbleChartBuilder yAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
        return this;
    }

    @Override
    public BubbleChart build() {
        return new BubbleChart(this);
    }
}
