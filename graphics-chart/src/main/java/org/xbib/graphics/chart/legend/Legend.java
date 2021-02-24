package org.xbib.graphics.chart.legend;

import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.Styler;

public interface Legend<ST extends Styler, S extends Series> extends ChartComponent {

    double getSeriesLegendRenderGraphicHeight(S series);
}
