package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.AxesChartSeries;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Axis tick labels.
 */
public class AxisTickLabels<ST extends AxesChartStyler, S extends AxesChartSeries> implements ChartComponent {

    private final Chart<ST, S> chart;

    private final Direction direction;

    private final Axis<ST, S> yAxis;

    private Rectangle2D bounds;

    protected AxisTickLabels(Chart<ST, S> chart, Direction direction, Axis<ST, S> yAxis) {
        this.chart = chart;
        this.direction = direction;
        this.yAxis = yAxis;
    }

    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }

    @Override
    public void paint(Graphics2D g) {
        ST styler = chart.getStyler();
        g.setFont(styler.getAxisTickLabelsFont());
        g.setColor(styler.getAxisTickLabelsColor());
        if (direction == Direction.Y && chart.getStyler().isYAxisTicksVisible()) {
            boolean onRight = styler.getYAxisGroupPosistion(yAxis.getYIndex()) == YAxisPosition.Right;
            double xOffset;
            if (onRight) {
                xOffset = yAxis.getBounds().getX() + (styler.isYAxisTicksVisible() ?
                        styler.getAxisTickMarkLength() + styler.getAxisTickPadding() : 0);
            } else {
                double xWidth = yAxis.getAxisTitle().getBounds().getWidth();
                xOffset = yAxis.getAxisTitle().getBounds().getX() + xWidth;
            }
            double yOffset = yAxis.getBounds().getY();
            double height = yAxis.getBounds().getHeight();
            double maxTickLabelWidth = 0;
            Map<Double, TextLayout> axisLabelTextLayouts = new HashMap<>();
            for (int i = 0; i < chart.getYAxis().getAxisTickCalculator().getTickLabels().size(); i++) {
                String tickLabel = chart.getYAxis().getAxisTickCalculator().getTickLabels().get(i);
                double tickLocation = chart.getYAxis().getAxisTickCalculator().getTickLocations().get(i);
                double flippedTickLocation = yOffset + height - tickLocation;
                if (tickLabel != null && !tickLabel.isEmpty() &&
                        flippedTickLocation > yOffset &&
                        flippedTickLocation < yOffset + height) {
                    FontRenderContext frc = g.getFontRenderContext();
                    TextLayout axisLabelTextLayout = new TextLayout(tickLabel, styler.getAxisTickLabelsFont(), frc);
                    Rectangle2D tickLabelBounds = axisLabelTextLayout.getBounds();
                    double boundWidth = tickLabelBounds.getWidth();
                    if (boundWidth > maxTickLabelWidth) {
                        maxTickLabelWidth = boundWidth;
                    }
                    axisLabelTextLayouts.put(tickLocation, axisLabelTextLayout);
                }
            }
            for (Map.Entry<Double,TextLayout> entry : axisLabelTextLayouts.entrySet()) {
                Double tickLocation = entry.getKey();
                TextLayout axisLabelTextLayout = axisLabelTextLayouts.get(tickLocation);
                Shape shape = axisLabelTextLayout.getOutline(null);
                Rectangle2D tickLabelBounds = shape.getBounds();
                double flippedTickLocation = yOffset + height - tickLocation;
                AffineTransform orig = g.getTransform();
                AffineTransform at = new AffineTransform();
                double boundWidth = tickLabelBounds.getWidth();
                double xPos;
                switch (chart.getStyler().getYAxisLabelAlignment()) {
                    case Right:
                        xPos = xOffset + maxTickLabelWidth - boundWidth;
                        break;
                    case Centre:
                        xPos = xOffset + (maxTickLabelWidth - boundWidth) / 2;
                        break;
                    case Left:
                    default:
                        xPos = xOffset;
                }
                at.translate(xPos, flippedTickLocation + tickLabelBounds.getHeight() / 2.0);
                g.transform(at);
                g.fill(shape);
                g.setTransform(orig);
            }
            bounds = new Rectangle2D.Double(xOffset, yOffset, maxTickLabelWidth, height);
        } else if (direction == Direction.X && chart.getStyler().isXAxisTicksVisible()) {
            double xOffset = chart.getXAxis().getBounds().getX();
            double yOffset = chart.getXAxis().getAxisTitle().getBounds().getY();
            double width = chart.getXAxis().getBounds().getWidth();
            double maxTickLabelHeight = 0;
            int maxTickLabelY = 0;
            for (int i = 0; i < chart.getXAxis().getAxisTickCalculator().getTickLabels().size(); i++) {
                String tickLabel = chart.getXAxis().getAxisTickCalculator().getTickLabels().get(i);
                double tickLocation = chart.getXAxis().getAxisTickCalculator().getTickLocations().get(i);
                double shiftedTickLocation = xOffset + tickLocation;
                if (tickLabel != null && !tickLabel.isEmpty() &&
                        shiftedTickLocation > xOffset &&
                        shiftedTickLocation < xOffset + width) {
                    FontRenderContext frc = g.getFontRenderContext();
                    TextLayout textLayout = new TextLayout(tickLabel, chart.getStyler().getAxisTickLabelsFont(), frc);
                    AffineTransform rot = AffineTransform.getRotateInstance(-1 * Math.toRadians(chart.getStyler().getXAxisLabelRotation()), 0, 0);
                    Shape shape = textLayout.getOutline(rot);
                    Rectangle2D tickLabelBounds = shape.getBounds2D();
                    if (tickLabelBounds.getBounds().height > maxTickLabelY) {
                        maxTickLabelY = tickLabelBounds.getBounds().height;
                    }
                }
            }
            for (int i = 0; i < chart.getXAxis().getAxisTickCalculator().getTickLabels().size(); i++) {
                String tickLabel = chart.getXAxis().getAxisTickCalculator().getTickLabels().get(i);
                double tickLocation = chart.getXAxis().getAxisTickCalculator().getTickLocations().get(i);
                double shiftedTickLocation = xOffset + tickLocation;
                if (tickLabel != null && shiftedTickLocation > xOffset && shiftedTickLocation < xOffset + width) {
                    FontRenderContext frc = g.getFontRenderContext();
                    TextLayout textLayout = new TextLayout(tickLabel, styler.getAxisTickLabelsFont(), frc);
                    AffineTransform rot = AffineTransform.getRotateInstance(
                                    -1 * Math.toRadians(styler.getXAxisLabelRotation()), 0, 0);
                    Shape shape = textLayout.getOutline(rot);
                    Rectangle2D tickLabelBounds = shape.getBounds2D();
                    AffineTransform orig = g.getTransform();
                    AffineTransform at = new AffineTransform();
                    double xPos;
                    switch (styler.getXAxisLabelAlignment()) {
                        case Left:
                            xPos = shiftedTickLocation;
                            break;
                        case Right:
                            xPos = shiftedTickLocation - tickLabelBounds.getWidth();
                            break;
                        case Centre:
                        default:
                            xPos = shiftedTickLocation - tickLabelBounds.getWidth() / 2.0;
                    }
                    double shiftX = -1 * tickLabelBounds.getX() * Math.sin(Math.toRadians(chart.getStyler().getXAxisLabelRotation()));
                    double shiftY = -1 * (tickLabelBounds.getY() + tickLabelBounds.getHeight());
                    at.translate(xPos + shiftX, yOffset + shiftY);
                    g.transform(at);
                    g.fill(shape);
                    g.setTransform(orig);
                    if (tickLabelBounds.getHeight() > maxTickLabelHeight) {
                        maxTickLabelHeight = tickLabelBounds.getHeight();
                    }
                }
            }
            bounds = new Rectangle2D.Double(xOffset, yOffset - maxTickLabelHeight, width, maxTickLabelHeight);
        } else {
            bounds = new Rectangle2D.Double();
        }
    }
}
