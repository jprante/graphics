package org.xbib.graphics.chart.xy;

import org.xbib.graphics.chart.axis.DataType;
import org.xbib.graphics.chart.axis.Axis;
import org.xbib.graphics.chart.axis.AxisPair;
import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.legend.MarkerLegend;
import org.xbib.graphics.chart.plot.AxesChartPlot;
import org.xbib.graphics.chart.plot.ContentPlot;
import org.xbib.graphics.chart.style.AxesChartStyler;
import org.xbib.graphics.chart.style.SeriesColorMarkerLineStyle;
import org.xbib.graphics.chart.style.SeriesColorMarkerLineStyleCycler;
import org.xbib.graphics.chart.theme.Theme;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XYChart extends Chart<XYStyler, XYSeries> {

    public XYChart(int width, int height) {
        super(width, height, new XYStyler());
        axisPair = new AxisPair<>(this);
        plot = new XYPlot<>(this);
        legend = new MarkerLegend<>(this);
    }

    public XYChart(int width, int height, Theme theme) {
        this(width, height);
        styler.setTheme(theme);
    }

    public XYChart(XYChartBuilder chartBuilder) {
        this(chartBuilder.getWidth(), chartBuilder.getHeight(), chartBuilder.getTheme());
        setTitle(chartBuilder.getTitle());
        setXAxisTitle(chartBuilder.getxAxisTitle());
        setYAxisTitle(chartBuilder.getyAxisTitle());
    }

    public XYSeries addSeries(String seriesName, List<?> xData, List<? extends Number> yData) {
        return addSeries(seriesName, xData, yData, null, getDataType(xData));
    }

    public XYSeries addSeries(String seriesName, List<?> xData, List<? extends Number> yData, List<? extends Number> errorBars) {
        return addSeries(seriesName, xData, yData, errorBars, getDataType(xData));
    }

    public XYSeries addSeries(String seriesName, double[] xData, double[] yData) {
        return addSeries(seriesName, xData, yData, null);
    }

    public XYSeries addSeries(String seriesName, double[] xData, double[] yData, double[] errorBars) {
        return addSeries(seriesName,
                listFromDoubleArray(xData),
                listFromDoubleArray(yData),
                listFromDoubleArray(errorBars),
                DataType.Number);
    }

    public XYSeries addSeries(String seriesName, int[] xData, int[] yData) {
        return addSeries(seriesName, xData, yData, null);
    }

    public XYSeries addSeries(String seriesName, int[] xData, int[] yData, int[] errorBars) {
        return addSeries(seriesName, listFromIntArray(xData), listFromIntArray(yData),
                listFromIntArray(errorBars), DataType.Number);
    }

    public XYSeries addSeries(String seriesName,
                              List<?> xData,
                              List<? extends Number> yData,
                              List<? extends Number> errorBars,
                              DataType dataType) {
        sanityCheck(seriesName, xData, yData, errorBars);
        XYSeries series;
        if (xData != null) {
            if (xData.size() != yData.size()) {
                throw new IllegalArgumentException("X and Y-Axis sizes are not the same");
            }
            series = new XYSeries(seriesName, xData, yData, errorBars, dataType);
        } else {
            series = new XYSeries(seriesName, getGeneratedData(yData.size()), yData, errorBars, dataType);
        }
        seriesMap.put(seriesName, series);
        return series;
    }

    @Override
    public void paint(Graphics2D g, int width, int height) {
        setWidth(width);
        setHeight(height);
        for (XYSeries XYSeries : getSeriesMap().values()) {
            XYSeriesRenderStyle XYSeriesRenderStyle = XYSeries.getXySeriesRenderStyle();
            if (XYSeriesRenderStyle == null) {
                XYSeries.setXySeriesRenderStyle(getStyler().getDefaultSeriesRenderStyle());
            }
        }
        setSeriesStyles();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(styler.getChartBackgroundColor());
        Shape rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.fill(rect);
        axisPair.paint(g);
        plot.paint(g);
        chartTitle.paint(g);
        legend.paint(g);
        g.dispose();
    }

    private void setSeriesStyles() {
        SeriesColorMarkerLineStyleCycler seriesColorMarkerLineStyleCycler =
                new SeriesColorMarkerLineStyleCycler(getStyler().getSeriesColors(),
                        getStyler().getSeriesMarkers(),
                        getStyler().getSeriesLines());
        for (XYSeries series : getSeriesMap().values()) {
            SeriesColorMarkerLineStyle seriesColorMarkerLineStyle = seriesColorMarkerLineStyleCycler.getNextSeriesColorMarkerLineStyle();
            if (series.getLineStyle() == null) {
                series.setLineStyle(seriesColorMarkerLineStyle.getStroke());
            }
            if (series.getLineColor() == null) {
                series.setLineColor(seriesColorMarkerLineStyle.getColor());
            }
            if (series.getFillColor() == null) {
                series.setFillColor(seriesColorMarkerLineStyle.getColor());
            }
            if (series.getMarker() == null) {
                series.setMarker(seriesColorMarkerLineStyle.getMarker());
            }
            if (series.getMarkerColor() == null) {
                series.setMarkerColor(seriesColorMarkerLineStyle.getColor());
            }
        }
    }

    private DataType getDataType(List<?> data) {
        if (data == null) {
            return DataType.Number;
        }
        DataType axisType;
        Iterator<?> itr = data.iterator();
        Object dataPoint = itr.next();
        if (dataPoint instanceof Number) {
            axisType = DataType.Number;
        } else if (dataPoint instanceof Instant) {
            axisType = DataType.Instant;
        } else {
            throw new IllegalArgumentException("Series data must be either Number or Instant type");
        }
        return axisType;
    }

    private static class XYPlot<ST extends AxesChartStyler, S extends XYSeries> extends AxesChartPlot<ST, S> {

        private XYPlot(Chart<ST, S> chart) {
            super(chart);
            this.contentPlot = new ContentPlotXY<>(chart);
        }
    }

    private static class ContentPlotXY<ST extends AxesChartStyler, S extends XYSeries> extends ContentPlot<ST, S> {

        private final ST xystyler;

        private ContentPlotXY(Chart<ST, S> chart) {
            super(chart);
            xystyler = chart.getStyler();
        }

        @Override
        public void doPaint(Graphics2D g) {
            double xTickSpace = xystyler.getPlotContentSize() * getBounds().getWidth();
            double xLeftMargin = ((int) getBounds().getWidth() - xTickSpace) / 2.0;
            double yTickSpace = xystyler.getPlotContentSize() * getBounds().getHeight();
            double yTopMargin = ((int) getBounds().getHeight() - yTickSpace) / 2.0;
            double xMin = chart.getXAxis().getMin();
            double xMax = chart.getXAxis().getMax();
            Line2D.Double line = new Line2D.Double();
            if (xystyler.isXAxisLogarithmic()) {
                xMin = Math.log10(xMin);
                xMax = Math.log10(xMax);
            }
            Map<String, S> map = chart.getSeriesMap();
            for (S series : map.values()) {
                if (!series.isEnabled()) {
                    continue;
                }
                Axis<?, ?> yAxis = chart.getYAxis(series.getYAxisGroup());
                double yMin = yAxis.getMin();
                double yMax = yAxis.getMax();
                if (xystyler.isYAxisLogarithmic()) {
                    yMin = Math.log10(yMin);
                    yMax = Math.log10(yMax);
                }
                Collection<?> xData = series.getXData();
                Collection<? extends Number> yData = series.getYData();
                double previousX = -Double.MAX_VALUE;
                double previousY = -Double.MAX_VALUE;
                Iterator<?> xItr = xData.iterator();
                Iterator<? extends Number> yItr = yData.iterator();
                Iterator<? extends Number> ebItr = null;
                Collection<? extends Number> errorBars = series.getExtraValues();
                if (errorBars != null) {
                    ebItr = errorBars.iterator();
                }
                Path2D.Double path = null;
                while (xItr.hasNext()) {
                    Double x = null;
                    if (chart.getXAxis().getDataType() == DataType.Number) {
                        Number number = (Number) xItr.next();
                        x = number != null ? number.doubleValue() : null;
                    } else if (chart.getXAxis().getDataType() == DataType.Instant) {
                        Instant instant = (Instant) xItr.next();
                        x = instant != null ? (double) instant.toEpochMilli() : null;
                    }
                    if (xystyler.isXAxisLogarithmic()) {
                        x = x != null ? Math.log10(x) : null;
                    }
                    Number next = yItr.next();
                    if (x == null || next == null) {
                        closePath(g, path, previousX, yTopMargin);
                        path = null;
                        previousX = -Double.MAX_VALUE;
                        previousY = -Double.MAX_VALUE;
                        continue;
                    }
                    double yOrig = next.doubleValue();
                    double y;
                    if (xystyler.isYAxisLogarithmic()) {
                        y = Math.log10(yOrig);
                    } else {
                        y = yOrig;
                    }
                    double xTransform = xLeftMargin + ((x - xMin) / (xMax - xMin) * xTickSpace);
                    double yTransform = getBounds().getHeight() - (yTopMargin + (y - yMin) / (yMax - yMin) * yTickSpace);
                    if (Math.abs(xMax - xMin) / 5 == 0.0) {
                        xTransform = getBounds().getWidth() / 2.0;
                    }
                    if (Math.abs(yMax - yMin) / 5 == 0.0) {
                        yTransform = getBounds().getHeight() / 2.0;
                    }
                    double xOffset = getBounds().getX() + xTransform;
                    double yOffset = getBounds().getY() + yTransform;
                    boolean isSeriesLineOrArea = (XYSeriesRenderStyle.Line == series.getXySeriesRenderStyle()) ||
                            (XYSeriesRenderStyle.Area == series.getXySeriesRenderStyle());
                    boolean isSeriesStepLineOrStepArea = XYSeriesRenderStyle.Step == series.getXySeriesRenderStyle() ||
                            XYSeriesRenderStyle.StepArea == series.getXySeriesRenderStyle();
                    if (isSeriesLineOrArea || isSeriesStepLineOrStepArea) {
                        if (series.getLineStyle() != Theme.Series.NONE_STROKE) {
                            if (previousX != -Double.MAX_VALUE && previousY != -Double.MAX_VALUE) {
                                g.setColor(series.getLineColor());
                                g.setStroke(series.getLineStyle());
                                if (isSeriesLineOrArea) {
                                    line.setLine(previousX, previousY, xOffset, yOffset);
                                    g.draw(line);
                                } else {
                                    if (previousX != xOffset) {
                                        line.setLine(previousX, previousY, xOffset, previousY);
                                        g.draw(line);
                                    }
                                    if (previousY != yOffset) {
                                        line.setLine(xOffset, previousY, xOffset, yOffset);
                                        g.draw(line);
                                    }
                                }
                            }
                        }
                    }
                    if (XYSeriesRenderStyle.Area == series.getXySeriesRenderStyle() ||
                            XYSeriesRenderStyle.StepArea == series.getXySeriesRenderStyle()) {
                        if (previousX != -Double.MAX_VALUE && previousY != -Double.MAX_VALUE) {
                            g.setColor(series.getFillColor());
                            double yBottomOfArea = getBounds().getY() + getBounds().getHeight() - yTopMargin;
                            if (path == null) {
                                path = new Path2D.Double();
                                path.moveTo(previousX, yBottomOfArea);
                                path.lineTo(previousX, previousY);
                            }
                            if (XYSeriesRenderStyle.Area == series.getXySeriesRenderStyle()) {
                                path.lineTo(xOffset, yOffset);
                            } else {
                                if (previousX != xOffset) {
                                    path.lineTo(xOffset, previousY);
                                }
                                if (previousY != yOffset) {
                                    path.lineTo(xOffset, yOffset);
                                }
                            }
                        }
                        if (xOffset < previousX) {
                            throw new RuntimeException("X-Data must be in ascending order for Area Charts");
                        }
                    }
                    previousX = xOffset;
                    previousY = yOffset;
                    if (series.getMarker() != null) {
                        g.setColor(series.getMarkerColor());
                        series.getMarker().paint(g, xOffset, yOffset, xystyler.getMarkerSize());
                    }
                    if (errorBars != null) {
                        double eb = ebItr.next().doubleValue();
                        if (xystyler.isErrorBarsColorSeriesColor()) {
                            g.setColor(series.getLineColor());
                        } else {
                            g.setColor(xystyler.getErrorBarsColor());
                        }
                        g.setStroke(Theme.Strokes.ERROR_BARS);
                        double topValue;
                        if (xystyler.isYAxisLogarithmic()) {
                            topValue = yOrig + eb;
                            topValue = Math.log10(topValue);
                        } else {
                            topValue = y + eb;
                        }
                        double topEBTransform = getBounds().getHeight() - (yTopMargin + (topValue - yMin) / (yMax - yMin) * yTickSpace);
                        double topEBOffset = getBounds().getY() + topEBTransform;
                        double bottomValue;
                        if (xystyler.isYAxisLogarithmic()) {
                            bottomValue = yOrig - eb;
                            bottomValue = Math.log10(bottomValue);
                        } else {
                            bottomValue = y - eb;
                        }
                        double bottomEBTransform = getBounds().getHeight() - (yTopMargin + (bottomValue - yMin) / (yMax - yMin) * yTickSpace);
                        double bottomEBOffset = getBounds().getY() + bottomEBTransform;
                        line = new Line2D.Double(xOffset, topEBOffset, xOffset, bottomEBOffset);
                        g.draw(line);
                        line = new Line2D.Double(xOffset - 3, bottomEBOffset, xOffset + 3, bottomEBOffset);
                        g.draw(line);
                        line = new Line2D.Double(xOffset - 3, topEBOffset, xOffset + 3, topEBOffset);
                        g.draw(line);
                    }
                }
                g.setColor(series.getFillColor());
                closePath(g, path, previousX, yTopMargin);
            }
        }
    }

    private void sanityCheck(String seriesName, List<?> xData, List<? extends Number> yData, List<? extends Number> errorBars) {
        if (seriesMap.containsKey(seriesName)) {
            throw new IllegalArgumentException("Series name >" + seriesName + "< has already been used. Use unique names for each series");
        }
        if (yData == null) {
            throw new IllegalArgumentException("Y-Axis data cannot be null");
        }
        if (yData.size() == 0) {
            throw new IllegalArgumentException("Y-Axis data cannot be empty");
        }
        if (xData != null && xData.size() == 0) {
            throw new IllegalArgumentException("X-Axis data cannot be empty");
        }
        if (errorBars != null && errorBars.size() != yData.size()) {
            throw new IllegalArgumentException("Error bars and Y-Axis sizes are not the same");
        }
    }
}
