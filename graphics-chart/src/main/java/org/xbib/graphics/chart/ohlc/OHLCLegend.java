package org.xbib.graphics.chart.ohlc;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.legend.AbstractLegend;
import org.xbib.graphics.chart.theme.Theme;
import org.xbib.graphics.chart.legend.LegendLayout;
import org.xbib.graphics.chart.legend.LegendRenderType;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class OHLCLegend<ST extends OHLCStyler, S extends OHLCSeries> extends AbstractLegend<ST, S> {

    private final ST axesChartStyler;

    public OHLCLegend(Chart<ST, S> chart) {
        super(chart);
        axesChartStyler = chart.getStyler();
    }

    @Override
    public void doPaint(Graphics2D g) {
        double startx = xOffset + chart.getStyler().getLegendPadding();
        double starty = yOffset + chart.getStyler().getLegendPadding();
        Object oldHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Map<String, S> map = chart.getSeriesMap();
        for (S series : map.values()) {
            if (series.isNotShownInLegend()) {
                continue;
            }
            if (!series.isEnabled()) {
                continue;
            }
            Map<String, Rectangle2D> seriesTextBounds = getSeriesTextBounds(series);
            float legendEntryHeight = getLegendEntryHeight(seriesTextBounds, axesChartStyler.getMarkerSize());
            if (series.getLegendRenderType() == LegendRenderType.Line
                    && series.getLineStyle() != Theme.Series.NONE_STROKE) {
                g.setColor(series.getLineColor());
                g.setStroke(series.getLineStyle());
                Shape line = new Line2D.Double(startx,
                                starty + legendEntryHeight / 2.0,
                                startx + chart.getStyler().getLegendSeriesLineLength(),
                                starty + legendEntryHeight / 2.0);
                g.draw(line);
            }
            double x = startx + chart.getStyler().getLegendSeriesLineLength()
                            + chart.getStyler().getLegendPadding();
            paintSeriesText(g, seriesTextBounds, axesChartStyler.getMarkerSize(), x, starty);
            if (chart.getStyler().getLegendLayout() == LegendLayout.Vertical) {
                starty += legendEntryHeight + chart.getStyler().getLegendPadding();
            } else {
                int markerWidth = chart.getStyler().getLegendSeriesLineLength();
                float legendEntryWidth = getLegendEntryWidth(seriesTextBounds, markerWidth);
                startx += legendEntryWidth + chart.getStyler().getLegendPadding();
            }
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
    }

    @Override
    public double getSeriesLegendRenderGraphicHeight(S series) {
        return (series.getLegendRenderType() == LegendRenderType.Box
                || series.getLegendRenderType() == LegendRenderType.BoxNoOutline)
                ? BOX_SIZE
                : axesChartStyler.getMarkerSize();
    }
}
