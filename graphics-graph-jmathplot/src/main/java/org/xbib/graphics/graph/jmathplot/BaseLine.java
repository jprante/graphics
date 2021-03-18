/*
 * Created on 1 juin 2005 by richet
 */
package org.xbib.graphics.graph.jmathplot;

import org.xbib.graphics.graph.jmathplot.render.AbstractDrawer;
import java.awt.Color;

public class BaseLine extends Line {

    public BaseLine(Color col, double[] c1, double[] c2) {
        super(col, c1, c2);
    }

    public void plot(AbstractDrawer draw) {
        if (!visible) {
            return;
        }

        draw.setColor(color);
        draw.drawLineBase(extrem[0], extrem[1]);
    }

}
