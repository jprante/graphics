package org.xbib.graphics.io.vector.eps;

import org.xbib.graphics.io.vector.VectorGraphics2DProvider;

import java.awt.Rectangle;

public class EPSGraphics2DProvider implements VectorGraphics2DProvider<EPSGraphics2D> {
    @Override
    public String name() {
        return "eps";
    }

    @Override
    public EPSGraphics2D provide(Rectangle rectangle) {
        return new EPSGraphics2D(rectangle);
    }
}
