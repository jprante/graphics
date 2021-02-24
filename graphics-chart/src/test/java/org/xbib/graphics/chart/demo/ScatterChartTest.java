package org.xbib.graphics.chart.demo;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.chart.theme.Theme;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.axis.YAxisPosition;
import org.xbib.graphics.chart.legend.LegendPosition;
import org.xbib.graphics.chart.xy.XYChart;
import org.xbib.graphics.chart.xy.XYChartBuilder;
import org.xbib.graphics.chart.xy.XYSeries;
import org.xbib.graphics.chart.xy.XYSeriesRenderStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ScatterChartTest {

    @Test
    public void testScatterChart0() throws IOException {
        XYChart chart = new XYChartBuilder().width(600).height(500).title("Gaussian Blobs").xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(16);
        chart.addSeries("Gaussian Blob 1", getGaussian(1000, 1, 10), getGaussian(1000, 1, 10));
        XYSeries series = chart.addSeries("Gaussian Blob 2", getGaussian(1000, 1, 10), getGaussian(1000, 0, 5));
        series.setMarker(Theme.Series.DIAMOND_MARKER);
        chart.write(Files.newOutputStream(Paths.get("build/scatterchart0.svg")),
                VectorGraphicsFormat.SVG);
    }

    private static final Random random = new Random();

    private static  List<Double> getGaussian(int number, double mean, double std) {
        List<Double> seriesData = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            seriesData.add(mean + std * random.nextGaussian());
        }
        return seriesData;
    }

    @Test
    public void testScatterChart1() throws IOException {
        XYChart chart = new XYChartBuilder().width(800).height(600).build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setMarkerSize(16);
        chart.getStyler().setYAxisGroupPosition(0, YAxisPosition.Right);
        List<Double> xData = new LinkedList<>();
        List<Double> yData = new LinkedList<>();
        Random random = new Random();
        int size = 1000;
        for (int i = 0; i < size; i++) {
            xData.add(random.nextGaussian() / 1000);
            yData.add(-1000000 + random.nextGaussian());
        }
        XYSeries series = chart.addSeries("Gaussian Blob", xData, yData);
        series.setMarker(Theme.Series.CROSS_MARKER);

        chart.write(Files.newOutputStream(Paths.get("build/scatterchart1.svg")),
                VectorGraphicsFormat.SVG);
    }

    @Test
    public void testScatterChart2() throws IOException {
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Logarithmic Data").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setLegendPosition(LegendPosition.InsideN);
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        Random random = new Random();
        int size = 400;
        for (int i = 0; i < size; i++) {
            double nextRandom = random.nextDouble();
            xData.add(Math.pow(10, nextRandom * 10));
            yData.add(1000000000.0 + nextRandom);
        }
        chart.addSeries("logarithmic data", xData, yData);

        chart.write(Files.newOutputStream(Paths.get("build/scatterchart2.svg")),
                VectorGraphicsFormat.SVG);
    }

    @Test
    public void testScatterChart3() throws IOException {
        XYChart chart =
                new XYChartBuilder()
                        .width(800)
                        .height(600)
                        .title("Single Point")
                        .xAxisTitle("X")
                        .yAxisTitle("Y")
                        .build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.addSeries("single point (1,1)", new double[] {1}, new double[] {1});

        chart.write(Files.newOutputStream(Paths.get("build/scatterchart3.svg")),
                VectorGraphicsFormat.SVG);
    }
}