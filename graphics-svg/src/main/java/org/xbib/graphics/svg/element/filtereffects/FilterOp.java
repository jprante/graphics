package org.xbib.graphics.svg.element.filtereffects;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;

public class FilterOp {

    private final BufferedImageOp op;

    private final Rectangle requiredImageBounds;

    public FilterOp(BufferedImageOp op, Rectangle requiredImageBounds) {
        this.op = op;
        this.requiredImageBounds = requiredImageBounds;
    }

    public BufferedImageOp getOp() {
        return op;
    }

    public Rectangle getRequiredImageBounds() {
        return requiredImageBounds;
    }
}
