package org.xbib.graphics.chart.theme;

import org.xbib.graphics.chart.legend.LegendPosition;
import org.xbib.graphics.chart.pie.PieStyler.AnnotationType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.List;

public class GGPlot2Theme extends DefaultTheme {

    @Override
    public Color getChartBackgroundColor() {
        return Theme.Colors.WHITE;
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
    public List<Series.Marker> getSeriesMarkers() {
        return GGPlot2.SERIES_MARKERS;
    }

    @Override
    public List<BasicStroke> getSeriesLines() {
        return GGPlot2.SERIES_LINES;
    }

    @Override
    public List<Color> getSeriesColors() {
        return GGPlot2.SERIES_COLORS;
    }

    @Override
    public Font getChartTitleFont() {
        return Fonts.SANS_SERIF_PLAIN_14;
    }

    @Override
    public boolean isChartTitleVisible() {
        return true;
    }

    @Override
    public boolean isChartTitleBoxVisible() {
        return true;
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
        return Fonts.SANS_SERIF_PLAIN_14;
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
        return Colors.WHITE;
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
        return Fonts.SANS_SERIF_PLAIN_14;
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
        return Fonts.SANS_SERIF_BOLD_13;
    }

    @Override
    public int getAxisTickMarkLength() {
        return 8;
    }

    @Override
    public int getAxisTickPadding() {
        return 5;
    }

    @Override
    public int getPlotMargin() {
        return 0;
    }

    @Override
    public boolean isAxisTicksLineVisible() {
        return false;
    }

    @Override
    public boolean isAxisTicksMarksVisible() {
        return true;
    }

    @Override
    public Color getAxisTickMarksColor() {
        return Colors.DARK_GREY;
    }

    @Override
    public Stroke getAxisTickMarksStroke() {
        return GGPlot2.AXIS_TICKMARK;
    }

    @Override
    public Color getAxisTickLabelsColor() {
        return Colors.DARK_GREY;
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
        return Colors.LIGHT_GREY;
    }

    @Override
    public Color getPlotBorderColor() {
        return Colors.WHITE;
    }

    @Override
    public boolean isPlotBorderVisible() {
        return false;
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
        return GGPlot2.GRID_LINES;
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
        return AnnotationType.LabelAndPercentage;
    }

    @Override
    public int getMarkerSize() {
        return 8;
    }

    @Override
    public Color getErrorBarsColor() {
        return Colors.DARK_GREY;
    }

    @Override
    public boolean isErrorBarsColorSeriesColor() {
        return false;
    }
}
