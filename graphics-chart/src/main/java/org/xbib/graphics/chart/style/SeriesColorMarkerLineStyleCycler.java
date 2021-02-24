package org.xbib.graphics.chart.style;

import org.xbib.graphics.chart.theme.Theme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

/**
 * Cycles through the different colors, markers, and strokes in a predetermined way.
 */
public class SeriesColorMarkerLineStyleCycler {

    private final List<Color> seriesColorList;
    private final List<Theme.Series.Marker> seriesMarkerList;
    private final List<BasicStroke> seriesLineStyleList;
    private int colorCounter = 0;
    private int markerCounter = 0;
    private int strokeCounter = 0;

    public SeriesColorMarkerLineStyleCycler(List<Color> seriesColorList,
                                            List<Theme.Series.Marker> seriesMarkerList,
                                            List<BasicStroke> seriesLineStyleList) {
        this.seriesColorList = seriesColorList;
        this.seriesMarkerList = seriesMarkerList;
        this.seriesLineStyleList = seriesLineStyleList;
    }

    public SeriesColorMarkerLineStyle getNextSeriesColorMarkerLineStyle() {
        if (colorCounter >= seriesColorList.size()) {
            colorCounter = 0;
            strokeCounter++;
        }
        Color seriesColor = seriesColorList.get(colorCounter++);
        if (strokeCounter >= seriesLineStyleList.size()) {
            strokeCounter = 0;
        }
        BasicStroke seriesLineStyle = seriesLineStyleList.get(strokeCounter);
        if (markerCounter >= seriesMarkerList.size()) {
            markerCounter = 0;
        }
        Theme.Series.Marker marker = seriesMarkerList.get(markerCounter++);
        return new SeriesColorMarkerLineStyle(seriesColor, marker, seriesLineStyle);
    }
}
