package org.xbib.graphics.chart.xy;

import org.xbib.graphics.chart.axis.DataType;
import org.xbib.graphics.chart.series.AxesChartSeriesNumericalNoErrorBars;
import org.xbib.graphics.chart.legend.LegendRenderType;

import java.util.List;

/**
 * A Series containing X and Y data to be plotted on a Chart
 */
public class XYSeries extends AxesChartSeriesNumericalNoErrorBars {

    private XYSeriesRenderStyle xySeriesRenderStyle;

    public XYSeries(String name,
                    List<?> xData,
                    List<? extends Number> yData,
                    List<? extends Number> errorBars,
                    DataType dataType) {
        super(name, xData, yData, errorBars, dataType);
    }

    public XYSeriesRenderStyle getXySeriesRenderStyle() {
        return xySeriesRenderStyle;
    }

    public void setXySeriesRenderStyle(XYSeriesRenderStyle xySeriesRenderStyle) {
        this.xySeriesRenderStyle = xySeriesRenderStyle;
    }

    @Override
    public LegendRenderType getLegendRenderType() {
        return xySeriesRenderStyle.getLegendRenderType();
    }


}