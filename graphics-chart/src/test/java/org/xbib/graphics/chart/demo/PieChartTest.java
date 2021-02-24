package org.xbib.graphics.chart.demo;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.pie.PieChart;
import org.xbib.graphics.chart.pie.PieChartBuilder;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class PieChartTest {

    @Test
    public void testPieChart2() throws IOException {
        PieChart chart = new PieChartBuilder()
                .width(800)
                .height(600)
                .title(getClass().getSimpleName())
                .build();
        List<Color> sliceColors = Arrays.asList(new Color(224, 68, 14),
                new Color(230, 105, 62),
                new Color(236, 143, 110),
                new Color(243, 180, 159),
                new Color(246, 199, 182));
        chart.getStyler().setSeriesColors(sliceColors);

        chart.addSeries("Gold", 24);
        chart.addSeries("Silver", 21);
        chart.addSeries("Platinum", 39);
        chart.addSeries("Copper", 17);
        chart.addSeries("Zinc", 40);

        chart.write(Files.newOutputStream(Paths.get("build/piechart2.svg")),
                VectorGraphicsFormat.SVG);

    }
}
