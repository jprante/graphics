package org.xbib.graphics.chart;

import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.Styler;
import org.xbib.graphics.chart.theme.Theme;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Chart Title.
 */
public class ChartTitle<ST extends Styler, S extends Series> implements ChartComponent {

    private final Chart<ST, S> chart;
    private Rectangle2D bounds;

    public ChartTitle(Chart<ST, S> chart) {
        this.chart = chart;
    }

    @Override
    public Rectangle2D getBounds() {
        if (bounds == null) {
            bounds = getBoundsHint();
        }
        return bounds;
    }

    @Override
    public void paint(Graphics2D g) {
        g.setFont(chart.getStyler().getChartTitleFont());
        if (!chart.getStyler().isChartTitleVisible() || chart.getTitle().length() == 0) {
            return;
        }
        Object oldHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout textLayout = new TextLayout(chart.getTitle(), chart.getStyler().getChartTitleFont(), frc);
        Rectangle2D textBounds = textLayout.getBounds();
        double xOffset = chart.getPlot().getBounds().getX();
        double yOffset = chart.getStyler().getChartPadding();
        if (chart.getStyler().isChartTitleBoxVisible()) {
            double chartTitleBoxWidth = chart.getPlot().getBounds().getWidth();
            double chartTitleBoxHeight = textBounds.getHeight() + 2 * chart.getStyler().getChartTitlePadding();
            g.setStroke(Theme.Strokes.TITLE);
            Shape rect = new Rectangle2D.Double(xOffset, yOffset, chartTitleBoxWidth, chartTitleBoxHeight);
            g.setColor(chart.getStyler().getChartTitleBoxBackgroundColor());
            g.fill(rect);
            g.setColor(chart.getStyler().getChartTitleBoxBorderColor());
            g.draw(rect);
        }
        xOffset = chart.getPlot().getBounds().getX() + (chart.getPlot().getBounds().getWidth() - textBounds.getWidth()) / 2.0;
        yOffset = chart.getStyler().getChartPadding() + textBounds.getHeight() + chart.getStyler().getChartTitlePadding();
        g.setColor(chart.getStyler().getChartFontColor());
        Shape shape = textLayout.getOutline(null);
        AffineTransform orig = g.getTransform();
        AffineTransform at = new AffineTransform();
        at.translate(xOffset, yOffset);
        g.transform(at);
        g.fill(shape);
        g.setTransform(orig);
        double width = 2 * chart.getStyler().getChartTitlePadding() + textBounds.getWidth();
        double height = 2 * chart.getStyler().getChartTitlePadding() + textBounds.getHeight();
        bounds = new Rectangle2D.Double(xOffset - chart.getStyler().getChartTitlePadding(),
                yOffset - textBounds.getHeight() - chart.getStyler().getChartTitlePadding(), width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
    }

    private Rectangle2D getBoundsHint() {
        if (chart.getStyler().isChartTitleVisible() && chart.getTitle().length() > 0) {
            TextLayout textLayout = new TextLayout(chart.getTitle(), chart.getStyler().getChartTitleFont(),
                    new FontRenderContext(null, true, false));
            Rectangle2D rectangle = textLayout.getBounds();
            double width = 2 * chart.getStyler().getChartTitlePadding() + rectangle.getWidth();
            double height = 2 * chart.getStyler().getChartTitlePadding() + rectangle.getHeight();
            return new Rectangle2D.Double(Double.NaN, Double.NaN, width, height);
        } else {
            return new Rectangle2D.Double();
        }
    }
}