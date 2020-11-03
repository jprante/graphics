package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Axis title.
 */
public class AxisTitle<ST extends AxesChartStyler, S extends Series> implements ChartComponent {

    private final Chart<ST, S> chart;
    private final Direction direction;
    private final Axis<ST, ?> yAxis;
    private final int yIndex;
    private Rectangle2D bounds;

    public AxisTitle(Chart<ST, S> chart, Direction direction, Axis<ST, ?> yAxis, int yIndex) {
        this.chart = chart;
        this.direction = direction;
        this.yAxis = yAxis;
        this.yIndex = yIndex;
    }

    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }

    @Override
    public void paint(Graphics2D g) {
        bounds = new Rectangle2D.Double();
        g.setColor(chart.getStyler().getChartFontColor());
        g.setFont(chart.getStyler().getAxisTitleFont());
        if (direction == Direction.Y) {
            String yAxisTitle = chart.getYAxisGroupTitle(yIndex);
            if (yAxisTitle != null && !yAxisTitle.trim().equalsIgnoreCase("") &&
                    chart.getStyler().isYAxisTitleVisible()) {
                FontRenderContext frc = g.getFontRenderContext();
                TextLayout nonRotatedTextLayout = new TextLayout(chart.getyYAxisTitle(), chart.getStyler().getAxisTitleFont(), frc);
                Rectangle2D nonRotatedRectangle = nonRotatedTextLayout.getBounds();
                boolean onRight = chart.getStyler().getYAxisGroupPosistion(yAxis.getYIndex()) == YAxisPosition.Right;
                int xOffset;
                if (onRight) {
                    xOffset = (int) (yAxis.getAxisTick().getBounds().getX()
                                            + yAxis.getAxisTick().getBounds().getWidth()
                                            + nonRotatedRectangle.getHeight());
                } else {
                    xOffset = (int) (yAxis.getBounds().getX() + nonRotatedRectangle.getHeight());
                }
                int yOffset = (int) ((yAxis.getBounds().getHeight() + nonRotatedRectangle.getWidth()) / 2.0
                                        + yAxis.getBounds().getY());
                AffineTransform rot = AffineTransform.getRotateInstance(-1 * Math.PI / 2, 0, 0);
                Shape shape = nonRotatedTextLayout.getOutline(rot);
                AffineTransform orig = g.getTransform();
                AffineTransform at = new AffineTransform();
                at.translate(xOffset, yOffset);
                g.transform(at);
                g.fill(shape);
                g.setTransform(orig);
                bounds = new Rectangle2D.Double(xOffset - nonRotatedRectangle.getHeight(),
                        yOffset - nonRotatedRectangle.getWidth(),
                        nonRotatedRectangle.getHeight() + chart.getStyler().getAxisTitlePadding(),
                        nonRotatedRectangle.getWidth());
            } else {
                bounds = new Rectangle2D.Double(yAxis.getBounds().getX(), yAxis.getBounds().getY(),0, yAxis.getBounds().getHeight());
            }
        } else {

            if (chart.getXAxisTitle() != null &&
                    !chart.getXAxisTitle().trim().equalsIgnoreCase("") &&
                    chart.getStyler().isXAxisTitleVisible()) {

                FontRenderContext frc = g.getFontRenderContext();
                TextLayout textLayout = new TextLayout(chart.getXAxisTitle(), chart.getStyler().getAxisTitleFont(), frc);
                Rectangle2D rectangle = textLayout.getBounds();

                double xOffset = chart.getXAxis().getBounds().getX()
                                + (chart.getXAxis().getBounds().getWidth() - rectangle.getWidth()) / 2.0;
                double yOffset = chart.getXAxis().getBounds().getY()
                                + chart.getXAxis().getBounds().getHeight()
                                - rectangle.getHeight();

                Shape shape = textLayout.getOutline(null);
                AffineTransform orig = g.getTransform();
                AffineTransform at = new AffineTransform();
                at.translate((float) xOffset, (float) (yOffset - rectangle.getY()));
                g.transform(at);
                g.fill(shape);
                g.setTransform(orig);
                bounds = new Rectangle2D.Double(xOffset, yOffset - chart.getStyler().getAxisTitlePadding(),
                        rectangle.getWidth(), rectangle.getHeight() + chart.getStyler().getAxisTitlePadding());
            } else {
                bounds = new Rectangle2D.Double(chart.getXAxis().getBounds().getX(),
                                chart.getXAxis().getBounds().getY() + chart.getXAxis().getBounds().getHeight(),
                                chart.getXAxis().getBounds().getWidth(), 0);
            }
        }
    }
}
