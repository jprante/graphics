package org.xbib.graphics.chart.legend;

import org.xbib.graphics.chart.theme.Theme;
import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.Styler;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractLegend<ST extends Styler, S extends Series> implements Legend<ST, S> {

    protected static final int BOX_SIZE = 20;
    protected static final int BOX_OUTLINE_WIDTH = 5;

    private static final int LEGEND_MARGIN = 6;
    private static final int MULTI_LINE_SPACE = 3;

    protected Chart<ST, S> chart;
    protected Rectangle2D bounds;
    protected double xOffset = 0;
    protected double yOffset = 0;

    protected AbstractLegend(Chart<ST, S> chart) {
        this.chart = chart;
    }

    public abstract double getSeriesLegendRenderGraphicHeight(S series);

    protected abstract void doPaint(Graphics2D g);

    @Override
    public Rectangle2D getBounds() {
        if (chart.getStyler().getLegendLayout() == LegendLayout.Vertical) {
            return getBoundsHintVertical();
        } else {
            return getBoundsHintHorizontal();
        }
    }

    @Override
    public void paint(Graphics2D g) {
        if (!chart.getStyler().isLegendVisible()) {
            return;
        }
        if (chart.getSeriesMap().isEmpty()) {
            return;
        }
        if (chart.getPlot().getBounds().getWidth() < 30) {
            return;
        }
        if (chart.getStyler().getLegendLayout() == LegendLayout.Vertical) {
            bounds = getBoundsHintVertical();
        } else {
            bounds = getBoundsHintHorizontal();
        }
        double height = bounds.getHeight();
        switch (chart.getStyler().getLegendPosition()) {
            case OutsideE:
                xOffset = chart.getWidth() - bounds.getWidth() - chart.getStyler().getChartPadding();
                yOffset = chart.getPlot().getBounds().getY() + (chart.getPlot().getBounds().getHeight() - bounds.getHeight()) / 2.0;
                break;
            case InsideNW:
                xOffset = chart.getPlot().getBounds().getX() + LEGEND_MARGIN;
                yOffset = chart.getPlot().getBounds().getY() + LEGEND_MARGIN;
                break;
            case InsideNE:
                xOffset = chart.getPlot().getBounds().getX() + chart.getPlot().getBounds().getWidth() - bounds.getWidth() - LEGEND_MARGIN;
                yOffset = chart.getPlot().getBounds().getY() + LEGEND_MARGIN;
                break;
            case InsideSE:
                xOffset = chart.getPlot().getBounds().getX() + chart.getPlot().getBounds().getWidth() - bounds.getWidth() - LEGEND_MARGIN;
                yOffset = chart.getPlot().getBounds().getY() + chart.getPlot().getBounds().getHeight() - bounds.getHeight() - LEGEND_MARGIN;
                break;
            case InsideSW:
                xOffset = chart.getPlot().getBounds().getX() + LEGEND_MARGIN;
                yOffset = chart.getPlot().getBounds().getY() + chart.getPlot().getBounds().getHeight() - bounds.getHeight() - LEGEND_MARGIN;
                break;
            case InsideN:
                xOffset = chart.getPlot().getBounds().getX() + (chart.getPlot().getBounds().getWidth() - bounds.getWidth()) / 2 + LEGEND_MARGIN;
                yOffset = chart.getPlot().getBounds().getY() + LEGEND_MARGIN;
                break;
            case OutsideS:
                xOffset = chart.getPlot().getBounds().getX() + (chart.getPlot().getBounds().getWidth() - bounds.getWidth()) / 2.0;
                yOffset = chart.getHeight() - bounds.getHeight() - LEGEND_MARGIN;
                break;
            default:
                break;
        }
        Shape rect = new Rectangle2D.Double(xOffset, yOffset, bounds.getWidth(), height);
        g.setColor(chart.getStyler().getLegendBackgroundColor());
        g.fill(rect);
        g.setStroke(Theme.Strokes.LEGEND);
        g.setColor(chart.getStyler().getLegendBorderColor());
        g.draw(rect);
        doPaint(g);
    }

    protected Map<String, Rectangle2D> getSeriesTextBounds(S series) {
        String[] lines = series.getName().split("\\n");
        Map<String, Rectangle2D> seriesTextBounds = new LinkedHashMap<>(lines.length);
        for (String line : lines) {
            TextLayout textLayout = new TextLayout(line, chart.getStyler().getLegendFont(), new FontRenderContext(null, true, false));
            Shape shape = textLayout.getOutline(null);
            Rectangle2D bounds = shape.getBounds2D();
            seriesTextBounds.put(line, bounds);
        }
        return seriesTextBounds;
    }

    protected float getLegendEntryHeight(Map<String, Rectangle2D> seriesTextBounds, int markerSize) {
        float legendEntryHeight = 0;
        for (Map.Entry<String, Rectangle2D> entry : seriesTextBounds.entrySet()) {
            legendEntryHeight += entry.getValue().getHeight() + MULTI_LINE_SPACE;
        }
        legendEntryHeight -= MULTI_LINE_SPACE;
        legendEntryHeight = Math.max(legendEntryHeight, markerSize);
        return legendEntryHeight;
    }

    protected float getLegendEntryWidth(Map<String, Rectangle2D> seriesTextBounds, int markerSize) {
        float legendEntryWidth = 0;
        for (Map.Entry<String, Rectangle2D> entry : seriesTextBounds.entrySet()) {
            legendEntryWidth = Math.max(legendEntryWidth, (float) entry.getValue().getWidth());
        }
        return legendEntryWidth + markerSize + chart.getStyler().getLegendPadding();
    }

    protected void paintSeriesText(Graphics2D g, Map<String, Rectangle2D> seriesTextBounds, int markerSize, double x, double starty) {
        g.setColor(chart.getStyler().getChartFontColor());
        g.setFont(chart.getStyler().getLegendFont());
        double multiLineOffset = 0.0;
        for (Map.Entry<String, Rectangle2D> entry : seriesTextBounds.entrySet()) {
            double height = entry.getValue().getHeight();
            double centerOffsetY = (Math.max(markerSize, height) - height) / 2.0;
            FontRenderContext frc = g.getFontRenderContext();
            TextLayout tl = new TextLayout(entry.getKey(), chart.getStyler().getLegendFont(), frc);
            Shape shape = tl.getOutline(null);
            AffineTransform orig = g.getTransform();
            AffineTransform at = new AffineTransform();
            at.translate(x, starty + height + centerOffsetY + multiLineOffset);
            g.transform(at);
            g.fill(shape);
            g.setTransform(orig);
            multiLineOffset += height + MULTI_LINE_SPACE;
        }
    }

    /**
     * Determine the width and height of the chart legend.
     */
    private Rectangle2D getBoundsHintVertical() {
        if (!chart.getStyler().isLegendVisible()) {
            return new Rectangle2D.Double();
        }
        boolean containsBox = false;
        double legendTextContentMaxWidth = 0;
        double legendContentHeight = 0;
        Map<String, S> map = chart.getSeriesMap();
        for (S series : map.values()) {
            if (series.isNotShownInLegend()) {
                continue;
            }
            if (!series.isEnabled()) {
                continue;
            }
            Map<String, Rectangle2D> seriesTextBounds = getSeriesTextBounds(series);
            double legendEntryHeight = 0;
            for (Map.Entry<String, Rectangle2D> entry : seriesTextBounds.entrySet()) {
                legendEntryHeight += entry.getValue().getHeight() + MULTI_LINE_SPACE;
                legendTextContentMaxWidth = Math.max(legendTextContentMaxWidth, entry.getValue().getWidth());
            }
            legendEntryHeight -= MULTI_LINE_SPACE;
            legendEntryHeight = Math.max(legendEntryHeight, (getSeriesLegendRenderGraphicHeight(series)));
            legendContentHeight += legendEntryHeight + chart.getStyler().getLegendPadding();
            if (series.getLegendRenderType() == LegendRenderType.Box) {
                containsBox = true;
            }
        }
        double legendContentWidth;
        if (!containsBox) {
            legendContentWidth = chart.getStyler().getLegendSeriesLineLength() + chart.getStyler().getLegendPadding() + legendTextContentMaxWidth;
        } else {
            legendContentWidth = BOX_SIZE + chart.getStyler().getLegendPadding() + legendTextContentMaxWidth;
        }
        double width = legendContentWidth + 2 * chart.getStyler().getLegendPadding();
        double height = legendContentHeight + chart.getStyler().getLegendPadding();
        return new Rectangle2D.Double(Double.NaN, Double.NaN, width, height);
    }

    private Rectangle2D getBoundsHintHorizontal() {
        if (!chart.getStyler().isLegendVisible()) {
            return new Rectangle2D.Double();
        }
        double legendTextContentMaxHeight = 0;
        double legendContentWidth = 0;
        Map<String, S> map = chart.getSeriesMap();
        for (S series : map.values()) {
            if (series.isNotShownInLegend()) {
                continue;
            }
            if (!series.isEnabled()) {
                continue;
            }
            Map<String, Rectangle2D> seriesTextBounds = getSeriesTextBounds(series);
            double legendEntryHeight = 0;
            double legendEntryMaxWidth = 0;
            for (Map.Entry<String, Rectangle2D> entry : seriesTextBounds.entrySet()) {
                legendEntryHeight += entry.getValue().getHeight() + MULTI_LINE_SPACE;
                legendEntryMaxWidth = Math.max(legendEntryMaxWidth, entry.getValue().getWidth());
            }
            legendEntryHeight -= MULTI_LINE_SPACE;
            legendTextContentMaxHeight = Math.max(legendEntryHeight, getSeriesLegendRenderGraphicHeight(series));
            legendContentWidth += legendEntryMaxWidth + chart.getStyler().getLegendPadding();
            if (series.getLegendRenderType() == LegendRenderType.Line) {
                legendContentWidth = chart.getStyler().getLegendSeriesLineLength()
                        + chart.getStyler().getLegendPadding()
                        + legendContentWidth;
            } else {
                legendContentWidth = BOX_SIZE + chart.getStyler().getLegendPadding() + legendContentWidth;
            }
        }
        double width = legendContentWidth + chart.getStyler().getLegendPadding();
        double height = legendTextContentMaxHeight + chart.getStyler().getLegendPadding() * 2;
        return new Rectangle2D.Double(0, 0, width, height);
    }

}
