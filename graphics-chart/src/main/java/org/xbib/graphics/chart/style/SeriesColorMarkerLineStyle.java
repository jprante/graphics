package org.xbib.graphics.chart.style;

import org.xbib.graphics.chart.theme.Theme;

import java.awt.BasicStroke;
import java.awt.Color;

public final class SeriesColorMarkerLineStyle {

    private final Color color;
    private final Theme.Series.Marker marker;
    private final BasicStroke stroke;

    public SeriesColorMarkerLineStyle(Color color, Theme.Series.Marker marker, BasicStroke stroke) {
        this.color = color;
        this.marker = marker;
        this.stroke = stroke;
    }

    public Color getColor() {
        return color;
    }

    public Theme.Series.Marker getMarker() {
        return marker;
    }

    public BasicStroke getStroke() {
        return stroke;
    }
}
