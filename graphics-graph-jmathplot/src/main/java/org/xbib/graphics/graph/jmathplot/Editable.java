package org.xbib.graphics.graph.jmathplot;

import org.xbib.graphics.graph.jmathplot.render.AbstractDrawer;

/**
 * BSD License
 *
 * @author Yann RICHET
 */
public interface Editable {
    double[] isSelected(int[] screenCoord, AbstractDrawer draw);

    void edit(Object editParent);

    void editnote(AbstractDrawer draw);

}
