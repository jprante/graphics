package org.xbib.graphics.io.vector;

public interface VectorGraphics2DProvider<V extends VectorGraphics2D> {

    String name();

    V provide(double x, double y, double width, double height);
}
