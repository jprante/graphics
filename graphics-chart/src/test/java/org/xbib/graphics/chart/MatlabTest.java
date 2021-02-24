package org.xbib.graphics.chart;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.theme.MatlabTheme;
import org.xbib.graphics.chart.xy.XYChart;
import org.xbib.graphics.chart.xy.XYChartBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MatlabTest {

    @Test
    public void testMatlabInstants() throws IOException {
        XYChart chart = new XYChartBuilder().width(800).height(600)
                .theme(new MatlabTheme())
                .title("Matlab Theme")
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .build();
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.getStyler().setXAxisTickMarkSpacingHint(100);
        chart.getStyler().setDatePattern("HH:mm:ss");
        List<Instant> xData = new ArrayList<>();
        List<Double> y1Data = new ArrayList<>();
        List<Double> y2Data = new ArrayList<>();

        xData.add(Instant.parse("2012-08-01T00:00:00Z"));
        y1Data.add(120d);
        y2Data.add(15d);

        xData.add(Instant.parse("2012-08-01T01:00:00Z"));
        y1Data.add(165d);
        y2Data.add(15d);

        xData.add(Instant.parse("2012-08-01T02:00:00Z"));
        y1Data.add(210d);
        y2Data.add(20d);

        xData.add(Instant.parse("2012-08-01T03:00:00Z"));
        y1Data.add(400d);
        y2Data.add(30d);

        xData.add(Instant.parse("2012-08-01T04:00:00Z"));
        y1Data.add(800d);
        y2Data.add(100d);

        xData.add(Instant.parse("2012-08-01T05:00:00Z"));
        y1Data.add(2000d);
        y2Data.add(120d);

        xData.add(Instant.parse("2012-08-01T06:00:00Z"));
        y1Data.add(3000d);
        y2Data.add(150d);

        chart.addSeries("downloads", xData, y1Data);
        chart.addSeries("price", xData, y2Data);

        chart.write(Files.newOutputStream(Paths.get("build/matlab.svg")),
                VectorGraphicsFormat.SVG);
    }
}
