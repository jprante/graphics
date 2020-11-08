package org.xbib.graphics.io.pdfbox;

import java.awt.Shape;

/**
 * Default implementation which does nothing. You can derive from it to only
 * override the needed methods
 */
public class DefaultDrawControl implements DrawControl {

    public static final DefaultDrawControl INSTANCE = new DefaultDrawControl();

    protected DefaultDrawControl() {
    }

    @Override
    public Shape transformShapeBeforeFill(Shape shape, DrawControlEnv env) {
        return shape;
    }

    @Override
    public Shape transformShapeBeforeDraw(Shape shape, DrawControlEnv env) {
        return shape;
    }

    @Override
    public void afterShapeFill(Shape shape, DrawControlEnv env) {
    }

    @Override
    public void afterShapeDraw(Shape shape, DrawControlEnv env) {
    }
}
