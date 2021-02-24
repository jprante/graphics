package org.xbib.graphics.chart.pie;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.plot.CircularPlot;
import org.xbib.graphics.chart.plot.ContentPlot;
import org.xbib.graphics.chart.plot.SurfacePlot;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.SeriesColorMarkerLineStyle;
import org.xbib.graphics.chart.style.SeriesColorMarkerLineStyleCycler;
import org.xbib.graphics.chart.theme.Theme;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Map;

public class PieChart extends Chart<PieStyler, PieSeries> {

    public PieChart(int width, int height) {
        super(width, height, new PieStyler());
        plot = new PiePlot<>(this);
        legend = new PieLegend<>(this);
    }

    public PieChart(int width, int height, Theme theme) {
        this(width, height);
        styler.setTheme(theme);
    }

    public PieChart(PieChartBuilder chartBuilder) {
        this(chartBuilder.getWidth(), chartBuilder.getHeight(), chartBuilder.getTheme());
        setTitle(chartBuilder.getTitle());
    }

    public PieSeries addSeries(String seriesName, Number value) {
        PieSeries series = new PieSeries(seriesName, value);
        if (seriesMap.containsKey(seriesName)) {
            throw new IllegalArgumentException("Series name >" + seriesName + "< has already been used. Use unique names for each series");
        }
        seriesMap.put(seriesName, series);
        return series;
    }

    @Override
    public void paint(Graphics2D g, int width, int height) {
        setWidth(width);
        setHeight(height);
        for (PieSeries pieSeries : getSeriesMap().values()) {
            PieSeriesRenderStyle seriesType = pieSeries.getPieSeriesRenderStyle();
            if (seriesType == null) {
                pieSeries.setPieSeriesRenderStyle(getStyler().getDefaultSeriesRenderStyle());
            }
        }
        setSeriesStyles();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(styler.getChartBackgroundColor());
        Shape rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.fill(rect);
        plot.paint(g);
        chartTitle.paint(g);
        legend.paint(g);
    }

    public void setSeriesStyles() {
        SeriesColorMarkerLineStyleCycler seriesColorMarkerLineStyleCycler =
                new SeriesColorMarkerLineStyleCycler(getStyler().getSeriesColors(),
                        getStyler().getSeriesMarkers(),
                        getStyler().getSeriesLines());
        for (Series series : getSeriesMap().values()) {
            SeriesColorMarkerLineStyle seriesColorMarkerLineStyle =
                    seriesColorMarkerLineStyleCycler.getNextSeriesColorMarkerLineStyle();
            if (series.getFillColor() == null) { // wasn't set manually
                series.setFillColor(seriesColorMarkerLineStyle.getColor());
            }
        }
    }

    private static class PiePlot<ST extends PieStyler, S extends PieSeries> extends CircularPlot<ST, S> {

        private ContentPlot<ST, S> contentPlot;

        private SurfacePlot<ST, S> surfacePlot;

        private PiePlot(Chart<ST, S> chart) {
            super(chart);
        }

        protected void initContentAndSurface(Chart<ST, S> chart) {
            this.contentPlot = new ContentPlotPie<>(chart);
            this.surfacePlot = new SurfacePlotPie<>(chart);
        }

        @Override
        public void paint(Graphics2D g) {
            super.paint(g);
            surfacePlot.paint(g);
            if (chart.getSeriesMap().isEmpty()) {
                return;
            }
            contentPlot.paint(g);
        }
    }

    private static class ContentPlotPie<ST extends PieStyler, S extends PieSeries> extends ContentPlot<ST, S> {

        private final PieStyler pieStyler;

        private final DecimalFormat df = new DecimalFormat("#.0");

        private ContentPlotPie(Chart<ST, S> chart) {
            super(chart);
            pieStyler = chart.getStyler();
        }

        @Override
        public void doPaint(Graphics2D g) {
            double pieFillPercentage = pieStyler.getPlotContentSize();
            double halfBorderPercentage = (1 - pieFillPercentage) / 2.0;
            double width = pieStyler.isCircular() ?
                    Math.min(getBounds().getWidth(), getBounds().getHeight()) : getBounds().getWidth();
            double height = pieStyler.isCircular() ?
                    Math.min(getBounds().getWidth(), getBounds().getHeight()) : getBounds().getHeight();
            Rectangle2D pieBounds = new Rectangle2D.Double(
                    getBounds().getX()
                            + getBounds().getWidth() / 2
                            - width / 2
                            + halfBorderPercentage * width,
                    getBounds().getY()
                            + getBounds().getHeight() / 2
                            - height / 2
                            + halfBorderPercentage * height,
                    width * pieFillPercentage,
                    height * pieFillPercentage);
            double total = 0.0;
            Map<String, S> map = chart.getSeriesMap();
            for (S series : map.values()) {
                if (!series.isEnabled()) {
                    continue;
                }
                total += series.getValue().doubleValue();
            }
            if (pieStyler.isSumVisible()) {
                DecimalFormat totalDf =
                        (pieStyler.getDecimalPattern() == null)
                                ? df
                                : new DecimalFormat(pieStyler.getDecimalPattern());

                String annotation = totalDf.format(total);

                TextLayout textLayout =
                        new TextLayout(
                                annotation, pieStyler.getSumFont(), new FontRenderContext(null, true, false));
                Shape shape = textLayout.getOutline(null);
                g.setColor(pieStyler.getChartFontColor());

                // compute center
                Rectangle2D annotationRectangle = textLayout.getBounds();
                double xCenter =
                        pieBounds.getX() + pieBounds.getWidth() / 2 - annotationRectangle.getWidth() / 2;
                double yCenter =
                        pieBounds.getY() + pieBounds.getHeight() / 2 + annotationRectangle.getHeight() / 2;

                // set text
                AffineTransform orig = g.getTransform();
                AffineTransform at = new AffineTransform();
                at.translate(xCenter, yCenter);
                g.transform(at);
                g.fill(shape);
                g.setTransform(orig);
            }
            double startAngle = pieStyler.getStartAngleInDegrees() + 90;
            map = chart.getSeriesMap();
            for (S series : map.values()) {
                if (!series.isEnabled()) {
                    continue;
                }
                Number y = series.getValue();
                // draw slice/donut
                double arcAngle = (y.doubleValue() * 360 / total);
                g.setColor(series.getFillColor());
                // slice
                if (PieSeriesRenderStyle.Pie == series.getPieSeriesRenderStyle()) {
                    Arc2D.Double pieShape = new Arc2D.Double(
                            pieBounds.getX(),
                            pieBounds.getY(),
                            pieBounds.getWidth(),
                            pieBounds.getHeight(),
                            startAngle,
                            arcAngle,
                            Arc2D.PIE);
                    g.fill(pieShape);
                    g.setColor(pieStyler.getPlotBackgroundColor());
                    g.draw(pieShape);
                }
                else {
                    Shape donutSlice =
                            getDonutSliceShape(pieBounds, pieStyler.getDonutThickness(), startAngle, arcAngle);
                    g.fill(donutSlice);
                    g.setColor(pieStyler.getPlotBackgroundColor());
                    g.draw(donutSlice);
                }
                if (pieStyler.hasAnnotations()) {
                    String annotation = "";
                    if (pieStyler.getAnnotationType() == PieStyler.AnnotationType.Value) {
                        if (pieStyler.getDecimalPattern() != null) {
                            DecimalFormat df = new DecimalFormat(pieStyler.getDecimalPattern());
                            annotation = df.format(y);
                        } else {
                            annotation = y.toString();
                        }
                    } else if (pieStyler.getAnnotationType() == PieStyler.AnnotationType.Label) {
                        annotation = series.getName();
                    } else if (pieStyler.getAnnotationType() == PieStyler.AnnotationType.LabelAndPercentage) {
                        double percentage = y.doubleValue() / total * 100;
                        annotation = series.getName() + " (" + df.format(percentage) + "%)";
                    } else if (pieStyler.getAnnotationType() == PieStyler.AnnotationType.Percentage) {
                        double percentage = y.doubleValue() / total * 100;
                        annotation = df.format(percentage) + "%";
                    }
                    TextLayout textLayout = new TextLayout(annotation,
                            pieStyler.getAnnotationsFont(),
                            new FontRenderContext(null, true, false));
                    Rectangle2D annotationRectangle = textLayout.getBounds();
                    double xCenter =
                            pieBounds.getX() + pieBounds.getWidth() / 2 - annotationRectangle.getWidth() / 2;
                    double yCenter =
                            pieBounds.getY() + pieBounds.getHeight() / 2 + annotationRectangle.getHeight() / 2;
                    double angle = (arcAngle + startAngle) - arcAngle / 2;
                    double xOffset = xCenter + Math.cos(Math.toRadians(angle))
                            * (pieBounds.getWidth() / 2 * pieStyler.getAnnotationDistance());
                    double yOffset = yCenter - Math.sin(Math.toRadians(angle))
                            * (pieBounds.getHeight() / 2 * pieStyler.getAnnotationDistance());

                    // get annotation width
                    Shape shape = textLayout.getOutline(null);
                    Rectangle2D annotationBounds = shape.getBounds2D();
                    double annotationWidth = annotationBounds.getWidth();
                    double annotationHeight = annotationBounds.getHeight();

                    // get slice area
                    double xOffset1 = xCenter
                            + Math.cos(Math.toRadians(startAngle))
                            * (pieBounds.getWidth() / 2 * pieStyler.getAnnotationDistance());
                    double yOffset1 = yCenter
                            - Math.sin(Math.toRadians(startAngle))
                            * (pieBounds.getHeight() / 2 * pieStyler.getAnnotationDistance());
                    double xOffset2 = xCenter
                            + Math.cos(Math.toRadians((arcAngle + startAngle)))
                            * (pieBounds.getWidth() / 2 * pieStyler.getAnnotationDistance());
                    double yOffset2 = yCenter
                            - Math.sin(Math.toRadians((arcAngle + startAngle)))
                            * (pieBounds.getHeight() / 2 * pieStyler.getAnnotationDistance());
                    double xDiff = Math.abs(xOffset1 - xOffset2);
                    double yDiff = Math.abs(yOffset1 - yOffset2);
                    boolean annotationWillFit = false;
                    if (xDiff >= yDiff) { // assume more vertically orientated slice
                        if (annotationWidth < xDiff) {
                            annotationWillFit = true;
                        }
                    } else if (xDiff <= yDiff) { // assume more horizontally orientated slice
                        if (annotationHeight < yDiff) {
                            annotationWillFit = true;
                        }
                    }
                    if (pieStyler.isDrawAllAnnotations() || annotationWillFit) {
                        g.setColor(pieStyler.getChartFontColor());
                        g.setFont(pieStyler.getAnnotationsFont());
                        AffineTransform orig = g.getTransform();
                        AffineTransform at = new AffineTransform();
                        if (pieStyler.getAnnotationDistance() <= 1.0) {
                            at.translate(xOffset, yOffset);
                        }
                        else {
                            xCenter = pieBounds.getX() + pieBounds.getWidth() / 2;
                            yCenter = pieBounds.getY() + pieBounds.getHeight() / 2;
                            double endPoint = (3.0 - pieStyler.getAnnotationDistance());
                            double xOffsetStart = xCenter + Math.cos(Math.toRadians(angle)) * (pieBounds.getWidth() / 2.01);
                            double xOffsetEnd = xCenter + Math.cos(Math.toRadians(angle)) * (pieBounds.getWidth() / endPoint);
                            double yOffsetStart = yCenter - Math.sin(Math.toRadians(angle)) * (pieBounds.getHeight() / 2.01);
                            double yOffsetEnd = yCenter - Math.sin(Math.toRadians(angle)) * (pieBounds.getHeight() / endPoint);
                            g.setStroke(Theme.Strokes.PIE);
                            Shape line = new Line2D.Double(xOffsetStart, yOffsetStart, xOffsetEnd, yOffsetEnd);
                            g.draw(line);
                            at.translate(xOffset - Math.sin(Math.toRadians(angle - 90)) * annotationWidth / 2 + 3, yOffset);
                        }
                        g.transform(at);
                        g.fill(shape);
                        g.setTransform(orig);
                    }
                }
                startAngle += arcAngle;
            }
        }

        Shape getDonutSliceShape(Rectangle2D pieBounds, double thickness, double start, double extent) {
            thickness = thickness / 2;
            GeneralPath generalPath = new GeneralPath();
            GeneralPath dummy = new GeneralPath();
            double x = pieBounds.getX();
            double y = pieBounds.getY();
            double width = pieBounds.getWidth();
            double height = pieBounds.getHeight();
            Shape outer = new Arc2D.Double(x, y, width, height, start, extent, Arc2D.OPEN);
            double wt = width * thickness;
            double ht = height * thickness;
            Shape inner = new Arc2D.Double(x + wt, y + ht, width - 2 * wt, height - 2 * ht, start + extent, -extent, Arc2D.OPEN);
            generalPath.append(outer, false);
            dummy.append(new Arc2D.Double(x + wt, y + ht, width - 2 * wt, height - 2 * ht, start, extent, Arc2D.OPEN),
                    false);
            Point2D point = dummy.getCurrentPoint();
            if (point != null) {
                generalPath.lineTo(point.getX(), point.getY());
            }
            generalPath.append(inner, false);
            dummy.append(new Arc2D.Double(x, y, width, height, start + extent, -extent, Arc2D.OPEN), false);
            point = dummy.getCurrentPoint();
            generalPath.lineTo(point.getX(), point.getY());
            return generalPath;
        }
    }

    private static class SurfacePlotPie<ST extends PieStyler, S extends PieSeries> extends SurfacePlot<ST, S> {

        private final ST pieStyler;

        private SurfacePlotPie(Chart<ST, S> chart) {
            super(chart);
            this.pieStyler = chart.getStyler();
        }

        @Override
        public void paint(Graphics2D g) {
            Rectangle2D bounds = getBounds();
            if (bounds != null) {
                Shape rect = new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
                g.setColor(pieStyler.getPlotBackgroundColor());
                g.fill(rect);
                if (pieStyler.isPlotBorderVisible()) {
                    g.setColor(pieStyler.getPlotBorderColor());
                    g.draw(rect);
                }
            }
        }
    }
}
