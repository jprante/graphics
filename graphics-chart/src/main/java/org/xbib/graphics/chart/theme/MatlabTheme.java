package org.xbib.graphics.chart.theme;

import org.xbib.graphics.chart.legend.LegendPosition;
import org.xbib.graphics.chart.pie.PieStyler.AnnotationType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.List;

public class MatlabTheme extends DefaultTheme {

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
    public List<Series.Marker> getSeriesMarkers() {
        return Matlab.SERIES_MARKERS;
    }

    @Override
    public List<BasicStroke> getSeriesLines() {
        return Matlab.SERIES_LINES;
    }

    @Override
    public List<Color> getSeriesColors() {
        return Matlab.SERIES_COLORS;
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
        return Colors.WHITE;
    }

    @Override
    public Color getChartTitleBoxBorderColor() {
        return Colors.WHITE;
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
        return Colors.BLACK;
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
        return Fonts.SANS_SERIF_PLAIN_12;
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
        return Fonts.SANS_SERIF_PLAIN_12;
    }

    @Override
    public int getAxisTickMarkLength() {
        return 5;
    }

    @Override
    public int getAxisTickPadding() {
        return 4;
    }

    @Override
    public int getPlotMargin() {
        return 3;
    }

    @Override
    public Color getAxisTickMarksColor() {
        return Colors.BLACK;
    }

    @Override
    public Stroke getAxisTickMarksStroke() {
        return Matlab.AXIS_TICKMARK;
    }

    @Override
    public Color getAxisTickLabelsColor() {
        return Colors.BLACK;
    }

    @Override
    public boolean isAxisTicksLineVisible() {
        return false;
    }

    @Override
    public boolean isAxisTicksMarksVisible() {
        return false;
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
        return Colors.BLACK;
    }

    @Override
    public boolean isPlotBorderVisible() {
        return true;
    }

    @Override
    public boolean isPlotTicksMarksVisible() {
        return true;
    }

    @Override
    public Color getPlotGridLinesColor() {
        return Colors.BLACK;
    }

    @Override
    public Stroke getPlotGridLinesStroke() {
        return Matlab.GRID_LINES;
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
        return AnnotationType.Label;
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
