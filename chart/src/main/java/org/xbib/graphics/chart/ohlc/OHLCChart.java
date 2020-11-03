package org.xbib.graphics.chart.ohlc;

import org.xbib.graphics.chart.axis.DataType;
import org.xbib.graphics.chart.axis.Axis;
import org.xbib.graphics.chart.axis.AxisPair;
import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.plot.AxesChartPlot;
import org.xbib.graphics.chart.plot.ContentPlot;
import org.xbib.graphics.chart.style.AxesChartStyler;
import org.xbib.graphics.chart.style.SeriesColorMarkerLineStyle;
import org.xbib.graphics.chart.style.SeriesColorMarkerLineStyleCycler;
import org.xbib.graphics.chart.theme.Theme;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OHLCChart extends Chart<OHLCStyler, OHLCSeries> {

    public OHLCChart(int width, int height) {
        super(width, height, new OHLCStyler());
        axisPair = new AxisPair<>(this);
        plot = new OHLCPlot<>(this);
        legend = new OHLCLegend<>(this);
    }

    public OHLCChart(int width, int height, Theme theme) {
        this(width, height);
        styler.setTheme(theme);
    }

    public OHLCChart(OHLCChartBuilder chartBuilder) {
        this(chartBuilder.getWidth(), chartBuilder.getHeight(), chartBuilder.getTheme());
        setTitle(chartBuilder.getTitle());
        setXAxisTitle(chartBuilder.getxAxisTitle());
        setYAxisTitle(chartBuilder.getyAxisTitle());
    }

    public OHLCSeries addSeries(String seriesName,
            List<?> xData,
            List<? extends Number> openData,
            List<? extends Number> highData,
            List<? extends Number> lowData,
            List<? extends Number> closeData) {

        DataType dataType = getDataType(xData);
        if (dataType == DataType.Instant) {
            return addSeries(seriesName, xData, openData, highData, lowData, closeData, DataType.Instant);
        }
        return addSeries(seriesName, xData, openData, highData, lowData, closeData, DataType.Number);
    }

    public OHLCSeries addSeries(String seriesName,
            double[] xData,
            double[] openData,
            double[] highData,
            double[] lowData,
            double[] closeData) {
        return addSeries(seriesName,
                listFromDoubleArray(xData),
                listFromDoubleArray(openData),
                listFromDoubleArray(highData),
                listFromDoubleArray(lowData),
                listFromDoubleArray(closeData), DataType.Number);
    }

    private OHLCSeries addSeries(String seriesName,
                                 List<?> xData,
                                 List<? extends Number> openData,
                                 List<? extends Number> highData,
                                 List<? extends Number> lowData,
                                 List<? extends Number> closeData,
                                 DataType dataType) {
        if (seriesMap.containsKey(seriesName)) {
            throw new IllegalArgumentException("Series name >" + seriesName
                            + "< has already been used. Use unique names for each series!!!");
        }
        sanityCheck(seriesName, openData, highData, lowData, closeData);
        final List<?> xDataToUse;
        if (xData != null) {
            checkDataLengths(seriesName, "X-Axis", xData, closeData);
            xDataToUse = xData;
        } else {
            xDataToUse = getGeneratedData(closeData.size());
        }
        OHLCSeries series = new OHLCSeries(seriesName, xDataToUse, openData, highData, lowData, closeData, dataType);
        seriesMap.put(seriesName, series);
        return series;
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

    private void checkData(String seriesName, String dataName, List<? extends Number> data) {
        if (data == null) {
            throw new IllegalArgumentException(dataName + " data cannot be null >" + seriesName);
        }
        if (data.size() == 0) {
            throw new IllegalArgumentException(dataName + " data cannot be empty >" + seriesName);
        }
    }

    private void checkDataLengths(String seriesName, String data1Name,
                                  List<?> data1,
                                  List<?> data2) {
        String data2Name = "Close";
        if (data1.size() != data2.size()) {
            throw new IllegalArgumentException(
                    data1Name + " and " + data2Name + " sizes are not the same >" + seriesName);
        }
    }
    private void sanityCheck(String seriesName,
                             List<? extends Number> openData,
                             List<? extends Number> highData,
                             List<? extends Number> lowData,
                             List<? extends Number> closeData) {
        checkData(seriesName, "Open", openData);
        checkData(seriesName, "High", highData);
        checkData(seriesName, "Low", lowData);
        checkData(seriesName, "Close", closeData);
        checkDataLengths(seriesName, "Open", openData, closeData);
        checkDataLengths(seriesName, "High", highData, closeData);
        checkDataLengths(seriesName, "Low", lowData, closeData);
    }

    @Override
    public void paint(Graphics2D g, int width, int height) {
        setWidth(width);
        setHeight(height);
        // set the series render styles if they are not set. Legend and Plot need it.
        for (OHLCSeries series : getSeriesMap().values()) {
            OHLCSeriesRenderStyle renderStyle =
                    series.getOhlcSeriesRenderStyle(); // would be directly set
            if (renderStyle == null) { // wasn't overridden, use default from Style Manager
                series.setOhlcSeriesRenderStyle(getStyler().getDefaultSeriesRenderStyle());
            }
        }
        setSeriesStyles();
        paintBackground(g);
        axisPair.paint(g);
        plot.paint(g);
        chartTitle.paint(g);
        legend.paint(g);
    }

    /** set the series color, marker and line style based on theme */
    private void setSeriesStyles() {
        SeriesColorMarkerLineStyleCycler seriesColorMarkerLineStyleCycler =
                new SeriesColorMarkerLineStyleCycler(getStyler().getSeriesColors(),
                        getStyler().getSeriesMarkers(),
                        getStyler().getSeriesLines());
        for (OHLCSeries series : getSeriesMap().values()) {
            SeriesColorMarkerLineStyle seriesColorMarkerLineStyle =
                    seriesColorMarkerLineStyleCycler.getNextSeriesColorMarkerLineStyle();
            if (series.getLineStyle() == null) {
                series.setLineStyle(seriesColorMarkerLineStyle.getStroke());
            }
            if (series.getLineColor() == null) {
                series.setLineColor(seriesColorMarkerLineStyle.getColor());
            }
            if (series.getFillColor() == null) {
                series.setFillColor(seriesColorMarkerLineStyle.getColor());
            }
            if (series.getUpColor() == null) {
                series.setUpColor(Color.GREEN);
            }
            if (series.getDownColor() == null) {
                series.setDownColor(Color.RED);
            }
        }
    }

    private static class OHLCPlot<ST extends AxesChartStyler, S extends OHLCSeries> extends AxesChartPlot<ST, S> {

        private OHLCPlot(Chart<ST, S> chart) {
            super(chart);
            this.contentPlot = new ContentPlotOHLC<>(chart);
        }
    }

    private static class ContentPlotOHLC<ST extends AxesChartStyler, S extends OHLCSeries> extends ContentPlot<ST, S> {

        private final ST ohlcStyler;

        private ContentPlotOHLC(Chart<ST, S> chart) {
            super(chart);
            ohlcStyler = chart.getStyler();
        }

        @Override
        public void doPaint(Graphics2D g) {
            double xTickSpace = ohlcStyler.getPlotContentSize() * getBounds().getWidth();
            double xLeftMargin = ((int) getBounds().getWidth() - xTickSpace) / 2.0;
            double yTickSpace = ohlcStyler.getPlotContentSize() * getBounds().getHeight();
            double yTopMargin = ((int) getBounds().getHeight() - yTickSpace) / 2.0;
            double xMin = chart.getXAxis().getMin();
            double xMax = chart.getXAxis().getMax();
            Line2D.Double line = new Line2D.Double();
            Rectangle2D.Double rect = new Rectangle2D.Double();
            if (ohlcStyler.isXAxisLogarithmic()) {
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
                if (ohlcStyler.isYAxisLogarithmic()) {
                    yMin = Math.log10(yMin);
                    yMax = Math.log10(yMax);
                }
                List<?> xData = series.getXData();
                List<? extends Number> openData = series.getOpenData();
                List<? extends Number> highData = series.getHighData();
                List<? extends Number> lowData = series.getLowData();
                List<? extends Number> closeData = series.getCloseData();
                double candleHalfWidth =
                        Math.max(3, xTickSpace / xData.size() / 2 - ohlcStyler.getAxisTickPadding());
                for (int i = 0; i < xData.size(); i++) {
                    Double x = (Double) xData.get(i);
                    if (ohlcStyler.isXAxisLogarithmic()) {
                        x = Math.log10(x);
                    }
                    if (Double.isNaN((Double) closeData.get(i))) {
                        continue;
                    }
                    Double openOrig = (Double) openData.get(i);
                    Double highOrig = (Double) highData.get(i);
                    Double lowOrig = (Double) lowData.get(i);
                    Double closeOrig = (Double) closeData.get(i);
                    double openY;
                    double highY;
                    double lowY;
                    double closeY;
                    if (ohlcStyler.isYAxisLogarithmic()) {
                        openY = Math.log10(openOrig);
                        highY = Math.log10(highOrig);
                        lowY = Math.log10(lowOrig);
                        closeY = Math.log10(closeOrig);
                    } else {
                        openY = openOrig;
                        highY = highOrig;
                        lowY = lowOrig;
                        closeY = closeOrig;
                    }
                    double xTransform = xLeftMargin + ((x - xMin) / (xMax - xMin) * xTickSpace);
                    double openTransform =
                            getBounds().getHeight() - (yTopMargin + (openY - yMin) / (yMax - yMin) * yTickSpace);
                    double highTransform =
                            getBounds().getHeight() - (yTopMargin + (highY - yMin) / (yMax - yMin) * yTickSpace);
                    double lowTransform =
                            getBounds().getHeight() - (yTopMargin + (lowY - yMin) / (yMax - yMin) * yTickSpace);
                    double closeTransform =
                            getBounds().getHeight() - (yTopMargin + (closeY - yMin) / (yMax - yMin) * yTickSpace);
                    if (Math.abs(xMax - xMin) / 5 == 0.0) {
                        xTransform = getBounds().getWidth() / 2.0;
                    }
                    if (Math.abs(yMax - yMin) / 5 == 0.0) {
                        openTransform = getBounds().getHeight() / 2.0;
                        highTransform = getBounds().getHeight() / 2.0;
                        lowTransform = getBounds().getHeight() / 2.0;
                        closeTransform = getBounds().getHeight() / 2.0;
                    }
                    double xOffset = getBounds().getX() + xTransform;
                    double openOffset = getBounds().getY() + openTransform;
                    double highOffset = getBounds().getY() + highTransform;
                    double lowOffset = getBounds().getY() + lowTransform;
                    double closeOffset = getBounds().getY() + closeTransform;
                    if (series.getLineStyle() != Theme.Series.NONE_STROKE) {
                        if (xOffset != -Double.MAX_VALUE
                                && openOffset != -Double.MAX_VALUE
                                && highOffset != -Double.MAX_VALUE
                                && lowOffset != -Double.MAX_VALUE
                                && closeOffset != -Double.MAX_VALUE) {
                            g.setColor(series.getLineColor());
                            g.setStroke(series.getLineStyle());
                            line.setLine(xOffset, highOffset, xOffset, lowOffset);
                            g.draw(line);
                            final double xStart = xOffset - candleHalfWidth;
                            final double xEnd = xOffset + candleHalfWidth;
                            if (series.getOhlcSeriesRenderStyle() == OHLCSeriesRenderStyle.Candle) {
                                if (closeOrig > openOrig) {
                                    g.setPaint(series.getUpColor());
                                } else {
                                    g.setPaint(series.getDownColor());
                                }
                                rect.setRect(xStart,
                                        Math.min(openOffset, closeOffset),
                                        xEnd - xStart,
                                        Math.abs(closeOffset - openOffset));
                                g.fill(rect);
                            } else {
                                line.setLine(xStart, openOffset, xOffset, openOffset);
                                g.draw(line);
                                line.setLine(xOffset, closeOffset, xEnd, closeOffset);
                                g.draw(line);
                            }
                        }
                    }
                }
                g.setColor(series.getFillColor());
            }
        }
    }
}
