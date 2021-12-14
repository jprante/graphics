package org.xbib.graphics.chart;

import org.xbib.graphics.chart.theme.Theme;
import org.xbib.graphics.chart.xy.XYChart;
import org.xbib.graphics.chart.xy.XYSeries;

import java.util.List;

/**
 * A convenience class for making Charts with one line of code.
 */
public final class QuickChart {

    private final static int WIDTH = 600;

    private final static int HEIGHT = 400;

    private QuickChart() {
    }

    public static XYChart getChart(String chartTitle,
                                   String xTitle,
                                   String yTitle,
                                   String seriesName,
                                   double[] xData,
                                   double[] yData) {
        double[][] yData2d = { yData };
        if (seriesName == null) {
            return getChart(chartTitle, xTitle, yTitle, null, xData, yData2d);
        } else {
            return getChart(chartTitle, xTitle, yTitle, new String[]{seriesName}, xData, yData2d);
        }
    }

    public static XYChart getChart(String chartTitle,
                                   String xTitle,
                                   String yTitle,
                                   String[] seriesNames,
                                   double[] xData,
                                   double[][] yData) {
        XYChart chart = new XYChart(WIDTH, HEIGHT);
        chart.setTitle(chartTitle);
        chart.setXAxisTitle(xTitle);
        chart.setYAxisTitle(yTitle);
        for (int i = 0; i < yData.length; i++) {
            XYSeries series;
            if (seriesNames != null) {
                series = chart.addSeries(seriesNames[i], xData, yData[i]);
            } else {
                chart.getStyler().setLegendVisible(false);
                series = chart.addSeries(" " + i, xData, yData[i]);
            }
            series.setMarker(Theme.Series.NONE_MARKER);
        }
        return chart;
    }

    public static XYChart getChart(String chartTitle,
                                   String xTitle,
                                   String yTitle,
                                   String seriesName,
                                   List<? extends Double> xData,
                                   List<? extends Double> yData) {
        XYChart chart = new XYChart(WIDTH, HEIGHT);
        chart.setTitle(chartTitle);
        chart.setXAxisTitle(xTitle);
        chart.setYAxisTitle(yTitle);
        XYSeries series = chart.addSeries(seriesName, xData, yData);
        series.setMarker(Theme.Series.NONE_MARKER);
        return chart;
    }
}
