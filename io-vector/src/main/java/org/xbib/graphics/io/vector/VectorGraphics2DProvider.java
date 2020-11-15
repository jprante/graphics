package org.xbib.graphics.io.vector;

import java.awt.Rectangle;

public interface VectorGraphics2DProvider<V extends VectorGraphics2D> {

    String name();

    V provide(Rectangle rectangle);
}
