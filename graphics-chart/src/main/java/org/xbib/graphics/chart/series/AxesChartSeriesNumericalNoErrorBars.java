package org.xbib.graphics.chart.series;

import org.xbib.graphics.chart.axis.DataType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * A Series containing X and Y data to be plotted on a Chart with X and Y Axes.
 */
public abstract class AxesChartSeriesNumericalNoErrorBars extends MarkerSeries {

    protected List<?> xData; // can be Number or Instant
    protected List<? extends Number> yData;
    protected List<? extends Number> extraValues;

    public AxesChartSeriesNumericalNoErrorBars(String name,
                                               List<?> xData,
                                               List<? extends Number> yData,
                                               List<? extends Number> extraValues,
                                               DataType xDataType) {
        super(name, xDataType);
        this.xData = xData;
        this.yData = yData;
        this.extraValues = extraValues;
        calculateMinMax();
    }

    @Override
    protected void calculateMinMax() {
        // xData
        List<Double> xMinMax = findMinMax(xData);
        setXMin(xMinMax.get(0));
        setXMax(xMinMax.get(1));
        // yData
        List<Double> yMinMax;
        if (extraValues == null) {
            yMinMax = findMinMax(yData);
        } else {
            yMinMax = findMinMaxWithErrorBars(yData, extraValues);
        }
        setYMin(yMinMax.get(0));
        setYMax(yMinMax.get(1));
    }

    List<Double> findMinMax(List<?> data) {
        Double min = null;
        Double max = null;
        for (Object dataPoint : data) {
            if (dataPoint instanceof Double) {
                Double d = (Double) dataPoint;
                if (min == null) {
                    min = d;
                }
                if (max == null) {
                    max = d;
                }
                if (!Double.isNaN(d)) {
                    if (d < min) {
                        min = d;
                    }
                    if (d > max) {
                        max = d;
                    }
                }
            } else if (dataPoint instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) dataPoint;
                if (min == null) {
                    min = bd.doubleValue();
                }
                if (max == null) {
                    max = bd.doubleValue();
                }
                if (bd.doubleValue() < min) {
                    min = bd.doubleValue();
                }
                if (bd.doubleValue() > max) {
                    max = bd.doubleValue();
                }
            } else if (dataPoint instanceof Integer) {
                int i = (Integer) dataPoint;
                if (min == null) {
                    min = (double) i;
                }
                if (max == null) {
                    max = (double) i;
                }
                if (i < min) {
                    min = (double) i;
                }
                if (i > max) {
                    max = (double) i;
                }
            } else if (dataPoint instanceof Instant) {
                Instant instant = (Instant) dataPoint;
                if (min == null) {
                    min = (double) instant.toEpochMilli();
                }
                if (max == null) {
                    max = (double) instant.toEpochMilli();
                }
                if (instant.toEpochMilli() < min) {
                    min = (double) instant.toEpochMilli();
                }
                if (instant.toEpochMilli() > max) {
                    max = (double) instant.toEpochMilli();
                }
            }
        }
        return Arrays.asList(min, max);
    }

    private List<Double> findMinMaxWithErrorBars(List<? extends Number> data,
                                             List<? extends Number> errorBars) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < data.size(); i++) {
            Number n = data.get(i);
            Double d = n instanceof Double ? (Double) n : null;
            Number nn = errorBars.get(i);
            Double eb = nn instanceof Double ? (Double) nn : null;
            if (d != null && eb != null) {
                if (d - eb < min) {
                    min = d - eb;
                }
                if (d + eb > max) {
                    max = d + eb;
                }
            }
        }
        return Arrays.asList(min, max);
    }

    public List<?> getXData() {
        return xData;
    }

    public List<? extends Number> getYData() {
        return yData;
    }

    public List<? extends Number> getExtraValues() {
        return extraValues;
    }
}
