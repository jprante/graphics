package org.xbib.graphics.chart.demo;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.chart.Histogram;
import org.xbib.graphics.chart.theme.GGPlot2Theme;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.category.CategoryChart;
import org.xbib.graphics.chart.category.CategoryChartBuilder;
import org.xbib.graphics.chart.category.CategorySeries;
import org.xbib.graphics.chart.category.CategorySeriesRenderStyle;
import org.xbib.graphics.chart.legend.LegendPosition;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BarChartTest {

    @Test
    public void testBarChart1() throws IOException {
        CategoryChart chart =
                new CategoryChartBuilder()
                        .width(800)
                        .height(600)
                        .title("Score Histogram")
                        .xAxisTitle("Score")
                        .yAxisTitle("Number")
                        .build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setPlotGridLinesVisible(false);

        chart.addSeries("test 1", Arrays.asList(0, 1, 2, 3, 4), Arrays.asList(4, 5, 9, 6, 5));

        chart.write(Files.newOutputStream(Paths.get("build/barchart1.svg")),
                VectorGraphicsFormat.SVG);
    }

    @Test
    public void testBarChart6() throws IOException {
        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Score Histogram").xAxisTitle("Mean").yAxisTitle("Count").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setAvailableSpaceFill(.96);
        chart.getStyler().setOverlapped(true);

        Histogram histogram1 = new Histogram(getGaussianData(10000), 20, -20, 20);
        Histogram histogram2 = new Histogram(getGaussianData(5000), 20, -20, 20);
        chart.addSeries("histogram 1", histogram1.getxAxisData(), histogram1.getyAxisData());
        chart.addSeries("histogram 2", histogram2.getxAxisData(), histogram2.getyAxisData());

        chart.write(Files.newOutputStream(Paths.get("build/barchart6.svg")),
                VectorGraphicsFormat.SVG);
    }

    private List<Double> getGaussianData(int count) {
        List<Double> data = new ArrayList<>(count);
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            data.add(r.nextGaussian() * 10);
        }
        return data;
    }

    @Test
    public void testGGPlot1() throws IOException {
        CategoryChart chart =
                new CategoryChartBuilder()
                        .width(800)
                        .height(600)
                        .title("Temperature vs. Color")
                        .xAxisTitle("Color")
                        .yAxisTitle("Temperature")
                        .theme(new GGPlot2Theme())
                        .build();

        chart.getStyler().setPlotGridVerticalLinesVisible(false);

        chart.addSeries("fish", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                Arrays.asList(-40, 30, 20, 60, 60));
        chart.addSeries("worms", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                Arrays.asList(50, 10, -20, 40, 60));
        chart.addSeries("birds", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                Arrays.asList(13, 22, -23, -34, 37));
        chart.addSeries("ants", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                Arrays.asList(50, 57, -14, -20, 31));
        chart.addSeries("slugs", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                Arrays.asList(-2, 29, 49, -16, -43));

        chart.write(Files.newOutputStream(Paths.get("build/ggplot1.svg")),
                VectorGraphicsFormat.SVG);
    }


    @Test
    public void testGGPlot2() throws IOException {
        CategoryChart chart =
                new CategoryChartBuilder()
                        .width(800)
                        .height(600)
                        .title("Temperature vs. Color")
                        .xAxisTitle("Color")
                        .yAxisTitle("Temperature")
                        .theme(new GGPlot2Theme())
                        .build();

        chart.getStyler().setPlotGridVerticalLinesVisible(false);
        List<CategorySeries> categorySeries = new ArrayList<>();

        categorySeries.add(chart.addSeries("fish", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                        Arrays.asList(-40, 30, 20, 60, 60)));
        categorySeries.add(chart.addSeries("worms", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                        Arrays.asList(50, 10, -20, 40, 60)));
        categorySeries.add(chart.addSeries("birds", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                        Arrays.asList(13, 22, -23, -34, 37)));
        categorySeries.add(chart.addSeries("ants", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                        Arrays.asList(50, 57, -14, -20, 31)));
        categorySeries.add(chart.addSeries("slugs", Arrays.asList("Blue", "Red", "Green", "Yellow", "Orange"),
                        Arrays.asList(-2, 29, 49, -16, -43)));

        for (CategorySeries series : categorySeries) {
            series.setCategorySeriesRenderStyle(CategorySeriesRenderStyle.SteppedBar);
            series.setFillColor(new Color(0, 0, 0, 0));
        }
        chart.write(Files.newOutputStream(Paths.get("build/ggplot2.svg")),
                VectorGraphicsFormat.SVG);
    }

}
