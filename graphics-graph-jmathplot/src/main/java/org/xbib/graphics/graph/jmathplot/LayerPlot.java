/*
 * Created on 5 juil. 07 by richet
 */
package org.xbib.graphics.graph.jmathplot;

import org.xbib.graphics.graph.jmathplot.panel.DataPanel;
import org.xbib.graphics.graph.jmathplot.canvas.PlotCanvas;
import org.xbib.graphics.graph.jmathplot.render.AbstractDrawer;

public abstract class LayerPlot extends Plot {

    Plot plot;

    public LayerPlot(String name, Plot p) {
        super(name, p.color);
        plot = p;
    }

    public double[] isSelected(int[] screenCoordTest, AbstractDrawer draw) {
        return null;
    }

    @Override
    public double[][] getBounds() {
        return plot.getBounds();
    }

    @Override
    public DataPanel getDataPanel(PlotCanvas plotCanvas) {
        return null;
    }
}
