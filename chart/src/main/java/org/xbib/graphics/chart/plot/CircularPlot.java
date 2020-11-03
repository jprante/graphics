package org.xbib.graphics.chart.plot;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.Styler;
import org.xbib.graphics.chart.legend.LegendPosition;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public abstract class CircularPlot<ST extends Styler, S extends Series> extends Plot<ST, S> {

    protected Rectangle2D bounds;

    public CircularPlot(Chart<ST, S> chart) {
        super(chart);
        initContentAndSurface(chart);
    }

    protected abstract void initContentAndSurface(Chart<ST, S> chart);

    @Override
    public void paint(Graphics2D g) {
        double xOffset = chart.getStyler().getChartPadding();
        double yOffset = chart.getChartTitle().getBounds().getHeight() + chart.getStyler().getChartPadding();
        double width = chart.getWidth()
                        - (chart.getStyler().getLegendPosition() == LegendPosition.OutsideE
                        ? chart.getLegend().getBounds().getWidth()
                        : 0)
                        - 2 * chart.getStyler().getChartPadding()
                        - (chart.getStyler().getLegendPosition() == LegendPosition.OutsideE
                        && chart.getStyler().isLegendVisible()
                        ? chart.getStyler().getChartPadding()
                        : 0);
        double height = chart.getHeight()
                        - chart.getChartTitle().getBounds().getHeight()
                        - (chart.getStyler().getLegendPosition() == LegendPosition.OutsideS
                        ? chart.getLegend().getBounds().getHeight()
                        : 0)
                        - 2 * chart.getStyler().getChartPadding();
        this.bounds = new Rectangle2D.Double(xOffset, yOffset, width, height);
    }

    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }
}
