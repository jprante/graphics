package org.xbib.graphics.chart.series;

import org.xbib.graphics.chart.axis.DataType;

import java.util.List;

/**
 * A Series containing X and Y data to be plotted on a Chart with X and Y Axes, values associated
 * with each X-Y point, could be used for bubble sizes for example, but no error bars, as the min
 * and max are calculated differently. No markers.
 */
public abstract class NoMarkersSeries extends AxesChartSeriesNumericalNoErrorBars {

    protected NoMarkersSeries(String name, List<?> xData,
                              List<? extends Number> yData,
                              List<? extends Number> extraValues, DataType axisType) {
        super(name, xData, yData, extraValues, axisType);
        this.extraValues = extraValues;
        calculateMinMax();
    }

    @Override
    protected void calculateMinMax() {
        List<Double> xMinMax = findMinMax(xData);
        setXMin(xMinMax.get(0));
        setXMax(xMinMax.get(1));
        List<Double> yMinMax = findMinMax(yData);
        setYMin(yMinMax.get(0));
        setYMax(yMinMax.get(1));
    }
}
