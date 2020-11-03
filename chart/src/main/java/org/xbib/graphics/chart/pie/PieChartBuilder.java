package org.xbib.graphics.chart.pie;

import org.xbib.graphics.chart.ChartBuilder;

public class PieChartBuilder extends ChartBuilder<PieChartBuilder, PieChart> {

    public PieChartBuilder() {
    }

    @Override
    public PieChart build() {

        return new PieChart(this);
    }
}
