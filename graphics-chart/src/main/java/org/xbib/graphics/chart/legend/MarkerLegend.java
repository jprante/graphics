package org.xbib.graphics.chart.legend;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.series.MarkerSeries;
import org.xbib.graphics.chart.style.AxesChartStyler;
import org.xbib.graphics.chart.theme.Theme;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class MarkerLegend<ST extends AxesChartStyler, S extends MarkerSeries> extends AbstractLegend<ST, S> {

    private final ST axesChartStyler;

    public MarkerLegend(Chart<ST, S> chart) {
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
            float legendEntryHeight = getLegendEntryHeight(seriesTextBounds, ((series.getLegendRenderType() == LegendRenderType.Line ||
                                    series.getLegendRenderType() == LegendRenderType.Scatter) ?
                                    axesChartStyler.getMarkerSize() : BOX_SIZE));
            if (series.getLegendRenderType() == LegendRenderType.Line ||
                    series.getLegendRenderType() == LegendRenderType.Scatter) {
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
                if (series.getMarker() != null) {
                    g.setColor(series.getMarkerColor());
                    series.getMarker().paint(g,
                                    startx + chart.getStyler().getLegendSeriesLineLength() / 2.0,
                                    starty + legendEntryHeight / 2.0,
                                    axesChartStyler.getMarkerSize());
                }
            } else {
                Shape rectSmall = new Rectangle2D.Double(startx, starty, BOX_SIZE, BOX_SIZE);
                g.setColor(series.getFillColor());
                g.fill(rectSmall);
                if (series.getLegendRenderType() != LegendRenderType.BoxNoOutline) {
                    g.setColor(series.getLineColor());
                    BasicStroke existingLineStyle = series.getLineStyle();
                    BasicStroke newLineStyle = new BasicStroke(Math.min(existingLineStyle.getLineWidth(), BOX_OUTLINE_WIDTH * 0.5f),
                                    existingLineStyle.getEndCap(),
                                    existingLineStyle.getLineJoin(),
                                    existingLineStyle.getMiterLimit(),
                                    existingLineStyle.getDashArray(),
                                    existingLineStyle.getDashPhase());
                    g.setPaint(series.getLineColor());
                    g.setStroke(newLineStyle);
                    Path2D.Double outlinePath = new Path2D.Double();
                    double lineOffset = existingLineStyle.getLineWidth() * 0.5;
                    outlinePath.moveTo(startx + lineOffset, starty + lineOffset);
                    outlinePath.lineTo(startx + lineOffset, starty + BOX_SIZE - lineOffset);
                    outlinePath.lineTo(startx + BOX_SIZE - lineOffset, starty + BOX_SIZE - lineOffset);
                    outlinePath.lineTo(startx + BOX_SIZE - lineOffset, starty + lineOffset);
                    outlinePath.closePath();
                    g.draw(outlinePath);
                }
            }
            if (series.getLegendRenderType() == LegendRenderType.Line ||
                    series.getLegendRenderType() == LegendRenderType.Scatter) {
                double x = startx + chart.getStyler().getLegendSeriesLineLength()
                                + chart.getStyler().getLegendPadding();
                paintSeriesText(g, seriesTextBounds, axesChartStyler.getMarkerSize(), x, starty);
            } else {
                double x = startx + BOX_SIZE + chart.getStyler().getLegendPadding();
                paintSeriesText(g, seriesTextBounds, BOX_SIZE, x, starty);
            }
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
        return (series.getLegendRenderType() == LegendRenderType.Box ||
                series.getLegendRenderType() == LegendRenderType.BoxNoOutline) ?
                BOX_SIZE : axesChartStyler.getMarkerSize();
    }
}
