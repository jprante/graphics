package org.xbib.graphics.chart.ohlc;

import org.xbib.graphics.chart.ChartBuilder;

public class OHLCChartBuilder extends ChartBuilder<OHLCChartBuilder, OHLCChart> {

    private String xAxisTitle = "";
    private String yAxisTitle = "";

    public OHLCChartBuilder() {
    }

    public OHLCChartBuilder xAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
        return this;
    }

    public String getxAxisTitle() {
        return xAxisTitle;
    }

    public OHLCChartBuilder yAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
        return this;
    }

    public String getyAxisTitle() {
        return yAxisTitle;
    }

    @Override
    public OHLCChart build() {
        return new OHLCChart(this);
    }
}
