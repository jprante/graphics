package org.xbib.graphics.graph.jmathplot;

import org.xbib.graphics.graph.jmathplot.render.AbstractDrawer;
import java.awt.Color;

public interface Plotable {

    void plot(AbstractDrawer draw);

    void setVisible(boolean v);

    boolean getVisible();

    void setColor(Color c);

    Color getColor();

}