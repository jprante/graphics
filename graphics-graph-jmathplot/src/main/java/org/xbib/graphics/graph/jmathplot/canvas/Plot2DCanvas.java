package org.xbib.graphics.graph.jmathplot.canvas;

import static org.xbib.graphics.graph.jmathplot.Base.LINEAR;
import static org.xbib.graphics.graph.jmathplot.utils.Array.getColumnCopy;
import static org.xbib.graphics.graph.jmathplot.utils.Array.getColumnsRangeCopy;
import static org.xbib.graphics.graph.jmathplot.utils.Array.increment;
import static org.xbib.graphics.graph.jmathplot.utils.Array.mergeColumns;
import static org.xbib.graphics.graph.jmathplot.utils.Histogram.histogram_classes;
import static org.xbib.graphics.graph.jmathplot.utils.Histogram.histogram_classes_2D;
import org.xbib.graphics.graph.jmathplot.Base;
import org.xbib.graphics.graph.jmathplot.BasePlot;
import org.xbib.graphics.graph.jmathplot.BarPlot;
import org.xbib.graphics.graph.jmathplot.BoxPlot2D;
import org.xbib.graphics.graph.jmathplot.CloudPlot2D;
import org.xbib.graphics.graph.jmathplot.HistogramPlot2D;
import org.xbib.graphics.graph.jmathplot.LinePlot;
import org.xbib.graphics.graph.jmathplot.ScatterPlot;
import org.xbib.graphics.graph.jmathplot.StaircasePlot;
import org.xbib.graphics.graph.jmathplot.render.AWTDrawer2D;
import java.awt.Color;

/**
 * BSD License
 *
 * @author Yann RICHET
 */
public class Plot2DCanvas extends PlotCanvas {

    // public final static String PARALLELHISTOGRAM = "PARALLELHISTOGRAM";

    private static final long serialVersionUID = 1L;

    public Plot2DCanvas() {
        super();
        ActionMode = ZOOM;
    }

    public Plot2DCanvas(Base b, BasePlot bp) {
        super(b, bp);
        ActionMode = ZOOM;
    }

    public Plot2DCanvas(double[] min, double[] max, String[] axesScales, String[] axesLabels) {
        super(min, max, axesScales, axesLabels);
        ActionMode = ZOOM;
    }

    public void initDrawer() {
        draw = new AWTDrawer2D(this);
    }

    public void initBasenGrid(double[] min, double[] max) {
        initBasenGrid(min, max, new String[]{LINEAR, LINEAR}, new String[]{"X", "Y"});
    }

    public void initBasenGrid() {
        initBasenGrid(new double[]{0, 0}, new double[]{1, 1});
    }

    private static double[][] convertY(double[] XY) {
        double[] x = increment(XY.length, 1, 1);
        return mergeColumns(x, XY);
    }

    private static double[][] convertXY(double[]... XY) {
		if (XY.length == 2 && XY[0].length != 2) {
			return mergeColumns(XY[0], XY[1]);
		} else {
			return XY;
		}
    }

    public int addScatterPlot(String name, Color c, double[] Y) {
        return addPlot(new ScatterPlot(name, c, convertY(Y)));
    }

    public int addScatterPlot(String name, Color c, double[][] XY) {
        return addPlot(new ScatterPlot(name, c, convertXY(XY)));
    }

    public int addScatterPlot(String name, Color c, double[] X, double[] Y) {
        return addPlot(new ScatterPlot(name, c, convertXY(X, Y)));
    }

    public int addLinePlot(String name, Color c, double[] Y) {
        return addPlot(new LinePlot(name, c, convertY(Y)));
    }

    public int addLinePlot(String name, Color c, double[][] XY) {
        return addPlot(new LinePlot(name, c, convertXY(XY)));
    }

    public int addLinePlot(String name, Color c, double[] X, double[] Y) {
        return addPlot(new LinePlot(name, c, convertXY(X, Y)));
    }

    public int addBarPlot(String name, Color c, double[] Y) {
        return addPlot(new BarPlot(name, c, convertY(Y)));
    }

    public int addBarPlot(String name, Color c, double[][] XY) {
        return addPlot(new BarPlot(name, c, convertXY(XY)));
    }

    public int addBarPlot(String name, Color c, double[] X, double[] Y) {
        return addPlot(new BarPlot(name, c, convertXY(X, Y)));
    }

    public int addStaircasePlot(String name, Color c, double[] Y) {
        return addPlot(new StaircasePlot(name, c, convertY(Y)));
    }

    public int addStaircasePlot(String name, Color c, double[][] XY) {
        return addPlot(new StaircasePlot(name, c, convertXY(XY)));
    }

    public int addStaircasePlot(String name, Color c, double[] X, double[] Y) {
        return addPlot(new StaircasePlot(name, c, convertXY(X, Y)));
    }


    public int addBoxPlot(String name, Color c, double[][] XY, double[][] dX) {
        return addPlot(new BoxPlot2D(XY, dX, c, name));
    }

    public int addBoxPlot(String name, Color c, double[][] XYdX) {
        return addPlot(new BoxPlot2D(getColumnsRangeCopy(XYdX, 0, 1), getColumnsRangeCopy(XYdX, 2, 3), c, name));
    }

    public int addHistogramPlot(String name, Color c, double[][] XY, double[] dX) {
        return addPlot(new HistogramPlot2D(name, c, XY, dX));
    }

    public int addHistogramPlot(String name, Color c, double[][] XY, double dX) {
        return addPlot(new HistogramPlot2D(name, c, XY, dX));
    }

    public int addHistogramPlot(String name, Color c, double[][] XYdX) {
        return addPlot(new HistogramPlot2D(name, c, getColumnsRangeCopy(XYdX, 0, 1), getColumnCopy(XYdX, 2)));
    }

    public int addHistogramPlot(String name, Color c, double[] X, int n) {
        double[][] XY = histogram_classes(X, n);
        return addPlot(new HistogramPlot2D(name, c, XY, XY[1][0] - XY[0][0]));
    }

    public int addHistogramPlot(String name, Color c, double[] X, double... bounds) {
        double[][] XY = histogram_classes(X, bounds);
        return addPlot(new HistogramPlot2D(name, c, XY, XY[1][0] - XY[0][0]));
    }

    public int addHistogramPlot(String name, Color c, double[] X, double min, double max, int n) {
        double[][] XY = histogram_classes(X, min, max, n);
        return addPlot(new HistogramPlot2D(name, c, XY, XY[1][0] - XY[0][0]));
    }

    public int addCloudPlot(String name, Color c, double[][] sampleXY, int nX, int nY) {
        double[][] XYh = histogram_classes_2D(sampleXY, nX, nY);
        return addPlot(new CloudPlot2D(name, c, XYh, XYh[1][0] - XYh[0][0], XYh[nX][1] - XYh[0][1]));
    }

    public static void main(String[] args) {
        /*
         * Plot2DPanel p2d = new Plot2DPanel(DoubleArray.random(10, 2), "plot
         * 1", PlotPanel.SCATTER); new FrameView(p2d);
         * p2d.addPlot(DoubleArray.random(10, 2), "plot 2", PlotPanel.SCATTER);
         * p2d.grid.getAxe(0).darkLabel.setCorner(0.5, -10);
         * p2d.grid.getAxe(1).darkLabel.setCorner(0, -0.5);
         */
    }
}