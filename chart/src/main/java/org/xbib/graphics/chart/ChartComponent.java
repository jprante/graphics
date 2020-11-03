package org.xbib.graphics.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * A components of a chart that need to be painted.
 */
public interface ChartComponent {

    Rectangle2D getBounds();

    void paint(Graphics2D g);
}
