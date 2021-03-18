package org.xbib.graphics.graph.jmathplot.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.graph.jmathplot.panel.FrameView;
import org.xbib.graphics.graph.jmathplot.panel.Plot2DPanel;
import javax.swing.JFrame;

public class QuantileLayerPlotTest {

    @Test
    public void test() throws InterruptedException {
        Plot2DPanel p2 = new Plot2DPanel();
        for (int i = 0; i < 1; i++) {
            double[][] XYZ = new double[10][2];
            for (int j = 0; j < XYZ.length; j++) {
                XYZ[j][0] = /*1 + */Math.random();
                XYZ[j][1] = /*100 * */Math.random();
            }
            p2.addScatterPlot("toto" + i, XYZ);
        }
        p2.addQuantiletoPlot(0, 1, 1.0, true, 0.2);
        new FrameView(p2).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Thread.sleep(10000);
    }
}
