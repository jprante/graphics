package org.xbib.graphics.chart.plot;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class AxesChartPlot<ST extends AxesChartStyler, S extends Series> extends Plot<ST, S> {

    protected ContentPlot<ST, S> contentPlot;

    protected SurfacePlot<ST, S> surfacePlot;

    protected Rectangle2D bounds;

    public AxesChartPlot(Chart<ST, S> chart) {
        super(chart);
        this.surfacePlot = new SurfacePlotAxesChart<>(chart);
    }

    @Override
    public void paint(Graphics2D g) {
        Rectangle2D yAxisBounds = chart.getAxisPair().getLeftYAxisBounds();
        Rectangle2D xAxisBounds = chart.getXAxis().getBounds();
        double xOffset = xAxisBounds.getX();
        double yOffset = yAxisBounds.getY();
        double width = xAxisBounds.getWidth();
        double height = yAxisBounds.getHeight();
        this.bounds = new Rectangle2D.Double(xOffset, yOffset, width, height);
        surfacePlot.paint(g);
        if (chart.getSeriesMap().isEmpty()) {
            return;
        }
        contentPlot.paint(g);
    }

    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }
}
