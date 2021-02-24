package org.xbib.graphics.chart.series;

import org.xbib.graphics.chart.axis.DataType;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * A Series containing X and Y data to be plotted on a Chart with X and Y Axes.
 */
public abstract class AxesChartSeries extends Series {

    private final DataType xAxisType;
    private final DataType yAxisType;
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private BasicStroke stroke;
    private Color lineColor;
    private float lineWidth = -1.0f;

    public AxesChartSeries(String name, DataType xAxisType) {
        super(name);
        this.xAxisType = xAxisType;
        this.yAxisType = DataType.Number;
    }

    protected abstract void calculateMinMax();

    public void setLineColor(Color color) {
        this.lineColor = color;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public DataType getxAxisDataType() {
        return xAxisType;
    }

    public DataType getyAxisDataType() {
        return yAxisType;
    }

    public void setXMin(double xMin) {
        this.xMin = xMin;
    }

    public double getXMin() {
        return xMin;
    }

    public void setXMax(double xMax) {
        this.xMax = xMax;
    }

    public double getXMax() {
        return xMax;
    }

    public void setYMin(double yMin) {
        this.yMin = yMin;
    }

    public double getYMin() {
        return yMin;
    }

    public void setYMax(double yMax) {
        this.yMax = yMax;
    }

    public double getYMax() {
        return yMax;
    }

    public void setLineStyle(BasicStroke basicStroke) {
        stroke = basicStroke;
        if (this.lineWidth > 0.0f) {
            stroke = new BasicStroke(lineWidth,
                            this.stroke.getEndCap(),
                            this.stroke.getLineJoin(),
                            this.stroke.getMiterLimit(),
                            this.stroke.getDashArray(),
                            this.stroke.getDashPhase());
        }
    }

    public BasicStroke getLineStyle() {
        return stroke;
    }

    public AxesChartSeries setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    public float getLineWidth() {
        return lineWidth;
    }

}
