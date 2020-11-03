package org.xbib.graphics.chart.series;

import org.xbib.graphics.chart.axis.DataType;
import org.xbib.graphics.chart.theme.Theme;

import java.awt.Color;

/**
 * A Series containing X and Y data to be plotted on a Chart with X and Y Axes,
 * contains series markers and error bars.
 */
public abstract class MarkerSeries extends AxesChartSeries {

    private Theme.Series.Marker marker;

    private Color markerColor;

    protected MarkerSeries(String name, DataType xDataType) {
        super(name, xDataType);
    }

    public void setMarker(Theme.Series.Marker marker) {
        this.marker = marker;
    }

    public Theme.Series.Marker getMarker() {
        return marker;
    }

    public void setMarkerColor(Color color) {
        this.markerColor = color;
    }

    public Color getMarkerColor() {
        return markerColor;
    }
}
