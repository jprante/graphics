package org.xbib.graphics.chart.pie;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.legend.AbstractLegend;
import org.xbib.graphics.chart.legend.LegendLayout;
import org.xbib.graphics.chart.legend.LegendRenderType;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class PieLegend<ST extends PieStyler, S extends PieSeries> extends AbstractLegend<ST, S> {

    public PieLegend(Chart<ST, S> chart) {
        super(chart);
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
            float legendEntryHeight = getLegendEntryHeight(seriesTextBounds, BOX_SIZE);
            Shape rectSmall = new Rectangle2D.Double(startx, starty, BOX_SIZE, BOX_SIZE);
            g.setColor(series.getFillColor());
            g.fill(rectSmall);
            final double x = startx + BOX_SIZE + chart.getStyler().getLegendPadding();
            paintSeriesText(g, seriesTextBounds, BOX_SIZE, x, starty);
            if (chart.getStyler().getLegendLayout() == LegendLayout.Vertical) {
                starty += legendEntryHeight + chart.getStyler().getLegendPadding();
            } else {
                int markerWidth = BOX_SIZE;
                if (series.getLegendRenderType() == LegendRenderType.Line) {
                    markerWidth = chart.getStyler().getLegendSeriesLineLength();
                }
                float legendEntryWidth = getLegendEntryWidth(seriesTextBounds, markerWidth);
                startx += legendEntryWidth + chart.getStyler().getLegendPadding();
            }
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
    }

    @Override
    public double getSeriesLegendRenderGraphicHeight(S series) {
        return BOX_SIZE;
    }
}
