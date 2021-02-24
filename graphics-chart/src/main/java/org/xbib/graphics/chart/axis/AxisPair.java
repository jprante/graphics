package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.category.CategorySeriesRenderStyle;
import org.xbib.graphics.chart.category.CategoryStyler;
import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.AxesChartSeries;
import org.xbib.graphics.chart.series.AxesChartSeriesCategory;
import org.xbib.graphics.chart.style.AxesChartStyler;
import org.xbib.graphics.chart.legend.LegendPosition;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AxisPair<ST extends AxesChartStyler, S extends AxesChartSeries> implements ChartComponent {

    private final Chart<ST, S> chart;

    private final Axis<ST, S> xAxis;

    private final Axis<ST, S> yAxis;

    private final TreeMap<Integer, Axis<ST, S>> yAxisMap;

    private final Rectangle2D.Double leftYAxisBounds;

    private final Rectangle2D.Double rightYAxisBounds;

    private Axis<ST, S> leftMainYAxis;

    private Axis<ST, S> rightMainYAxis;

    private final Map<String, Map<Double, Object>> axisLabelOverrideMap;

    public AxisPair(Chart<ST, S> chart) {
        this.chart = chart;
        this.xAxis = new Axis<>(chart, Direction.X, 0);
        this.yAxis = new Axis<>(chart, Direction.Y, 0);
        this.yAxisMap = new TreeMap<>();
        yAxisMap.put(0, yAxis);
        leftYAxisBounds = new Rectangle2D.Double();
        rightYAxisBounds = new Rectangle2D.Double();
        axisLabelOverrideMap = new HashMap<>();
    }

    @Override
    public Rectangle2D getBounds() {
        return null; // should never be called
    }

    @Override
    public void paint(Graphics2D g) {
        prepareForPaint();
        leftMainYAxis = null;
        rightMainYAxis = null;
        ST styler = chart.getStyler();
        final int chartPadding = styler.getChartPadding();
        int tickMargin = (styler.isYAxisTicksVisible() ? (styler.getPlotMargin()) : 0);
        leftYAxisBounds.width = 0;
        int leftCount = 0;
        double leftStart = chartPadding;
        for (Map.Entry<Integer, Axis<ST, S>> e : yAxisMap.entrySet()) {
            Axis<ST, S> ya = e.getValue();
            if (styler.getYAxisGroupPosistion(e.getKey()) == YAxisPosition.Right) {
                continue;
            }
            if (e.getKey() == 0) {
                continue;
            }
            ya.preparePaint();
            Rectangle2D.Double bounds = (Rectangle2D.Double) ya.getBounds();
            bounds.x = leftStart;
            ya.paint(g);
            double width = bounds.getWidth();
            leftStart += chartPadding + width + tickMargin;
            leftYAxisBounds.width += width;
            leftCount++;
            leftMainYAxis = ya;
        }
        if (styler.getYAxisGroupPosistion(0) != YAxisPosition.Right) {
            yAxis.preparePaint();
            Rectangle2D.Double bounds = (Rectangle2D.Double) yAxis.getBounds();
            bounds.x = leftStart;
            yAxis.paint(g);
            double width = bounds.getWidth();
            //leftStart += chartPadding + width + tickMargin;
            leftYAxisBounds.width += width;
            leftCount++;
            leftMainYAxis = yAxis;
        }

        if (leftCount > 1) {
            leftYAxisBounds.width += (leftCount - 1) * chartPadding;
        }
        leftYAxisBounds.width += leftCount * tickMargin;
        rightYAxisBounds.width = 0;
        double legendWidth = 0;
        if (styler.getLegendPosition() == LegendPosition.OutsideE && styler.isLegendVisible()) {
            legendWidth = chart.getLegend().getBounds().getWidth() + styler.getChartPadding();
        }
        double rightEnd = chart.getWidth() - legendWidth - chartPadding;
        rightYAxisBounds.x = rightEnd;
        int rightCount = 0;
        for (Map.Entry<Integer, Axis<ST, S>> e : yAxisMap.descendingMap().entrySet()) {
            Axis<ST, S> ya = e.getValue();
            if (styler.getYAxisGroupPosistion(e.getKey()) != YAxisPosition.Right) {
                continue;
            }
            if (e.getKey() == 0) {
                continue;
            }
            ya.preparePaint();
            Rectangle2D.Double bounds = (Rectangle2D.Double) ya.getBounds();
            double aproxWidth = bounds.getWidth();
            double xOffset = rightEnd - aproxWidth;
            bounds.x = xOffset;
            rightYAxisBounds.x = xOffset;
            ya.paint(g);
            rightYAxisBounds.width += aproxWidth;
            rightEnd -= chartPadding + aproxWidth + tickMargin;
            rightCount++;
            rightMainYAxis = ya;
        }
        if (styler.getYAxisGroupPosistion(0) == YAxisPosition.Right) {
            yAxis.preparePaint();
            Rectangle2D.Double bounds = (Rectangle2D.Double) yAxis.getBounds();
            double aproxWidth = bounds.getWidth();
            double xOffset = rightEnd - aproxWidth;
            bounds.x = xOffset;
            rightYAxisBounds.x = xOffset;
            yAxis.paint(g);
            rightYAxisBounds.width += aproxWidth;
            //rightEnd -= chartPadding + aproxWidth + tickMargin;
            rightCount++;
            rightMainYAxis = yAxis;
        }
        if (leftMainYAxis == null) {
            leftMainYAxis = yAxis;
        }
        if (rightMainYAxis == null) {
            rightMainYAxis = yAxis;
        }

        if (rightCount > 1) {
            rightYAxisBounds.width += (rightCount - 1) * chartPadding;
        }
        rightYAxisBounds.width += rightCount * tickMargin;
        Rectangle2D.Double bounds = (java.awt.geom.Rectangle2D.Double) yAxis.getBounds();
        leftYAxisBounds.x = chartPadding;
        leftYAxisBounds.y = bounds.y;
        leftYAxisBounds.height = bounds.height;
        rightYAxisBounds.y = bounds.y;
        rightYAxisBounds.height = bounds.height;
        xAxis.preparePaint();
        xAxis.paint(g);
    }

    public Axis<ST, S> getYAxis(int yIndex) {
        return yAxisMap.get(yIndex);
    }

    public Axis<ST, S> getXAxis() {
        return xAxis;
    }

    public Axis<ST, S> getYAxis() {
        return yAxis;
    }

    public Rectangle2D.Double getLeftYAxisBounds() {
        return leftYAxisBounds;
    }

    public Rectangle2D.Double getRightYAxisBounds() {
        return rightYAxisBounds;
    }

    public Axis<ST, S> getLeftMainYAxis() {
        return leftMainYAxis;
    }

    public Axis<ST, S> getRightMainYAxis() {
        return rightMainYAxis;
    }

    public Map<String, Map<Double, Object>> getAxisLabelOverrideMap() {
        return axisLabelOverrideMap;
    }

    private void prepareForPaint() {
        boolean mainYAxisUsed = false;
        if (chart.getSeriesMap() != null) {
            for (S series : chart.getSeriesMap().values()) {
                int yIndex = series.getYAxisGroup();
                if (!mainYAxisUsed && yIndex == 0) {
                    mainYAxisUsed = true;
                }
                if (yAxisMap.containsKey(yIndex)) {
                    continue;
                }
                yAxisMap.put(yIndex, new Axis<>(chart, Direction.Y, yIndex));
            }
        }
        xAxis.setDataType(null);
        yAxis.setDataType(null);
        for (S series : chart.getSeriesMap().values()) {
            xAxis.setDataType(series.getxAxisDataType());
            yAxis.setDataType(series.getyAxisDataType());
            getYAxis(series.getYAxisGroup()).setDataType(series.getyAxisDataType());
            if (!mainYAxisUsed) {
                yAxis.setDataType(series.getyAxisDataType());
            }
        }
        xAxis.resetMinMax();
        for (Axis<ST, S> ya : yAxisMap.values()) {
            ya.resetMinMax();
        }
        if (chart.getSeriesMap() == null || chart.getSeriesMap().size() < 1) {
            xAxis.addMinMax(-1, 1);
            for (Axis<ST, S> ya : yAxisMap.values()) {
                ya.addMinMax(-1, 1);
            }
        } else {
            int disabledCount = 0;
            for (S series : chart.getSeriesMap().values()) {
                if (!series.isEnabled()) {
                    disabledCount++;
                    continue;
                }
                xAxis.addMinMax(series.getXMin(), series.getXMax());
                getYAxis(series.getYAxisGroup()).addMinMax(series.getYMin(), series.getYMax());
                if (!mainYAxisUsed) {
                    yAxis.addMinMax(series.getYMin(), series.getYMax());
                }
            }
            if (disabledCount == chart.getSeriesMap().values().size()) {
                xAxis.addMinMax(-1, 1);
                for (Axis<ST, S> ya : yAxisMap.values()) {
                    ya.addMinMax(-1, 1);
                }
            }
        }
        overrideMinMaxForXAxis();
        for (Axis<ST, S> ya : yAxisMap.values()) {
            overrideMinMaxForYAxis(ya);
        }
        if (chart.getStyler().isXAxisLogarithmic() && xAxis.getMin() <= 0.0) {
            throw new IllegalArgumentException("Series data (accounting for error bars too) cannot be less or equal to zero for a logarithmic X-Axis");
        }
        if (chart.getStyler().isYAxisLogarithmic() && yAxis.getMin() <= 0.0) {
            for (Axis<ST, S> ya : yAxisMap.values()) {
                if (ya.getMin() <= 0.0) {
                    throw new IllegalArgumentException("Series data (accounting for error bars too) cannot be less or equal to zero for a logarithmic Y-Axis");
                }
            }
        }
        if (xAxis.getMin() == Double.POSITIVE_INFINITY || xAxis.getMax() == Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException(
                    "Series data (accounting for error bars too) cannot be equal to Double.POSITIVE_INFINITY!!!");
        }
        for (Axis<ST, S> ya : yAxisMap.values()) {
            if (ya.getMin() == Double.POSITIVE_INFINITY || ya.getMax() == Double.POSITIVE_INFINITY) {
                throw new IllegalArgumentException(
                        "Series data (accounting for error bars too) cannot be equal to Double.POSITIVE_INFINITY!!!");
            }
            if (ya.getMin() == Double.NEGATIVE_INFINITY || ya.getMax() == Double.NEGATIVE_INFINITY) {
                throw new IllegalArgumentException(
                        "Series data (accounting for error bars too) cannot be equal to Double.NEGATIVE_INFINITY!!!");
            }
        }

        if (xAxis.getMin() == Double.NEGATIVE_INFINITY || xAxis.getMax() == Double.NEGATIVE_INFINITY) {
            throw new IllegalArgumentException(
                    "Series data (accounting for error bars too) cannot be equal to Double.NEGATIVE_INFINITY!!!");
        }
    }

    private void overrideMinMaxForXAxis() {
        double overrideXAxisMinValue = xAxis.getMin();
        double overrideXAxisMaxValue = xAxis.getMax();
        if (chart.getStyler().getXAxisMin() != null) {
            overrideXAxisMinValue = chart.getStyler().getXAxisMin();
        }
        if (chart.getStyler().getXAxisMax() != null) {
            overrideXAxisMaxValue = chart.getStyler().getXAxisMax();
        }
        xAxis.setMin(overrideXAxisMinValue);
        xAxis.setMax(overrideXAxisMaxValue);
    }

    private void overrideMinMaxForYAxis(Axis<?, ?> yAxis) {
        double overrideYAxisMinValue = yAxis.getMin();
        double overrideYAxisMaxValue = yAxis.getMax();
        if (chart.getStyler() instanceof CategoryStyler) {
            CategoryStyler categoryStyler = (CategoryStyler) chart.getStyler();
            if (categoryStyler.getDefaultSeriesRenderStyle() == CategorySeriesRenderStyle.Bar) {
                if (categoryStyler.isStacked()) {
                    AxesChartSeriesCategory axesChartSeries =
                            (AxesChartSeriesCategory) chart.getSeriesMap().values().iterator().next();
                    List<?> categories = (List<?>) axesChartSeries.getXData();
                    int numCategories = categories.size();
                    double[] accumulatedStackOffsetPos = new double[numCategories];
                    double[] accumulatedStackOffsetNeg = new double[numCategories];
                    for (S series : chart.getSeriesMap().values()) {
                        AxesChartSeriesCategory axesChartSeriesCategory = (AxesChartSeriesCategory) series;
                        if (!series.isEnabled()) {
                            continue;
                        }
                        int categoryCounter = 0;
                        for (Number next : axesChartSeriesCategory.getYData()) {
                            if (next == null) {
                                categoryCounter++;
                                continue;
                            }
                            if (next.doubleValue() > 0) {
                                accumulatedStackOffsetPos[categoryCounter] += next.doubleValue();
                            } else if (next.doubleValue() < 0) {
                                accumulatedStackOffsetNeg[categoryCounter] += next.doubleValue();
                            }
                            categoryCounter++;
                        }
                    }
                    double max = accumulatedStackOffsetPos[0];
                    for (int i = 1; i < accumulatedStackOffsetPos.length; i++) {
                        if (accumulatedStackOffsetPos[i] > max) {
                            max = accumulatedStackOffsetPos[i];
                        }
                    }
                    double min = accumulatedStackOffsetNeg[0];
                    for (int i = 1; i < accumulatedStackOffsetNeg.length; i++) {
                        if (accumulatedStackOffsetNeg[i] < min) {
                            min = accumulatedStackOffsetNeg[i];
                        }
                    }
                    overrideYAxisMaxValue = max;
                    overrideYAxisMinValue = min;
                }
                if (yAxis.getMin() > 0.0) {
                    overrideYAxisMinValue = 0.0;
                }
                if (yAxis.getMax() < 0.0) {
                    overrideYAxisMaxValue = 0.0;
                }
            }
        }
        if (chart.getStyler().getYAxisMin(yAxis.getYIndex()) != null) {
            overrideYAxisMinValue = chart.getStyler().getYAxisMin(yAxis.getYIndex());
        } else if (chart.getStyler().getYAxisMin() != null) {
            overrideYAxisMinValue = chart.getStyler().getYAxisMin();
        }
        if (chart.getStyler().getYAxisMax(yAxis.getYIndex()) != null) {
            overrideYAxisMaxValue = chart.getStyler().getYAxisMax(yAxis.getYIndex());
        } else if (chart.getStyler().getYAxisMax() != null) {
            overrideYAxisMaxValue = chart.getStyler().getYAxisMax();
        }
        yAxis.setMin(overrideYAxisMinValue);
        yAxis.setMax(overrideYAxisMaxValue);
    }
}
