package org.xbib.graphics.chart.plot;

import org.xbib.graphics.chart.Chart;
import org.xbib.graphics.chart.ChartComponent;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.Styler;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public abstract class ContentPlot<ST extends Styler, S extends Series> extends Plot<ST, S> implements ChartComponent {

    public ContentPlot(Chart<ST, S> chart) {
        super(chart);
    }

    @Override
    public void paint(Graphics2D g) {
        Rectangle2D bounds = getBounds();
        if (bounds != null) {
            if (bounds.getWidth() < 30) {
                return;
            }
            Shape saveClip = g.getClip();
            g.setClip(bounds.createIntersection(bounds));
            doPaint(g);
            g.setClip(saveClip);
        }
    }

    /**
     * Closes a path for area charts if one is available.
     */
    protected void closePath(Graphics2D g, Path2D.Double path, double previousX, double yTopMargin) {
        if (path != null) {
            double yBottomOfArea = getBounds().getY() + getBounds().getHeight() - yTopMargin;
            path.lineTo(previousX, yBottomOfArea);
            path.closePath();
            g.fill(path);
        }
    }

    protected abstract void doPaint(Graphics2D g);
}
