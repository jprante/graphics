package org.xbib.graphics.chart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Histogram {

    private final Collection<? extends Number> originalData;

    private final int numBins;

    private final double min;

    private final double max;

    private List<Double> xAxisData;

    private List<Double> yAxisData;

    public Histogram(Collection<? extends Number> data, int numBins) {
        this.numBins = numBins;
        this.originalData = data;
        double tempMax = -Double.MAX_VALUE;
        double tempMin = Double.MAX_VALUE;
        for (Number number : data) {
            double value = number.doubleValue();
            if (value > tempMax) {
                tempMax = value;
            }
            if (value < tempMin) {
                tempMin = value;
            }
        }
        max = tempMax;
        min = tempMin;
        init();
    }

    public Histogram(Collection<? extends Number> data, int numBins, double min, double max) {
        this.numBins = numBins;
        this.originalData = data;
        this.min = min;
        this.max = max;
        init();
    }

    private void init() {
        double[] tempYAxisData = new double[numBins];
        final double binSize = (max - min) / numBins;
        for (Number anOriginalData : originalData) {
            double doubleValue = (anOriginalData).doubleValue();
            int bin = (int) ((doubleValue - min) / binSize);
            if (bin >= 0) {
                if (doubleValue == max) {
                    tempYAxisData[bin - 1] += 1;
                } else if (bin <= numBins && bin != numBins) {
                    tempYAxisData[bin] += 1;
                }
            }
        }
        yAxisData = new ArrayList<>(numBins);
        for (double d : tempYAxisData) {
            yAxisData.add(d);
        }
        xAxisData = new ArrayList<>(numBins);
        for (int i = 0; i < numBins; i++) {
            xAxisData.add(((i * (max - min)) / numBins + min) + binSize / 2);
        }
    }

    public List<Double> getxAxisData() {
        return xAxisData;
    }

    public List<Double> getyAxisData() {
        return yAxisData;
    }

    public Collection<? extends Number> getOriginalData() {
        return originalData;
    }

    public int getNumBins() {
        return numBins;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

}
