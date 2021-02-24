package org.xbib.graphics.chart.bubble;

import org.xbib.graphics.chart.axis.DataType;
import org.xbib.graphics.chart.series.NoMarkersSeries;
import org.xbib.graphics.chart.legend.LegendRenderType;

import java.util.List;

/**
 * A Series containing X, Y and bubble size data to be plotted on a Chart.
 */
public class BubbleSeries extends NoMarkersSeries {

    private BubbleSeriesRenderStyle bubbleSeriesRenderStyle = null;

    public BubbleSeries(String name, List<?> xData, List<? extends Number> yData, List<? extends Number> bubbleSizes) {
        super(name, xData, yData, bubbleSizes, DataType.Number);
    }

    public BubbleSeriesRenderStyle getBubbleSeriesRenderStyle() {
        return bubbleSeriesRenderStyle;
    }

    public void setBubbleSeriesRenderStyle(BubbleSeriesRenderStyle bubbleSeriesRenderStyle) {
        this.bubbleSeriesRenderStyle = bubbleSeriesRenderStyle;
    }

    @Override
    public LegendRenderType getLegendRenderType() {
        return bubbleSeriesRenderStyle.getLegendRenderType();
    }

}
