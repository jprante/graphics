package org.xbib.graphics.chart.plot;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.Styler;

import java.awt.geom.Rectangle2D;

public abstract class Plot<ST extends Styler, S extends Series> implements ChartComponent {

    protected final Chart<ST, S> chart;

    public Plot(Chart<ST, S> chart) {
        this.chart = chart;
    }

    @Override
    public Rectangle2D getBounds() {
        return chart.getPlot().getBounds();
    }
}
