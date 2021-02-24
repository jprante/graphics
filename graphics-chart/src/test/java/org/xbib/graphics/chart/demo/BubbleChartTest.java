package org.xbib.graphics.chart.demo;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.bubble.BubbleChart;
import org.xbib.graphics.chart.bubble.BubbleChartBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class BubbleChartTest {

    @Test
    public void testBubble1() throws IOException {
        BubbleChart chart = new BubbleChartBuilder().width(800).height(600).title("BubbleChart01").xAxisTitle("X").yAxisTitle("Y").build();

        List<Double> xData = Arrays.asList(1.5, 2.6, 3.3, 4.9, 5.5, 6.3, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        List<Double> yData = Arrays.asList(10.0, 4.0, 7.0, 7.7, 7.0, 5.5, 10.0, 4.0, 7.0, 1.0, 7.0, 9.0);
        List<Double> bubbleData = Arrays.asList(17.0, 40.0, 50.0, 51.0, 26.0, 20.0, 66.0, 35.0, 80.0, 27.0, 29.0, 44.0);

        List<Double> xData2 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 1.5, 2.6, 3.3, 4.9, 5.5, 6.3);
        List<Double> yData2 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 10.0, 8.5, 4.0, 1.0, 4.7, 9.0);
        List<Double> bubbleData2 = Arrays.asList(37.0, 35.0, 80.0, 27.0, 29.0, 44.0, 57.0, 40.0, 50.0, 33.0, 26.0, 20.0);

        chart.addSeries("A", xData, yData, bubbleData);
        chart.addSeries("B", xData2, yData2, bubbleData2);

        chart.write(Files.newOutputStream(Paths.get("build/bubblechart1.svg")),
                VectorGraphicsFormat.SVG);
    }
}
