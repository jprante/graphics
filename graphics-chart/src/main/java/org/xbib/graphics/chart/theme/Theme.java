package org.xbib.graphics.chart.theme;

import org.xbib.graphics.chart.legend.LegendPosition;
import org.xbib.graphics.chart.pie.PieStyler.AnnotationType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface Theme {

    interface Default {

        List<Color> SERIES_COLORS = Arrays.asList(
                new Color(141, 211, 199),
                new Color(255, 255, 179),
                new Color(190, 186, 218),
                new Color(251, 128, 114),
                new Color(128, 177, 211),
                new Color(253, 180, 98),
                new Color(179, 222, 105),
                new Color(252, 205, 229),
                new Color(217, 217, 217),
                new Color(188, 128, 189),
                new Color(204, 235, 197),
                new Color(255, 237, 111)
        );

        List<Series.Marker> SERIES_MARKERS = Arrays.asList(
                Series.CIRCLE_MARKER,
                Series.SQUARE_MARKER,
                Series.DIAMOND_MARKER,
                Series.TRIANGLE_UP_MARKER,
                Series.TRIANGLE_DOWN_MARKER,
                Series.CROSS_MARKER);

        List<BasicStroke> SERIES_LINES = Arrays.asList(
                Series.SOLID_STROKE,
                Series.DOT_DOT_STROKE,
                Series.DASH_DASH_STROKE,
                Series.DASH_DOT_STROKE);

        BasicStroke AXIS_TICKMARK = new BasicStroke(1.0f);

        BasicStroke GRID_LINES = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[] {3.0f, 5.0f}, 0.0f);
    }

    interface Standard {

        Color BLUE = new Color(0, 55, 255, 180);
        Color ORANGE = new Color(255, 172, 0, 180);
        Color PURPLE = new Color(128, 0, 255, 180);
        Color GREEN = new Color(0, 205, 0, 180);
        Color RED = new Color(205, 0, 0, 180);
        Color YELLOW = new Color(255, 215, 0, 180);
        Color MAGENTA = new Color(255, 0, 255, 180);
        Color PINK = new Color(255, 166, 201, 180);
        Color LIGHT_GREY = new Color(207, 207, 207, 180);
        Color CYAN = new Color(0, 255, 255, 180);
        Color BROWN = new Color(102, 56, 10, 180);
        Color BLACK = new Color(0, 0, 0, 180);

        List<Color> SERIES_COLORS = Arrays.asList(
                BLUE, ORANGE, PURPLE, GREEN, RED, YELLOW, MAGENTA, PINK, LIGHT_GREY, CYAN, BROWN, BLACK
        );

        List<Series.Marker> SERIES_MARKERS = Arrays.asList(
                Series.CIRCLE_MARKER,
                Series.DIAMOND_MARKER,
                Series.SQUARE_MARKER,
                Series.TRIANGLE_DOWN_MARKER,
                Series.TRIANGLE_UP_MARKER);


        List<BasicStroke> SERIES_LINES = Arrays.asList(
                Series.SOLID_STROKE,
                Series.DASH_DOT_STROKE,
                Series.DASH_DASH_STROKE,
                Series.DOT_DOT_STROKE);

        BasicStroke AXIS_TICKMARK = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[]{3.0f, 0.0f}, 0.0f);

        BasicStroke GRID_LINES = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[]{3.0f, 3.0f}, 0.0f);
    }

    interface GGPlot2 {
        List<BasicStroke> SERIES_LINES =
                Arrays.asList(Series.SOLID_STROKE, Series.DOT_DOT_STROKE, Series.DASH_DASH_STROKE);

        List<Series.Marker> SERIES_MARKERS = Arrays.asList(Series.CIRCLE_MARKER,
                Series.DIAMOND_MARKER);

        Color RED = new Color(248, 118, 109, 255);
        Color YELLOW_GREEN = new Color(163, 165, 0, 255);
        Color GREEN = new Color(0, 191, 125, 255);
        Color BLUE = new Color(0, 176, 246, 255);
        Color PURPLE = new Color(231, 107, 243, 255);

        List<Color> SERIES_COLORS = Arrays.asList(RED, YELLOW_GREEN, GREEN, BLUE, PURPLE);

        BasicStroke AXIS_TICKMARK = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[]{3.0f, 0.0f}, 0.0f);

        BasicStroke GRID_LINES = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[]{3.0f, 0.0f}, 0.0f);
    }

    interface Matlab {

        List<BasicStroke> SERIES_LINES =
                Arrays.asList(Series.SOLID_STROKE/*, Series.DASH_DASH_STROKE, Series.DOT_DOT_STROKE*/);

        List<Series.Marker> SERIES_MARKERS =
                Collections.singletonList(Series.NONE_MARKER);

        Color BLUE = new Color(0, 0, 255, 255);
        Color GREEN = new Color(0, 128, 0, 255);
        Color RED = new Color(255, 0, 0, 255);
        Color TURQUOISE = new Color(0, 191, 191, 255);
        Color MAGENTA = new Color(191, 0, 191, 255);
        Color YELLOW = new Color(191, 191, 0, 255);
        Color DARK_GREY = new Color(64, 64, 64, 255);

        List<Color> SERIES_COLORS =
                Arrays.asList(BLUE, GREEN, RED, TURQUOISE, MAGENTA, YELLOW, DARK_GREY);

        BasicStroke AXIS_TICKMARK = new BasicStroke(.5f);
                //new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[]{3.0f, 0.0f}, 0.0f);

        BasicStroke GRID_LINES = new BasicStroke(.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {1f, 3.0f}, 0.0f);

        //new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[]{1.0f, 2.0f}, 0.0f);
    }

    interface PrinterFriendly {
        Color RED = new Color(228, 26, 28, 180);
        Color GREEN = new Color(55, 126, 184, 180);
        Color BLUE = new Color(77, 175, 74, 180);
        Color PURPLE = new Color(152, 78, 163, 180);
        Color ORANGE = new Color(255, 127, 0, 180);
        Color YELLOW = new Color(255, 255, 51, 180);
        Color BROWN = new Color(166, 86, 40, 180);
        Color PINK = new Color(247, 129, 191, 180);
        List<Color> SERIES_COLORS  =
                Arrays.asList(RED, GREEN, BLUE, PURPLE, ORANGE);
    }

    interface ColorBlindFriendly {
        Color BLACK = new Color(0, 0, 0, 255);
        Color ORANGE = new Color(230, 159, 0, 255);
        Color SKY_BLUE = new Color(86, 180, 233, 255);
        Color BLUISH_GREEN = new Color(0, 158, 115, 255);
        Color YELLOW = new Color(240, 228, 66, 255);
        Color BLUE = new Color(0, 114, 178, 255);
        Color VERMILLION = new Color(213, 94, 0, 255);
        Color REDDISH_PURPLE = new Color(204, 121, 167, 255);

        List<Color> SERIES_COLORS =
                Arrays.asList(BLACK, ORANGE, SKY_BLUE, BLUISH_GREEN, YELLOW, BLUE, VERMILLION, REDDISH_PURPLE);
    }

    interface Colors {
        Color BLACK = new Color(0, 0, 0);
        Color DARK_GREY = new Color(130, 130, 130);
        Color GREY = new Color(210, 210, 210);
        Color LIGHT_GREY = new Color(230, 230, 230);
        Color WHITE = new Color(255, 255, 255);
    }

    interface Fonts {
        Font SANS_SERIF_PLAIN_10 = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        Font SANS_SERIF_PLAIN_11 = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        Font SANS_SERIF_PLAIN_12 = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        Font SANS_SERIF_PLAIN_14 = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        Font SANS_SERIF_PLAIN_15 = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
        Font SANS_SERIF_BOLD_12 = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        Font SANS_SERIF_BOLD_13 = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        Font SANS_SERIF_BOLD_14 = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    }

    interface Strokes {
        BasicStroke TITLE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        BasicStroke LEGEND = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[] {3.0f, 0.0f}, 0.0f);
        BasicStroke ERROR_BARS = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        BasicStroke PIE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    }

    interface Series {

        BasicStroke NONE_STROKE = new BasicStroke();
        BasicStroke SOLID_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        BasicStroke DASH_DOT_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{3.0f, 1.0f}, 0.0f);
        BasicStroke DASH_DASH_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{3.0f, 3.0f}, 0.0f);
        BasicStroke DOT_DOT_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[]{2.0f}, 0.0f);

        abstract class Marker {

            protected BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);

            public abstract void paint(Graphics2D g, double xOffset, double yOffset, int markerSize);

        }

        class Circle extends Marker {

            @Override
            public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                g.setStroke(stroke);
                double halfSize = (double) markerSize / 2;
                Shape circle = new Ellipse2D.Double(xOffset - halfSize, yOffset - halfSize, markerSize, markerSize);
                g.fill(circle);
            }
        }

        class Cross extends Marker {

            @Override
            public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                g.setStroke(stroke);
                double halfSize = (double) markerSize / 2;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(xOffset - halfSize, yOffset - halfSize);
                path.lineTo(xOffset + halfSize, yOffset + halfSize);
                path.moveTo(xOffset - halfSize, yOffset + halfSize);
                path.lineTo(xOffset + halfSize, yOffset - halfSize);
                g.draw(path);
            }
        }

        class Diamond extends Marker {

            @Override
            public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                g.setStroke(stroke);
                double diamondHalfSize = (double) markerSize / 2 * 1.3;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(xOffset - diamondHalfSize, yOffset);
                path.lineTo(xOffset, yOffset - diamondHalfSize);
                path.lineTo(xOffset + diamondHalfSize, yOffset);
                path.lineTo(xOffset, yOffset + diamondHalfSize);
                path.closePath();
                g.fill(path);
            }
        }

        class None extends Marker {

            @Override
            public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                // do nothing!
            }
        }

        class Square extends Marker {

            @Override
            public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                g.setStroke(stroke);
                double halfSize = (double) markerSize / 2;
                Shape square = new Rectangle2D.Double(xOffset - halfSize, yOffset - halfSize, markerSize, markerSize);
                g.fill(square);
            }
        }

        class TriangleDown extends Marker {

            @Override
            public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                g.setStroke(stroke);
                double halfSize = (double) markerSize / 2;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(xOffset - halfSize, 1 + yOffset - halfSize);
                path.lineTo(xOffset, 1 + yOffset - halfSize + markerSize);
                path.lineTo(xOffset - halfSize + markerSize, 1 + yOffset - halfSize);
                path.closePath();
                g.fill(path);
            }
        }

        class TriangleUp extends Marker {

            @Override
            public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                g.setStroke(stroke);
                double halfSize = (double) markerSize / 2;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(xOffset - halfSize, yOffset - halfSize + markerSize - 1);
                path.lineTo(xOffset - halfSize + markerSize, yOffset - halfSize + markerSize - 1);
                path.lineTo(xOffset, yOffset - halfSize - 1);
                path.closePath();
                g.fill(path);
            }
        }

        Marker NONE_MARKER = new None();
        Marker CIRCLE_MARKER = new Circle();
        Marker CROSS_MARKER = new Cross();
        Marker DIAMOND_MARKER = new Diamond();
        Marker SQUARE_MARKER = new Square();
        Marker TRIANGLE_DOWN_MARKER = new TriangleDown();
        Marker TRIANGLE_UP_MARKER = new TriangleUp();
    }

    Font getBaseFont();

    Color getChartBackgroundColor();

    Color getChartFontColor();

    int getChartPadding();

    Font getChartTitleFont();

    boolean isChartTitleVisible();

    boolean isChartTitleBoxVisible();

    Color getChartTitleBoxBackgroundColor();

    Color getChartTitleBoxBorderColor();

    int getChartTitlePadding();

    Font getLegendFont();

    boolean isLegendVisible();

    Color getLegendBackgroundColor();

    Color getLegendBorderColor();

    int getLegendPadding();

    int getLegendSeriesLineLength();

    LegendPosition getLegendPosition();

    boolean isXAxisTitleVisible();

    boolean isYAxisTitleVisible();

    Font getAxisTitleFont();

    boolean isXAxisTicksVisible();

    boolean isYAxisTicksVisible();

    Font getAxisTickLabelsFont();

    int getAxisTickMarkLength();

    int getAxisTickPadding();

    Color getAxisTickMarksColor();

    Stroke getAxisTickMarksStroke();

    Color getAxisTickLabelsColor();

    boolean isAxisTicksLineVisible();

    boolean isAxisTicksMarksVisible();

    int getAxisTitlePadding();

    int getXAxisTickMarkSpacingHint();

    int getYAxisTickMarkSpacingHint();

    boolean isPlotGridVerticalLinesVisible();

    boolean isPlotGridHorizontalLinesVisible();

    Color getPlotBackgroundColor();

    Color getPlotBorderColor();

    boolean isPlotBorderVisible();

    Color getPlotGridLinesColor();

    Stroke getPlotGridLinesStroke();

    boolean isPlotTicksMarksVisible();

    double getPlotContentSize();

    int getPlotMargin();

    double getAvailableSpaceFill();

    boolean isOverlapped();

    boolean isCircular();

    Font getPieFont();

    double getAnnotationDistance();

    AnnotationType getAnnotationType();

    boolean isDrawAllAnnotations();

    double getDonutThickness();

    boolean isSumVisible();

    Font getSumFont();

    int getMarkerSize();

    Color getErrorBarsColor();

    boolean isErrorBarsColorSeriesColor();

    Font getAnnotationFont();

    List<Series.Marker> getSeriesMarkers();

    List<BasicStroke> getSeriesLines();

    List<Color> getSeriesColors();

}
