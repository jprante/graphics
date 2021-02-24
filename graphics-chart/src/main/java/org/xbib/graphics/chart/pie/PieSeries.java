package org.xbib.graphics.chart.pie;

import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.legend.LegendRenderType;

/**
 * A Series containing Pie data to be plotted on a Chart.
 */
public class PieSeries extends Series {

    private PieSeriesRenderStyle pieSeriesRenderStyle = null;
    private Number value;


    public PieSeries(String name, Number value) {
        super(name);
        this.value = value;
    }

    public void setPieSeriesRenderStyle(PieSeriesRenderStyle pieSeriesRenderStyle) {
        this.pieSeriesRenderStyle = pieSeriesRenderStyle;
    }

    public PieSeriesRenderStyle getPieSeriesRenderStyle() {
        return pieSeriesRenderStyle;
    }

    @Override
    public LegendRenderType getLegendRenderType() {
        return null;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

}
