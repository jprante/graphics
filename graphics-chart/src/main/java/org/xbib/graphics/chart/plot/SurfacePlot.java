package org.xbib.graphics.chart.plot;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.Styler;

public abstract class SurfacePlot<ST extends Styler, S extends Series> extends Plot<ST, S> implements ChartComponent {

    protected SurfacePlot(Chart<ST, S> chart) {
        super(chart);
    }
}
