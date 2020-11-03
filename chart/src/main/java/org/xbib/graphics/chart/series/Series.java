package org.xbib.graphics.chart.series;

import org.xbib.graphics.chart.legend.LegendRenderType;

import java.awt.Color;

/**
 * A Series containing data to be plotted on a Chart
 */
public abstract class Series {

    private final String name;
    private String label;
    private Color fillColor;
    private boolean showInLegend = true;
    private boolean isEnabled = true;
    private int yAxisGroup = 0;

    public Series(String name) {
        if (name == null || name.length() < 1) {
            throw new IllegalArgumentException("Series name cannot be null or zero-length");
        }
        this.name = name;
        this.label = name;
    }

    public abstract LegendRenderType getLegendRenderType();

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public Series setLabel(String label) {
        this.label = label;
        return this;
    }

    public boolean isNotShownInLegend() {
        return !showInLegend;
    }

    public void setShowInLegend(boolean showInLegend) {
        this.showInLegend = showInLegend;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setYAxisGroup(int yAxisGroup) {
        this.yAxisGroup = yAxisGroup;
    }

    public int getYAxisGroup() {
        return yAxisGroup;
    }
}
