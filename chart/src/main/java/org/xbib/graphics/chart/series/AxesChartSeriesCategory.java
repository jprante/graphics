package org.xbib.graphics.chart.series;

import org.xbib.graphics.chart.axis.DataType;

import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A Series containing X and Y data to be plotted on a Chart with X and Y Axes. xData can be Number
 * or Date or String, hence a List.
 */
public abstract class AxesChartSeriesCategory extends MarkerSeries {

    private final List<?> xData;
    private final List<? extends Number> yData;
    private final List<? extends Number> extraValues;

    public AxesChartSeriesCategory(String name,
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
        double[] xMinMax = findMinMax(xData, getxAxisDataType());
        setXMin(xMinMax[0]);
        setXMax(xMinMax[1]);
        double[] yMinMax;
        if (extraValues == null) {
            yMinMax = findMinMax(yData, getyAxisDataType());
        } else {
            yMinMax = findMinMaxWithErrorBars(yData, extraValues);
        }
        setYMin(yMinMax[0]);
        setYMax(yMinMax[1]);
    }

    private double[] findMinMaxWithErrorBars(Collection<? extends Number> data, Collection<? extends Number> errorBars) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        Iterator<? extends Number> itr = data.iterator();
        Iterator<? extends Number> ebItr = errorBars.iterator();
        while (itr.hasNext()) {
            double bigDecimal = itr.next().doubleValue();
            double eb = ebItr.next().doubleValue();
            if (bigDecimal - eb < min) {
                min = bigDecimal - eb;
            }
            if (bigDecimal + eb > max) {
                max = bigDecimal + eb;
            }
        }
        return new double[] {min, max};
    }

    private double[] findMinMax(Collection<?> data, DataType dataType) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (Object dataPoint : data) {
            if (dataPoint == null) {
                continue;
            }
            double value = 0.0;
            if (dataType == DataType.Number) {
                value = ((Number) dataPoint).doubleValue();
            } else if (dataType == DataType.Instant) {
                Instant date = (Instant) dataPoint;
                value = date.toEpochMilli();
            } else if (dataType == DataType.String) {
                return new double[] {Double.NaN, Double.NaN};
            }
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
        return new double[] {min, max};
    }

    public Collection<?> getXData() {
        return xData;
    }

    public Collection<? extends Number> getYData() {
        return yData;
    }

    public Collection<? extends Number> getExtraValues() {
        return extraValues;
    }
}
