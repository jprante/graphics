package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.AxesChartSeries;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Axis tick marks. This includes the little tick marks and the line that hugs the plot area.
 */
public class AxisTickMarks<ST extends AxesChartStyler, S extends AxesChartSeries> implements ChartComponent {

    private final Chart<ST, S> chart;

    private final Direction direction;

    private final Axis<ST, S> yAxis;

    private Rectangle2D bounds;

    protected AxisTickMarks(Chart<ST, S> chart, Direction direction, Axis<ST, S> yAxis) {
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
        g.setColor(styler.getAxisTickMarksColor());
        g.setStroke(styler.getAxisTickMarksStroke());
        if (direction == Direction.Y && chart.getStyler().isYAxisTicksVisible()) {
            int axisTickMarkLength = styler.getAxisTickMarkLength();
            boolean onRight = styler.getYAxisGroupPosistion(yAxis.getYIndex()) == YAxisPosition.Right;
            Rectangle2D yAxisBounds = yAxis.getBounds();
            Rectangle2D axisTickLabelBounds = yAxis.getAxisTick().getAxisTickLabels().getBounds();
            double xOffset;
            double lineXOffset;
            if (onRight) {
                xOffset = axisTickLabelBounds.getX() - styler.getAxisTickPadding() - axisTickMarkLength;
                lineXOffset = xOffset;
            } else {
                xOffset = axisTickLabelBounds.getX() + axisTickLabelBounds.getWidth() + styler.getAxisTickPadding();
                lineXOffset = xOffset + axisTickMarkLength;
            }
            double yOffset = yAxisBounds.getY();
            bounds = new Rectangle2D.Double(xOffset, yOffset, styler.getAxisTickMarkLength(), yAxis.getBounds().getHeight());
            if (styler.isAxisTicksMarksVisible()) {
                for (int i = 0; i < chart.getYAxis().getAxisTickCalculator().getTickLabels().size(); i++) {
                    double tickLocation = yAxis.getAxisTickCalculator().getTickLocations().get(i);
                    double flippedTickLocation = yOffset + yAxisBounds.getHeight() - tickLocation;
                    if (flippedTickLocation > bounds.getY() &&
                            flippedTickLocation < bounds.getY() + bounds.getHeight()) {
                        Shape line = new Line2D.Double(xOffset, flippedTickLocation, xOffset + axisTickMarkLength, flippedTickLocation);
                        g.draw(line);
                    }
                }
            }
            if (styler.isAxisTicksLineVisible()) {
                Shape line = new Line2D.Double(lineXOffset, yOffset, lineXOffset, yOffset + yAxisBounds.getHeight());
                g.draw(line);
            }
        } else if (direction == Direction.X && styler.isXAxisTicksVisible()) {
            int axisTickMarkLength = styler.getAxisTickMarkLength();
            double xOffset = chart.getXAxis().getBounds().getX();
            double yOffset = chart.getXAxis().getAxisTick().getAxisTickLabels().getBounds().getY() - styler.getAxisTickPadding();
            bounds = new Rectangle2D.Double(xOffset, yOffset - axisTickMarkLength,
                    chart.getXAxis().getBounds().getWidth(), axisTickMarkLength);
            if (styler.isAxisTicksMarksVisible()) {
                for (int i = 0; i < chart.getXAxis().getAxisTickCalculator().getTickLabels().size(); i++) {
                    double tickLocation = chart.getXAxis().getAxisTickCalculator().getTickLocations().get(i);
                    double shiftedTickLocation = xOffset + tickLocation;
                    if (shiftedTickLocation > bounds.getX() &&
                            shiftedTickLocation < bounds.getX() + bounds.getWidth()) {
                        Shape line = new Line2D.Double(shiftedTickLocation, yOffset, xOffset + tickLocation, yOffset - chart.getStyler().getAxisTickMarkLength());
                        g.draw(line);
                    }
                }
            }
            if (styler.isAxisTicksLineVisible()) {
                g.setStroke(styler.getAxisTickMarksStroke());
                g.drawLine((int) xOffset,
                        (int) (yOffset - axisTickMarkLength),
                        (int) (xOffset + chart.getXAxis().getBounds().getWidth()),
                        (int) (yOffset - axisTickMarkLength));
            } else {
                bounds = new Rectangle2D.Double();
            }
        }
    }
}
