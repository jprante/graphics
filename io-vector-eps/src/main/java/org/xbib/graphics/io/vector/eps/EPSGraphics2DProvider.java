package org.xbib.graphics.io.vector.eps;

import org.xbib.graphics.io.vector.VectorGraphics2DProvider;

public class EPSGraphics2DProvider implements VectorGraphics2DProvider<EPSGraphics2D> {
    @Override
    public String name() {
        return "eps";
    }

    @Override
    public EPSGraphics2D provide(double x, double y, double width, double height) {
        return new EPSGraphics2D(x, y, width, height);
    }
}
