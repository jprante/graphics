package org.xbib.graphics.svg.element.filtereffects;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;

public class FilterOp {

    public final BufferedImageOp op;

    public final Rectangle requiredImageBounds;

    public FilterOp(BufferedImageOp op, Rectangle requiredImageBounds) {
        this.op = op;
        this.requiredImageBounds = requiredImageBounds;
    }
}
