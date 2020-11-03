package org.xbib.graphics.chart.category;

import org.xbib.graphics.chart.axis.DataType;
import org.xbib.graphics.chart.axis.Axis;
import org.xbib.graphics.chart.axis.AxisPair;
import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.legend.MarkerLegend;
import org.xbib.graphics.chart.plot.AxesChartPlot;
import org.xbib.graphics.chart.plot.ContentPlot;
import org.xbib.graphics.chart.style.SeriesColorMarkerLineStyle;
import org.xbib.graphics.chart.style.SeriesColorMarkerLineStyleCycler;
import org.xbib.graphics.chart.theme.Theme;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.xbib.graphics.chart.category.CategorySeriesRenderStyle.SteppedBar;

public class CategoryChart extends Chart<CategoryStyler, CategorySeries> {

    public CategoryChart(int width, int height) {
        super(width, height, new CategoryStyler());
        axisPair = new AxisPair<>(this);
        plot = new CategoryPlot<>(this);
        legend = new MarkerLegend<>(this);
    }

    public CategoryChart(int width, int height, Theme theme) {
        this(width, height);
        styler.setTheme(theme);
    }

    public CategoryChart(CategoryChartBuilder chartBuilder) {
        this(chartBuilder.getWidth(), chartBuilder.getHeight(), chartBuilder.getTheme());
        setTitle(chartBuilder.getTitle());
        setXAxisTitle(chartBuilder.getxAxisTitle());
        setYAxisTitle(chartBuilder.getyAxisTitle());
    }

    /**
     * Add a series for a Category type chart using using double arrays
     *
     * @param seriesName series name
     * @param xData      the X-Axis data
     * @param yData      the Y-Axis data
     * @return A Series object that you can set properties on
     */
    public CategorySeries addSeries(String seriesName, double[] xData, double[] yData) {
        return addSeries(seriesName, xData, yData, null);
    }

    /**
     * Add a series for a Category type chart using using double arrays with error bars
     *
     * @param seriesName series name
     * @param xData      the X-Axis data
     * @param yData      the Y-Axis data
     * @param errorBars  the error bar data
     * @return A Series object that you can set properties on
     */
    public CategorySeries addSeries(String seriesName, double[] xData, double[] yData, double[] errorBars) {
        return addSeries(seriesName, listFromDoubleArray(xData), listFromDoubleArray(yData),
                listFromDoubleArray(errorBars));
    }

    /**
     * Add a series for a X-Y type chart using using int arrays
     *
     * @param seriesName series name
     * @param xData      the X-Axis data
     * @param yData      the Y-Axis data
     * @return A Series object that you can set properties on
     */
    public CategorySeries addSeries(String seriesName, int[] xData, int[] yData) {
        return addSeries(seriesName, xData, yData, null);
    }

    /**
     * Add a series for a X-Y type chart using using int arrays with error bars
     *
     * @param seriesName series name
     * @param xData      the X-Axis data
     * @param yData      the Y-Axis data
     * @param errorBars  the error bar data
     * @return A Series object that you can set properties on
     */
    public CategorySeries addSeries(String seriesName, int[] xData, int[] yData, int[] errorBars) {
        return addSeries(seriesName, listFromIntArray(xData), listFromIntArray(yData),
                listFromIntArray(errorBars));
    }

    /**
     * Add a series for a Category type chart using Lists
     *
     * @param seriesName series name
     * @param xData      the X-Axis data
     * @param yData      the Y-Axis data
     * @return A Series object that you can set properties on
     */
    public CategorySeries addSeries(String seriesName, List<?> xData, List<? extends Number> yData) {
        return addSeries(seriesName, xData, yData, null);
    }

    /**
     * Add a series for a Category type chart using Lists with error bars
     *
     * @param seriesName series name
     * @param xData      the X-Axis data
     * @param yData      the Y-Axis data
     * @param errorBars  the error bar data
     * @return A Series object that you can set properties on
     */
    public CategorySeries addSeries(String seriesName,
                                    List<?> xData,
                                    List<? extends Number> yData,
                                    List<? extends Number> errorBars) {
        sanityCheck(seriesName, xData, yData, errorBars);
        CategorySeries series;
        if (xData != null) {
            if (xData.size() != yData.size()) {
                throw new IllegalArgumentException("X and Y-Axis sizes are not the same");
            }
            series = new CategorySeries(seriesName, xData, yData, errorBars, getDataType(xData));
        } else {
            series = new CategorySeries(seriesName, getGeneratedData(yData.size()), yData, errorBars, DataType.String);
        }
        seriesMap.put(seriesName, series);
        return series;
    }

    private void sanityCheck(String seriesName, List<?> xData, List<? extends Number> yData,
                             List<? extends Number> errorBars) {
        if (seriesMap.containsKey(seriesName)) {
            throw new IllegalArgumentException("Series name >" + seriesName + "< has already been used");
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

    @Override
    public void paint(Graphics2D g, int width, int height) {
        setWidth(width);
        setHeight(height);
        for (CategorySeries categorySeries : getSeriesMap().values()) {
            CategorySeriesRenderStyle seriesType = categorySeries.getCategorySeriesRenderStyle();
            if (seriesType == null) {
                categorySeries.setCategorySeriesRenderStyle(getStyler().getDefaultSeriesRenderStyle());
            }
        }
        setSeriesStyles();
        paintBackground(g);
        axisPair.paint(g);
        plot.paint(g);
        chartTitle.paint(g);
        legend.paint(g);
    }

    public void setSeriesStyles() {
        SeriesColorMarkerLineStyleCycler seriesColorMarkerLineStyleCycler =
                new SeriesColorMarkerLineStyleCycler(getStyler().getSeriesColors(),
                        getStyler().getSeriesMarkers(), getStyler().getSeriesLines());
        for (CategorySeries series : getSeriesMap().values()) {
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
            if (series.getMarker() == null) {
                series.setMarker(seriesColorMarkerLineStyle.getMarker());
            }
            if (series.getMarkerColor() == null) {
                series.setMarkerColor(seriesColorMarkerLineStyle.getColor());
            }
        }
    }

    private DataType getDataType(List<?> data) {
        DataType axisType;
        Iterator<?> itr = data.iterator();
        Object dataPoint = itr.next();
        if (dataPoint instanceof Number) {
            axisType = DataType.Number;
        } else if (dataPoint instanceof Instant) {
            axisType = DataType.Instant;
        } else if (dataPoint instanceof String) {
            axisType = DataType.String;
        } else {
            throw new IllegalArgumentException("Series data must be either Number, Instant or String type");
        }
        return axisType;
    }

    private static class CategoryPlot<ST extends CategoryStyler, S extends CategorySeries> extends AxesChartPlot<ST, S> {

        private final ST categoryStyler;

        private CategoryPlot(Chart<ST, S> chart) {
            super(chart);
            categoryStyler = chart.getStyler();
        }

        @Override
        public void paint(Graphics2D g) {
            if (CategorySeriesRenderStyle.Bar.equals(categoryStyler.getDefaultSeriesRenderStyle()) || CategorySeriesRenderStyle.Stick.equals(categoryStyler.getDefaultSeriesRenderStyle())) {
                this.contentPlot = new ContentPlotCategoryBar<>(chart);
            } else {
                this.contentPlot = new ContentPlotCategoryLineAreaScatter<>(chart);
            }
            super.paint(g);
        }
    }

    private static class ContentPlotCategoryBar<ST extends CategoryStyler, S extends CategorySeries> extends ContentPlot<ST, S> {

        private final ST stylerCategory;

        ContentPlotCategoryBar(Chart<ST, S> chart) {
            super(chart);
            this.stylerCategory = chart.getStyler();
        }

        @Override
        public void doPaint(Graphics2D g) {
            double xTickSpace = stylerCategory.getPlotContentSize() * getBounds().getWidth();
            double xLeftMargin = (getBounds().getWidth() - xTickSpace) / 2.0;
            Map<String, S> seriesMap = chart.getSeriesMap();
            int numCategories = seriesMap.values().iterator().next().getXData().size();
            double gridStep = xTickSpace / numCategories;
            double yMin = chart.getYAxis().getMin();
            double yMax = chart.getYAxis().getMax();
            int chartForm; // 1=positive, -1=negative, 0=span
            if (yMin > 0.0 && yMax > 0.0) {
                chartForm = 1; // positive chart
            } else if (yMin < 0.0 && yMax < 0.0) {
                chartForm = -1; // negative chart
            } else {
                chartForm = 0;// span chart
            }
            double yTickSpace = stylerCategory.getPlotContentSize() * getBounds().getHeight();
            double yTopMargin = (getBounds().getHeight() - yTickSpace) / 2.0;
            int seriesCounter = 0;
            double[] accumulatedStackOffsetPos = new double[numCategories];
            double[] accumulatedStackOffsetNeg = new double[numCategories];
            for (S series : seriesMap.values()) {
                double previousX = -Double.MAX_VALUE;
                double previousY = -Double.MAX_VALUE;
                Iterator<? extends Number> yItr = series.getYData().iterator();
                Iterator<? extends Number> ebItr = null;
                Collection<? extends Number> errorBars = series.getExtraValues();
                if (errorBars != null) {
                    ebItr = errorBars.iterator();
                }
                List<Point2D.Double> steppedPath = null;
                List<Point2D.Double> steppedReturnPath = null;
                int categoryCounter = 0;
                while (yItr.hasNext()) {
                    Number next = yItr.next();
                    if (next == null) {
                        previousX = -Double.MAX_VALUE;
                        previousY = -Double.MAX_VALUE;
                        categoryCounter++;
                        continue;
                    }
                    double y = next.doubleValue();
                    double yTop = 0.0;
                    double yBottom = 0.0;
                    switch (chartForm) {
                        case 1: // positive chart
                            // check for points off the chart draw area due to a custom yMin
                            if (y < yMin) {
                                categoryCounter++;
                                continue;
                            }
                            yTop = y;
                            yBottom = yMin;
                            break;
                        case -1: // negative chart
                            // check for points off the chart draw area due to a custom yMin
                            if (y > yMax) {
                                categoryCounter++;
                                continue;
                            }
                            yTop = yMax;
                            yBottom = y;
                            break;
                        case 0: // span chart
                            if (y >= 0.0) { // positive
                                yTop = y;
                                if (series.getCategorySeriesRenderStyle() == CategorySeriesRenderStyle.Bar
                                        || series.getCategorySeriesRenderStyle() == CategorySeriesRenderStyle.Stick
                                        || series.getCategorySeriesRenderStyle()
                                        == SteppedBar) {
                                    yBottom = 0.0;
                                } else {
                                    yBottom = y;
                                }
                                if (stylerCategory.isStacked()) {
                                    yTop += accumulatedStackOffsetPos[categoryCounter];
                                    yBottom += accumulatedStackOffsetPos[categoryCounter];
                                    accumulatedStackOffsetPos[categoryCounter] += (yTop - yBottom);
                                }
                            } else {
                                if (series.getCategorySeriesRenderStyle() == CategorySeriesRenderStyle.Bar
                                        || series.getCategorySeriesRenderStyle() == CategorySeriesRenderStyle.Stick
                                        || series.getCategorySeriesRenderStyle()
                                        == SteppedBar) {
                                    yTop = 0.0;
                                } else {
                                    yTop = y; // yTransform uses yTop, and for non-bars and stick, it's the same as
                                    // yBottom.
                                }
                                yBottom = y;
                                if (stylerCategory.isStacked()) {
                                    yTop -= accumulatedStackOffsetNeg[categoryCounter];
                                    yBottom -= accumulatedStackOffsetNeg[categoryCounter];
                                    accumulatedStackOffsetNeg[categoryCounter] += (yTop - yBottom);
                                }
                            }
                            break;
                        default:
                            break;
                    }

                    double yTransform = getBounds().getHeight() - (yTopMargin + (yTop - yMin) / (yMax - yMin) * yTickSpace);
                    double yOffset = getBounds().getY() + yTransform;

                    double zeroTransform = getBounds().getHeight() - (yTopMargin + (yBottom - yMin) / (yMax - yMin) * yTickSpace);
                    double zeroOffset = getBounds().getY() + zeroTransform;
                    double xOffset;
                    double barWidth;

                    {
                        double barWidthPercentage = stylerCategory.getAvailableSpaceFill();
                        // SteppedBars can not have any space between them
                        if (series.getCategorySeriesRenderStyle() == CategorySeriesRenderStyle.SteppedBar)
                            barWidthPercentage = 1;

                        if (stylerCategory.isOverlapped() || stylerCategory.isStacked()) {

                            barWidth = gridStep * barWidthPercentage;
                            double barMargin = gridStep * (1 - barWidthPercentage) / 2;
                            xOffset = getBounds().getX() + xLeftMargin + gridStep * categoryCounter++ + barMargin;
                        } else {

                            barWidth = gridStep / chart.getSeriesMap().size() * barWidthPercentage;
                            double barMargin = gridStep * (1 - barWidthPercentage) / 2;
                            xOffset =
                                    getBounds().getX()
                                            + xLeftMargin
                                            + gridStep * categoryCounter++
                                            + seriesCounter * barWidth
                                            + barMargin;
                        }
                    }

                    // SteppedBar. Partially drawn in loop, partially after loop.
                    if (series.getCategorySeriesRenderStyle() == CategorySeriesRenderStyle.SteppedBar) {

                        double yCenter = zeroOffset;
                        double yTip = yOffset;
                        double stepLength = gridStep;

                        // yTip should be the value end, yCenter the center (0) end.
                        if (y < 0) {

                            yTip = zeroOffset;
                            yCenter = yOffset;
                        }

                        // Init in first iteration
                        if (steppedPath == null) {
                            steppedPath = new ArrayList<>();
                            steppedReturnPath = new ArrayList<>();
                            steppedPath.add(new Point2D.Double(xOffset, yCenter));
                        } else if (stylerCategory.isStacked()) {
                            // If a section of a stacked graph has changed from positive
                            // to negative or vice-versa, draw what we've stored up so far
                            // and resume with a blank slate.
                            if ((previousY > 0 && y < 0) || (previousY < 0 && y > 0)) {
                                drawStepBar(g, series, steppedPath, steppedReturnPath);

                                steppedPath.clear();
                                steppedReturnPath.clear();
                                steppedPath.add(new Point2D.Double(xOffset, yCenter));
                            }
                        }

                        if (!yItr.hasNext()) {

                            // Shift the far point of the final bar backwards
                            // by the same amount its start was shifted forward.
                            if (!(stylerCategory.isOverlapped() || stylerCategory.isStacked())) {

                                double singleBarStep = stepLength / (double) chart.getSeriesMap().size();
                                stepLength -= (seriesCounter * singleBarStep);
                            }
                        }

                        // Draw the vertical line to the new y position, and the horizontal flat of the bar.
                        steppedPath.add(new Point2D.Double(xOffset, yTip));
                        steppedPath.add(new Point2D.Double(xOffset + stepLength, yTip));

                        // Add the corresponding centerline (or equivalent) to the return path
                        // Could be simplfied and removed for non-stacked graphs
                        steppedReturnPath.add(new Point2D.Double(xOffset, yCenter));
                        steppedReturnPath.add(new Point2D.Double(xOffset + stepLength, yCenter));

                        previousY = y;
                    }

                    // paint series
                    if (series.getCategorySeriesRenderStyle() == CategorySeriesRenderStyle.Bar) {

                        // paint bar
                        Path2D.Double path = new Path2D.Double();
                        path.moveTo(xOffset, yOffset);
                        path.lineTo(xOffset + barWidth, yOffset);
                        path.lineTo(xOffset + barWidth, zeroOffset);
                        path.lineTo(xOffset, zeroOffset);
                        path.closePath();

                        g.setColor(series.getFillColor());
                        g.fill(path);
                    } else if (CategorySeriesRenderStyle.Stick.equals(series.getCategorySeriesRenderStyle())) {
                        if (series.getLineStyle() != Theme.Series.NONE_STROKE) {
                            g.setColor(series.getLineColor());
                            g.setStroke(series.getLineStyle());
                            Shape line = new Line2D.Double(xOffset, zeroOffset, xOffset, yOffset);
                            g.draw(line);
                        }

                        // paint marker
                        if (series.getMarker() != null) {
                            g.setColor(series.getMarkerColor());

                            if (y <= 0) {
                                series.getMarker().paint(g, xOffset, zeroOffset, stylerCategory.getMarkerSize());
                            } else {
                                series.getMarker().paint(g, xOffset, yOffset, stylerCategory.getMarkerSize());
                            }
                        }
                    } else {
                        if (series.getCategorySeriesRenderStyle() == CategorySeriesRenderStyle.Line) {
                            if (series.getLineStyle() != Theme.Series.NONE_STROKE) {
                                if (previousX != -Double.MAX_VALUE && previousY != -Double.MAX_VALUE) {
                                    g.setColor(series.getLineColor());
                                    g.setStroke(series.getLineStyle());
                                    Shape line = new Line2D.Double(previousX, previousY, xOffset + barWidth / 2, yOffset);
                                    g.draw(line);
                                }
                            }
                        }
                        previousX = xOffset + barWidth / 2;
                        previousY = yOffset;

                        // paint marker
                        if (series.getMarker() != null) {
                            g.setColor(series.getMarkerColor());
                            series.getMarker().paint(g, previousX, previousY, stylerCategory.getMarkerSize());
                        }

                    }
                    // paint error bars
                    if (errorBars != null) {
                        double eb = ebItr.next().doubleValue();
                        // set error bar style
                        if (stylerCategory.isErrorBarsColorSeriesColor()) {
                            g.setColor(series.getLineColor());
                        } else {
                            g.setColor(stylerCategory.getErrorBarsColor());
                        }
                        g.setStroke(Theme.Strokes.ERROR_BARS);

                        // Top value
                        double errorBarLength = ((eb) / (yMax - yMin) * yTickSpace);
                        double topEBOffset = yOffset - errorBarLength;

                        // Bottom value
                        double bottomEBOffset = yOffset + errorBarLength;

                        // Draw it
                        double errorBarOffset = xOffset + barWidth / 2;
                        Shape line = new Line2D.Double(errorBarOffset, topEBOffset, errorBarOffset, bottomEBOffset);
                        g.draw(line);
                        line = new Line2D.Double(errorBarOffset - 3, bottomEBOffset, errorBarOffset + 3, bottomEBOffset);
                        g.draw(line);
                        line = new Line2D.Double(errorBarOffset - 3, topEBOffset, errorBarOffset + 3, topEBOffset);
                        g.draw(line);
                    }

                }
                // Final drawing of a steppedBar is done after the main loop,
                // as it continues on null and we may end up missing the final iteration.
                if (steppedPath != null && !steppedReturnPath.isEmpty()) {
                    drawStepBar(g, series, steppedPath, steppedReturnPath);
                }
                seriesCounter++;
            }
        }

        private void drawStepBarLine(Graphics2D g, S series, Path2D.Double path) {
            if (series.getLineColor() != null) {
                g.setColor(series.getLineColor());
                g.setStroke(series.getLineStyle());
                g.draw(path);
            }
        }

        private void drawStepBarFill(Graphics2D g, S series, Path2D.Double path) {
            if (series.getFillColor() != null) {
                g.setColor(series.getFillColor());
                g.fill(path);
            }
        }

        private void drawStepBar(Graphics2D g, S series, List<Point2D.Double> path, List<Point2D.Double> returnPath) {
            Collections.reverse(returnPath);
            returnPath.remove(returnPath.size() - 1);
            path.addAll(returnPath);
            Path2D.Double drawPath = new Path2D.Double();
            Point2D.Double startPoint = path.remove(0);
            drawPath.moveTo(startPoint.getX(), startPoint.getY());
            for (Point2D.Double currentPoint : path) {
                drawPath.lineTo(currentPoint.getX(), currentPoint.getY());
            }
            drawStepBarFill(g, series, drawPath);
            drawPath.reset();
            drawPath.moveTo(startPoint.getX(), startPoint.getY());
            List<Point2D.Double> linePath = path.subList(0, path.size() - returnPath.size() + 1);
            for (Point2D.Double currentPoint : linePath) {
                drawPath.lineTo(currentPoint.getX(), currentPoint.getY());
            }
            drawStepBarLine(g, series, drawPath);
        }
    }

    private static class ContentPlotCategoryLineAreaScatter<ST extends CategoryStyler, S extends CategorySeries> extends ContentPlot<ST, S> {

        private final ST categoryStyler;

        protected ContentPlotCategoryLineAreaScatter(Chart<ST, S> chart) {
            super(chart);
            this.categoryStyler = chart.getStyler();
        }

        @Override
        public void doPaint(Graphics2D g) {
            double xTickSpace = categoryStyler.getPlotContentSize() * getBounds().getWidth();
            double xLeftMargin = ((int) getBounds().getWidth() - xTickSpace) / 2.0;
            double yTickSpace = categoryStyler.getPlotContentSize() * getBounds().getHeight();
            double yTopMargin =((int) getBounds().getHeight() - yTickSpace) / 2.0;
            Map<String, S> seriesMap = chart.getSeriesMap();
            int numCategories = seriesMap.values().iterator().next().getXData().size();
            double gridStep = xTickSpace / numCategories;
            for (S series : seriesMap.values()) {
                if (!series.isEnabled()) {
                    continue;
                }
                Axis<?, ?> yAxis = chart.getYAxis(series.getYAxisGroup());
                double yMin = yAxis.getMin();
                double yMax = yAxis.getMax();
                if (categoryStyler.isYAxisLogarithmic()) {
                    yMin = Math.log10(yMin);
                    yMax = Math.log10(yMax);
                }
                Collection<? extends Number> yData = series.getYData();
                double previousX = -Double.MAX_VALUE;
                double previousY = -Double.MAX_VALUE;
                Iterator<? extends Number> yItr = yData.iterator();
                Iterator<? extends Number> ebItr = null;
                Collection<? extends Number> errorBars = series.getExtraValues();
                if (errorBars != null) {
                    ebItr = errorBars.iterator();
                }
                Path2D.Double path = null;
                int categoryCounter = 0;
                while (yItr.hasNext()) {
                    Number next = yItr.next();
                    if (next == null) {
                        // for area charts
                        closePath(g, path, previousX, yTopMargin);
                        path = null;
                        previousX = -Double.MAX_VALUE;
                        previousY = -Double.MAX_VALUE;
                        continue;
                    }
                    double yOrig = next.doubleValue();
                    double y;
                    if (categoryStyler.isYAxisLogarithmic()) {
                        y = Math.log10(yOrig);
                    } else {
                        y = yOrig;
                    }
                    double yTransform = getBounds().getHeight() - (yTopMargin + (y - yMin) / (yMax - yMin) * yTickSpace);
                    if (Math.abs(yMax - yMin) / 5 == 0.0) {
                        yTransform = getBounds().getHeight() / 2.0;
                    }
                    double xOffset = getBounds().getX() + xLeftMargin + categoryCounter++ * gridStep + gridStep / 2;
                    double yOffset = getBounds().getY() + yTransform;
                    if (CategorySeriesRenderStyle.Line.equals(series.getCategorySeriesRenderStyle()) ||
                            CategorySeriesRenderStyle.Area.equals(series.getCategorySeriesRenderStyle())) {
                        if (series.getLineStyle() != Theme.Series.NONE_STROKE) {
                            if (previousX != -Double.MAX_VALUE && previousY != -Double.MAX_VALUE) {
                                g.setColor(series.getLineColor());
                                g.setStroke(series.getLineStyle());
                                Shape line = new Line2D.Double(previousX, previousY, xOffset, yOffset);
                                g.draw(line);
                            }
                        }
                    }
                    if (CategorySeriesRenderStyle.Area.equals(series.getCategorySeriesRenderStyle())) {
                        if (previousX != -Double.MAX_VALUE && previousY != -Double.MAX_VALUE) {
                            g.setColor(series.getFillColor());
                            double yBottomOfArea = getBounds().getY() + getBounds().getHeight() - yTopMargin;
                            if (path == null) {
                                path = new Path2D.Double();
                                path.moveTo(previousX, yBottomOfArea);
                                path.lineTo(previousX, previousY);
                            }
                            path.lineTo(xOffset, yOffset);
                        }
                        if (xOffset < previousX) {
                            throw new RuntimeException("X-Data must be in ascending order for Area Charts");
                        }
                    }
                    if (CategorySeriesRenderStyle.Stick.equals(series.getCategorySeriesRenderStyle())) {
                        if (series.getLineStyle() != Theme.Series.NONE_STROKE) {
                            double yBottomOfArea = getBounds().getY() + getBounds().getHeight() - yTopMargin;
                            g.setColor(series.getLineColor());
                            g.setStroke(series.getLineStyle());
                            Shape line = new Line2D.Double(xOffset, yBottomOfArea, xOffset, yOffset);
                            g.draw(line);
                        }
                    }
                    previousX = xOffset;
                    previousY = yOffset;
                    if (series.getMarker() != null) {
                        g.setColor(series.getMarkerColor());
                        series.getMarker().paint(g, xOffset, yOffset, categoryStyler.getMarkerSize());
                    }
                    if (errorBars != null) {
                        double eb = ebItr.next().doubleValue();
                        if (categoryStyler.isErrorBarsColorSeriesColor()) {
                            g.setColor(series.getLineColor());
                        } else {
                            g.setColor(categoryStyler.getErrorBarsColor());
                        }
                        g.setStroke(Theme.Strokes.ERROR_BARS);
                        double topValue;
                        if (categoryStyler.isYAxisLogarithmic()) {
                            topValue = yOrig + eb;
                            topValue = Math.log10(topValue);
                        } else {
                            topValue = y + eb;
                        }
                        double topEBTransform = getBounds().getHeight() - (yTopMargin + (topValue - yMin) / (yMax - yMin) * yTickSpace);
                        double topEBOffset = getBounds().getY() + topEBTransform;
                        double bottomValue;
                        if (categoryStyler.isYAxisLogarithmic()) {
                            bottomValue = yOrig - eb;
                            bottomValue = Math.log10(bottomValue);
                        } else {
                            bottomValue = y - eb;
                        }
                        double bottomEBTransform = getBounds().getHeight() - (yTopMargin + (bottomValue - yMin) / (yMax - yMin) * yTickSpace);
                        double bottomEBOffset = getBounds().getY() + bottomEBTransform;
                        Shape line = new Line2D.Double(xOffset, topEBOffset, xOffset, bottomEBOffset);
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
}
