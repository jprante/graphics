package org.xbib.graphics.chart.plot;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Draws the plot background, the plot border and the horizontal and vertical grid lines.
 */
public class SurfacePlotAxesChart<ST extends AxesChartStyler, S extends Series> extends SurfacePlot<ST, S> {

    private final ST axesChartStyler;

    protected SurfacePlotAxesChart(Chart<ST, S> chart) {
        super(chart);
        this.axesChartStyler = chart.getStyler();
    }

    @Override
    public void paint(Graphics2D g) {
        Rectangle2D bounds = getBounds();
        Shape rect = new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        g.setColor(axesChartStyler.getPlotBackgroundColor());
        g.fill(rect);
        if (axesChartStyler.isPlotGridHorizontalLinesVisible()) {
            List<Double> yAxisTickLocations = chart.getYAxis().getAxisTickCalculator().getTickLocations();
            for (Double yAxisTickLocation : yAxisTickLocations) {
                double yOffset = bounds.getY() + bounds.getHeight() - yAxisTickLocation;
                if (yOffset > bounds.getY() && yOffset < bounds.getY() + bounds.getHeight()) {
                    g.setColor(axesChartStyler.getPlotGridLinesColor());
                    g.setStroke(axesChartStyler.getPlotGridLinesStroke());
                    Shape line = axesChartStyler.getPlotGridLinesStroke().createStrokedShape(new Line2D.Double(
                                                    bounds.getX(), yOffset, bounds.getX() + bounds.getWidth(), yOffset));
                    g.draw(line);
                }
            }
        }
        if (axesChartStyler.isPlotTicksMarksVisible()) {
            List<Double> yAxisTickLocations =
                    chart.getAxisPair().getLeftMainYAxis().getAxisTickCalculator().getTickLocations();
            for (Double yAxisTickLocation : yAxisTickLocations) {
                double yOffset = bounds.getY() + bounds.getHeight() - yAxisTickLocation;
                if (yOffset > bounds.getY() && yOffset < bounds.getY() + bounds.getHeight()) {
                    g.setColor(axesChartStyler.getAxisTickMarksColor());
                    g.setStroke(axesChartStyler.getAxisTickMarksStroke());
                    Shape line = new Line2D.Double(bounds.getX(), yOffset,
                                    bounds.getX() + axesChartStyler.getAxisTickMarkLength(),
                                    yOffset);
                    g.draw(line);
                }
            }
            yAxisTickLocations = chart.getAxisPair().getRightMainYAxis().getAxisTickCalculator().getTickLocations();
            for (Double yAxisTickLocation : yAxisTickLocations) {
                double yOffset = bounds.getY() + bounds.getHeight() - yAxisTickLocation;
                if (yOffset > bounds.getY() && yOffset < bounds.getY() + bounds.getHeight()) {
                    g.setColor(axesChartStyler.getAxisTickMarksColor());
                    g.setStroke(axesChartStyler.getAxisTickMarksStroke());
                    Shape line = new Line2D.Double(bounds.getX() + bounds.getWidth(), yOffset,
                                    bounds.getX() + bounds.getWidth() - axesChartStyler.getAxisTickMarkLength(),
                                    yOffset);
                    g.draw(line);
                }
            }
        }
        if (axesChartStyler.isPlotGridVerticalLinesVisible() || axesChartStyler.isPlotTicksMarksVisible()) {
            List<Double> xAxisTickLocations = chart.getXAxis().getAxisTickCalculator().getTickLocations();
            for (Double xAxisTickLocation : xAxisTickLocations) {
                double tickLocation = xAxisTickLocation;
                double xOffset = bounds.getX() + tickLocation;
                if (xOffset > bounds.getX() && xOffset < bounds.getX() + bounds.getWidth()) {
                    if (axesChartStyler.isPlotGridVerticalLinesVisible()) {
                        g.setColor(axesChartStyler.getPlotGridLinesColor());
                        g.setStroke(axesChartStyler.getPlotGridLinesStroke());
                        Shape line = axesChartStyler.getPlotGridLinesStroke().createStrokedShape(new Line2D.Double(
                                                        xOffset, bounds.getY(), xOffset, bounds.getY() + bounds.getHeight()));
                        g.draw(line);
                    }
                    if (axesChartStyler.isPlotTicksMarksVisible()) {
                        g.setColor(axesChartStyler.getAxisTickMarksColor());
                        g.setStroke(axesChartStyler.getAxisTickMarksStroke());
                        Shape line = new Line2D.Double(xOffset, bounds.getY(), xOffset,
                                        bounds.getY() + axesChartStyler.getAxisTickMarkLength());
                        g.draw(line);
                        line = new Line2D.Double(xOffset, bounds.getY() + bounds.getHeight(), xOffset,
                                        bounds.getY() + bounds.getHeight() - axesChartStyler.getAxisTickMarkLength());
                        g.draw(line);
                    }
                }
            }
        }
        if (axesChartStyler.isPlotBorderVisible()) {
            g.setColor(axesChartStyler.getPlotBorderColor());
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[] {3.0f, 0.0f}, 0.0f));
            g.draw(rect);
        }
    }
}
