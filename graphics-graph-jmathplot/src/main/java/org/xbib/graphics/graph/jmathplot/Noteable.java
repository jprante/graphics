package org.xbib.graphics.graph.jmathplot;

import org.xbib.graphics.graph.jmathplot.render.AbstractDrawer;

public interface Noteable {

    double[] isSelected(int[] screenCoord, AbstractDrawer draw);

    void note(AbstractDrawer draw);
}