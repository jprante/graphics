package org.xbib.graphics.chart.theme;

import org.xbib.graphics.chart.legend.LegendPosition;
import org.xbib.graphics.chart.pie.PieStyler.AnnotationType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.List;

public class DefaultTheme implements Theme {

    @Override
    public Font getBaseFont() {
        return Fonts.SANS_SERIF_PLAIN_10;
    }

    @Override
    public boolean isDrawAllAnnotations() {
        return false;
    }

    @Override
    public double getDonutThickness() {
        return .33;
    }

    @Override
    public boolean isSumVisible() {
        return false;
    }

    @Override
    public Font getSumFont() {
        return getAnnotationFont();
    }

    @Override
    public Font getAnnotationFont() {
        return getPieFont().deriveFont(12f);
    }

    @Override
    public Color getChartBackgroundColor() {
        return Colors.WHITE;
    }

    @Override
    public Color getChartFontColor() {
        return Colors.BLACK;
    }

    @Override
    public int getChartPadding() {
        return 10;
    }

    @Override
    public List<Color> getSeriesColors() {
        return PrinterFriendly.SERIES_COLORS;
    }

    @Override
    public List<Series.Marker> getSeriesMarkers() {
        return Default.SERIES_MARKERS;
    }

    @Override
    public List<BasicStroke> getSeriesLines() {
        return Default.SERIES_LINES;
    }

    @Override
    public Font getChartTitleFont() {
        return Fonts.SANS_SERIF_BOLD_14;
    }

    @Override
    public boolean isChartTitleVisible() {
        return true;
    }

    @Override
    public boolean isChartTitleBoxVisible() {
        return false;
    }

    @Override
    public Color getChartTitleBoxBackgroundColor() {
        return Colors.GREY;
    }

    @Override
    public Color getChartTitleBoxBorderColor() {
        return Colors.GREY;
    }

    @Override
    public int getChartTitlePadding() {
        return 5;
    }

    @Override
    public Font getLegendFont() {
        return Fonts.SANS_SERIF_PLAIN_11;
    }

    @Override
    public boolean isLegendVisible() {
        return true;
    }

    @Override
    public Color getLegendBackgroundColor() {
        return Colors.WHITE;
    }

    @Override
    public Color getLegendBorderColor() {
        return Colors.DARK_GREY;
    }

    @Override
    public int getLegendPadding() {
        return 10;
    }

    @Override
    public int getLegendSeriesLineLength() {
        return 24;
    }

    @Override
    public LegendPosition getLegendPosition() {
        return LegendPosition.OutsideE;
    }

    @Override
    public boolean isXAxisTitleVisible() {
        return true;
    }

    @Override
    public boolean isYAxisTitleVisible() {
        return true;
    }

    @Override
    public Font getAxisTitleFont() {
        return Fonts.SANS_SERIF_BOLD_12;
    }

    @Override
    public boolean isXAxisTicksVisible() {
        return true;
    }

    @Override
    public boolean isYAxisTicksVisible() {
        return true;
    }

    @Override
    public Font getAxisTickLabelsFont() {
        return Fonts.SANS_SERIF_BOLD_12;
    }

    @Override
    public int getAxisTickMarkLength() {
        return 3;
    }

    @Override
    public int getAxisTickPadding() {
        return 4;
    }

    @Override
    public int getPlotMargin() {
        return 4;
    }

    @Override
    public Color getAxisTickMarksColor() {
        return Colors.DARK_GREY;
    }

    @Override
    public Stroke getAxisTickMarksStroke() {
        return Standard.AXIS_TICKMARK;
    }

    @Override
    public Color getAxisTickLabelsColor() {
        return Colors.BLACK;
    }

    @Override
    public boolean isAxisTicksLineVisible() {
        return true;
    }

    @Override
    public boolean isAxisTicksMarksVisible() {
        return true;
    }

    @Override
    public int getAxisTitlePadding() {
        return 10;
    }

    @Override
    public int getXAxisTickMarkSpacingHint() {
        return 74;
    }

    @Override
    public int getYAxisTickMarkSpacingHint() {
        return 44;
    }

    @Override
    public boolean isPlotGridVerticalLinesVisible() {
        return true;
    }

    @Override
    public boolean isPlotGridHorizontalLinesVisible() {
        return true;
    }

    @Override
    public Color getPlotBackgroundColor() {
        return Colors.WHITE;
    }

    @Override
    public Color getPlotBorderColor() {
        return Colors.DARK_GREY;
    }

    @Override
    public boolean isPlotBorderVisible() {
        return true;
    }

    @Override
    public boolean isPlotTicksMarksVisible() {
        return false;
    }

    @Override
    public Color getPlotGridLinesColor() {
        return Colors.WHITE;
    }

    @Override
    public Stroke getPlotGridLinesStroke() {
        return Standard.GRID_LINES;
    }

    @Override
    public double getPlotContentSize() {
        return .92;
    }

    @Override
    public double getAvailableSpaceFill() {
        return 0.9;
    }

    @Override
    public boolean isOverlapped() {
        return false;
    }

    @Override
    public boolean isCircular() {
        return true;
    }

    @Override
    public Font getPieFont() {
        return Fonts.SANS_SERIF_PLAIN_15;
    }

    @Override
    public double getAnnotationDistance() {
        return .67;
    }

    @Override
    public AnnotationType getAnnotationType() {
        return AnnotationType.Percentage;
    }

    @Override
    public int getMarkerSize() {
        return 8;
    }

    @Override
    public Color getErrorBarsColor() {
        return Colors.BLACK;
    }

    @Override
    public boolean isErrorBarsColorSeriesColor() {
        return false;
    }
}
