package org.xbib.graphics.chart.demo;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.axis.TextAlignment;
import org.xbib.graphics.chart.theme.Theme;
import org.xbib.graphics.chart.legend.LegendPosition;
import org.xbib.graphics.chart.xy.XYChart;
import org.xbib.graphics.chart.xy.XYChartBuilder;
import org.xbib.graphics.chart.xy.XYSeries;
import org.xbib.graphics.chart.xy.XYSeriesRenderStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AreaChartTest {

    @Test
    public void testAreaChart1() throws IOException {
        XYChart chart =
                new XYChartBuilder()
                        .width(800)
                        .height(600)
                        .title(getClass().getSimpleName())
                        .xAxisTitle("X")
                        .yAxisTitle("Y")
                        .build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
        chart.getStyler().setAxisTitlesVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        chart.getStyler().setSeriesColors(Theme.PrinterFriendly.SERIES_COLORS);

        chart.addSeries("a", new double[] {0, 3, 5, 7, 9}, new double[] {-3, 5, 9, 6, 5});
        chart.addSeries("b", new double[] {0, 2, 4, 6, 9}, new double[] {-1, 6, 4, 0, 4});
        chart.addSeries("c", new double[] {0, 1, 3, 8, 9}, new double[] {-2, -1, 1, 0, 1});

        chart.write(Files.newOutputStream(Paths.get("build/areachart1.svg")),
                VectorGraphicsFormat.SVG);
    }

    @Test
    public void testAreaChart2() throws IOException {
        XYChart chart = new XYChartBuilder()
                        .width(800)
                        .height(600)
                        .title(getClass().getSimpleName())
                        .xAxisTitle("X")
                        .yAxisTitle("Y")
                        .build();

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        List<Integer> xData = new ArrayList<>();
        List<Integer> yData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            xData.add(i);
            yData.add(i * i);
        }
        xData.add(5);
        yData.add(null);
        for (int i = 6; i < 10; i++) {
            xData.add(i);
            yData.add(i * i);
        }
        xData.add(10);
        yData.add(null);
        xData.add(11);
        yData.add(100);
        xData.add(12);
        yData.add(90);
        chart.addSeries("a", xData, yData);

        chart.write( Files.newOutputStream(Paths.get("build/areachart2.svg")),
                VectorGraphicsFormat.SVG);

    }

    @Test
    public void testAreaChart3() throws IOException {
        XYChart chart = new XYChartBuilder().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("Age").yAxisTitle("Amount").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        chart.getStyler().setYAxisLabelAlignment(TextAlignment.Right);
        chart.getStyler().setYAxisDecimalPattern("$ #,###.##");
        chart.getStyler().setPlotMargin(0);
        chart.getStyler().setPlotContentSize(.95);

        double[] xAges = new double[] { 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87,
                88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100 };

        double[] yLiability = new double[] { 672234, 691729, 711789, 732431, 753671, 775528, 798018, 821160, 844974, 869478, 907735, 887139, 865486,
                843023, 819621, 795398, 770426, 744749, 719011, 693176, 667342, 641609, 616078, 590846, 565385, 540002, 514620, 489380, 465149, 441817,
                419513, 398465, 377991, 358784, 340920, 323724, 308114, 293097, 279356, 267008, 254873 };

        double[] yPercentile75th = new double[] { 800000, 878736, 945583, 1004209, 1083964, 1156332, 1248041, 1340801, 1440138, 1550005, 1647728,
                1705046, 1705032, 1710672, 1700847, 1683418, 1686522, 1674901, 1680456, 1679164, 1668514, 1672860, 1673988, 1646597, 1641842, 1653758,
                1636317, 1620725, 1589985, 1586451, 1559507, 1544234, 1529700, 1507496, 1474907, 1422169, 1415079, 1346929, 1311689, 1256114, 1221034 };

        double[] yPercentile50th = new double[] { 800000, 835286, 873456, 927048, 969305, 1030749, 1101102, 1171396, 1246486, 1329076, 1424666, 1424173,
                1421853, 1397093, 1381882, 1364562, 1360050, 1336885, 1340431, 1312217, 1288274, 1271615, 1262682, 1237287, 1211335, 1191953, 1159689,
                1117412, 1078875, 1021020, 974933, 910189, 869154, 798476, 744934, 674501, 609237, 524516, 442234, 343960, 257025 };

        double[] yPercentile25th = new double[] { 800000, 791439, 809744, 837020, 871166, 914836, 958257, 1002955, 1054094, 1118934, 1194071, 1185041,
                1175401, 1156578, 1132121, 1094879, 1066202, 1054411, 1028619, 987730, 944977, 914929, 880687, 809330, 783318, 739751, 696201, 638242,
                565197, 496959, 421280, 358113, 276518, 195571, 109514, 13876, 29, 0, 0, 0, 0 };

        XYSeries seriesLiability = chart.addSeries("Liability", xAges, yLiability);
        seriesLiability.setXySeriesRenderStyle(XYSeriesRenderStyle.Area);
        seriesLiability.setMarker(Theme.Series.NONE_MARKER);

        chart.addSeries("75th Percentile", xAges, yPercentile75th);
        chart.addSeries("50th Percentile", xAges, yPercentile50th);
        chart.addSeries("25th Percentile", xAges, yPercentile25th);

        chart.write(Files.newOutputStream(Paths.get("build/areachart3.svg")),
                VectorGraphicsFormat.SVG);
    }

    @Test
    public void testAreaChart4() throws IOException {
        XYChart chart =
                new XYChartBuilder()
                        .width(800)
                        .height(600)
                        .title(getClass().getSimpleName())
                        .xAxisTitle("X")
                        .yAxisTitle("Y")
                        .build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
        chart.getStyler().setAxisTitlesVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.StepArea);

        chart.addSeries("a", new double[] {0, 3, 5, 7, 9}, new double[] {-3, 5, 9, 6, 5});
        chart.addSeries("b", new double[] {0, 2, 4, 6, 9}, new double[] {-1, 6, 4, 0, 4});
        chart.addSeries("c", new double[] {0, 1, 3, 8, 9}, new double[] {-2, -1, 1, 0, 1});

        chart.write(Files.newOutputStream(Paths.get("build/areachart4.svg")),
                VectorGraphicsFormat.SVG);
    }
}
