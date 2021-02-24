package org.xbib.graphics.chart.demo;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.category.CategoryChart;
import org.xbib.graphics.chart.category.CategoryChartBuilder;
import org.xbib.graphics.chart.category.CategorySeriesRenderStyle;
import org.xbib.graphics.chart.legend.LegendPosition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StickChartTest {

    @Test
    public void testStick1() throws IOException {
        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Stick").build();
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setDefaultSeriesRenderStyle(CategorySeriesRenderStyle.Stick);
        List<Integer> xData = new ArrayList<>();
        List<Integer> yData = new ArrayList<>();
        for (int i = -3; i <= 24; i++) {
            xData.add(i);
            yData.add(i);
        }
        chart.addSeries("data", xData, yData);
        chart.write(Files.newOutputStream(Paths.get("build/stickchart1.svg")),
                VectorGraphicsFormat.SVG);
    }
}
