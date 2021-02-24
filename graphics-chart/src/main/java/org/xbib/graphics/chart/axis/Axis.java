package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.category.CategoryStyler;
import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.AxesChartSeries;
import org.xbib.graphics.chart.series.AxesChartSeriesCategory;
import org.xbib.graphics.chart.style.AxesChartStyler;
import org.xbib.graphics.chart.legend.LegendPosition;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

public class Axis<ST extends AxesChartStyler, S extends AxesChartSeries> implements ChartComponent {

    private final Chart<ST, S> chart;
    private final Rectangle2D.Double bounds;
    private final ST axesChartStyler;
    private final AxisTitle<ST, S> axisTitle;
    private final AxisTick<ST, S> axisTick;
    private final Direction direction;
    private final int yIndex;
    private DataType dataType;
    private AxisTickCalculator axisTickCalculator;
    private double min;
    private double max;

    public Axis(Chart<ST, S> chart, Direction direction, int yIndex) {
        this.chart = chart;
        this.axesChartStyler = chart.getStyler();
        this.direction = direction;
        this.yIndex = yIndex;
        bounds = new Rectangle2D.Double();
        axisTitle = new AxisTitle<>(chart, direction, direction == Direction.Y ? this : null, yIndex);
        axisTick = new AxisTick<>(chart, direction, direction == Direction.Y ? this : null);
    }

    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }

    public void resetMinMax() {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
    }

    public void addMinMax(double min, double max) {
        if (Double.isNaN(this.min) || min < this.min) {
            this.min = min;
        }
        if (max > this.max) {
            this.max = max;
        }
    }

    public void preparePaint() {
        if (direction == Direction.Y) {
            double xOffset = 0;
            double yOffset = chart.getChartTitle().getBounds().getHeight() + axesChartStyler.getChartPadding();
            int i = 1;
            double width = 60;
            double height;
            do {
                double approximateXAxisWidth = chart.getWidth() - width
                                - (axesChartStyler.getLegendPosition() == LegendPosition.OutsideE
                                ? chart.getLegend().getBounds().getWidth()
                                : 0)
                                - 2 * axesChartStyler.getChartPadding()
                                - (axesChartStyler.isYAxisTicksVisible() ? (axesChartStyler.getPlotMargin()) : 0)
                                - (axesChartStyler.getLegendPosition() == LegendPosition.OutsideE
                                && axesChartStyler.isLegendVisible()
                                ? axesChartStyler.getChartPadding()
                                : 0);

                height = chart.getHeight() - yOffset
                                - chart.getXAxis().getXAxisHeightHint(approximateXAxisWidth)
                                - axesChartStyler.getPlotMargin()
                                - axesChartStyler.getChartPadding()
                                - (axesChartStyler.getLegendPosition() == LegendPosition.OutsideS
                                ? chart.getLegend().getBounds().getHeight()
                                : 0);

                width = getYAxisWidthHint(height);
            } while (i-- > 0);
            bounds.setRect(xOffset, yOffset, width, height);
        } else {
            Rectangle2D leftYAxisBounds = chart.getAxisPair().getLeftYAxisBounds();
            Rectangle2D rightYAxisBounds = chart.getAxisPair().getRightYAxisBounds();
            double maxYAxisY = Math.max(leftYAxisBounds.getY() + leftYAxisBounds.getHeight(),
                            rightYAxisBounds.getY() + rightYAxisBounds.getHeight());
            double xOffset = leftYAxisBounds.getWidth() + axesChartStyler.getChartPadding();
            double yOffset = maxYAxisY + axesChartStyler.getPlotMargin()
                            - (axesChartStyler.getLegendPosition() == LegendPosition.OutsideS
                            ? chart.getLegend().getBounds().getHeight()
                            : 0);
            double legendWidth = 0;
            if (axesChartStyler.getLegendPosition() == LegendPosition.OutsideE
                    && axesChartStyler.isLegendVisible()) {
                legendWidth = chart.getLegend().getBounds().getWidth() + axesChartStyler.getChartPadding();
            }
            double width = chart.getWidth() - leftYAxisBounds.getWidth()
                            - rightYAxisBounds.getWidth()
                            - 2 * axesChartStyler.getChartPadding()
                            - legendWidth;
            double height = chart.getHeight() - maxYAxisY
                            - axesChartStyler.getChartPadding()
                            - axesChartStyler.getPlotMargin();
            bounds.setRect(xOffset, yOffset, width, height);
        }
    }

    @Override
    public void paint(Graphics2D g) {
        Object oldHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (direction == Direction.Y) {
            boolean onRight = axesChartStyler.getYAxisGroupPosistion(yIndex) == YAxisPosition.Right;
            if (onRight) {
                axisTick.paint(g);
                axisTitle.paint(g);
            } else {
                axisTitle.paint(g);
                axisTick.paint(g);
            }
            bounds.width = (axesChartStyler.isYAxisTitleVisible() ? axisTitle.getBounds().getWidth() : 0)
                    + axisTick.getBounds().getWidth();

        } else {
            this.axisTickCalculator = getAxisTickCalculator(bounds.getWidth());
            axisTitle.paint(g);
            axisTick.paint(g);
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
    }

    private double getXAxisHeightHint(double workingSpace) {
        double titleHeight = 0.0;
        if (chart.getXAxisTitle() != null &&
                !chart.getXAxisTitle().trim().equalsIgnoreCase("") &&
                axesChartStyler.isXAxisTitleVisible()) {
            TextLayout textLayout = new TextLayout(chart.getXAxisTitle(),
                    axesChartStyler.getAxisTitleFont(), new FontRenderContext(null, true, false));
            Rectangle2D rectangle = textLayout.getBounds();
            titleHeight = rectangle.getHeight() + chart.getStyler().getAxisTitlePadding();
        }
        this.axisTickCalculator = getAxisTickCalculator(workingSpace);
        double axisTickLabelsHeight = 0.0;
        if (chart.getStyler().isXAxisTicksVisible()) {
            String sampleLabel = "";
            for (int i = 0; i < axisTickCalculator.getTickLabels().size(); i++) {
                if (axisTickCalculator.getTickLabels().get(i) != null &&
                        axisTickCalculator.getTickLabels().get(i).length() > sampleLabel.length()) {
                    sampleLabel = axisTickCalculator.getTickLabels().get(i);
                }
            }
            TextLayout textLayout = new TextLayout(sampleLabel.length() == 0 ? " " : sampleLabel,
                    axesChartStyler.getAxisTickLabelsFont(),
                    new FontRenderContext(null, true, false));
            AffineTransform rot = axesChartStyler.getXAxisLabelRotation() == 0 ? null :
                    AffineTransform.getRotateInstance(-1 * Math.toRadians(axesChartStyler.getXAxisLabelRotation()));
            Shape shape = textLayout.getOutline(rot);
            Rectangle2D rectangle = shape.getBounds();
            axisTickLabelsHeight = rectangle.getHeight() + axesChartStyler.getAxisTickPadding() + axesChartStyler.getAxisTickMarkLength();
        }
        return titleHeight + axisTickLabelsHeight;
    }

    private double getYAxisWidthHint(double workingSpace) {
        double titleHeight = 0.0;
        if (chart.getyYAxisTitle() != null &&
                !chart.getyYAxisTitle().trim().equalsIgnoreCase("") &&
                axesChartStyler.isYAxisTitleVisible()) {
            TextLayout textLayout = new TextLayout(chart.getyYAxisTitle(),
                    axesChartStyler.getAxisTitleFont(), new FontRenderContext(null, true, false));
            Rectangle2D rectangle = textLayout.getBounds();
            titleHeight = rectangle.getHeight() + axesChartStyler.getAxisTitlePadding();
        }
        double axisTickLabelsHeight = 0.0;
        if (axesChartStyler.isYAxisTicksVisible()) {
            this.axisTickCalculator = getAxisTickCalculator(workingSpace);
            String sampleLabel = "";
            for (int i = 0; i < axisTickCalculator.getTickLabels().size(); i++) {
                if (axisTickCalculator.getTickLabels().get(i) != null
                        && axisTickCalculator.getTickLabels().get(i).length() > sampleLabel.length()) {
                    sampleLabel = axisTickCalculator.getTickLabels().get(i);
                }
            }
            TextLayout textLayout = new TextLayout(sampleLabel.length() == 0 ? " " : sampleLabel,
                    axesChartStyler.getAxisTickLabelsFont(), new FontRenderContext(null, true, false));
            Rectangle2D rectangle = textLayout.getBounds();
            axisTickLabelsHeight = rectangle.getWidth() + axesChartStyler.getAxisTickPadding() + axesChartStyler.getAxisTickMarkLength();
        }
        return titleHeight + axisTickLabelsHeight;
    }

    private AxisTickCalculator getAxisTickCalculator(double workingSpace) {
        Map<Double, Object> labelOverrideMap = chart.getYAxisLabelOverrideMap(getDirection(), yIndex);
        if (labelOverrideMap != null) {
            if (getDirection() == Direction.X && axesChartStyler instanceof CategoryStyler) {
                AxesChartSeriesCategory axesChartSeries =
                        (AxesChartSeriesCategory) chart.getSeriesMap().values().iterator().next();
                List<?> categories = (List<?>) axesChartSeries.getXData();
                DataType axisType = chart.getAxisPair().getXAxis().getDataType();
                return new AxisTickCalculatorOverride(getDirection(),
                        workingSpace,
                        axesChartStyler,
                        labelOverrideMap,
                        axisType,
                        categories.size());
            }
            return new AxisTickCalculatorOverride(getDirection(), workingSpace, min, max, axesChartStyler, labelOverrideMap);
        }
        if (getDirection() == Direction.X) {
            if (axesChartStyler instanceof CategoryStyler) {
                AxesChartSeriesCategory axesChartSeries =
                        (AxesChartSeriesCategory) chart.getSeriesMap().values().iterator().next();
                List<?> categories = (List<?>) axesChartSeries.getXData();
                DataType axisType = chart.getAxisPair().getXAxis().getDataType();
                return new AxisTickCalculatorCategory(
                        getDirection(), workingSpace, categories, axisType, axesChartStyler);
            } else if (getDataType() == DataType.Instant) {
                return new AxisTickCalculatorInstant(getDirection(), workingSpace, min, max, axesChartStyler);
            } else if (axesChartStyler.isXAxisLogarithmic()) {
                return new AxisTickCalculatorLogarithmic(
                        getDirection(), workingSpace, min, max, axesChartStyler);
            } else {
                return new AxisTickCalculatorNumber(getDirection(), workingSpace, min, max, axesChartStyler);
            }
        } else {
            if (axesChartStyler.isYAxisLogarithmic() && getDataType() != DataType.Instant) {
                return new AxisTickCalculatorLogarithmic(getDirection(), workingSpace, min, max, axesChartStyler);
            } else {
                return new AxisTickCalculatorNumber(getDirection(), workingSpace, min, max, axesChartStyler);
            }
        }
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {

        if (dataType != null && this.dataType != null && this.dataType != dataType) {
            throw new IllegalArgumentException("Different Axes (e.g. Date, Number, String) cannot be mixed on the same chart!!");
        }
        this.dataType = dataType;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public AxisTick<ST, S> getAxisTick() {
        return axisTick;
    }

    public Direction getDirection() {
        return direction;
    }

    public AxisTitle<ST, S> getAxisTitle() {
        return axisTitle;
    }

    public AxisTickCalculator getAxisTickCalculator() {
        return this.axisTickCalculator;
    }

    public int getYIndex() {
        return yIndex;
    }
}
