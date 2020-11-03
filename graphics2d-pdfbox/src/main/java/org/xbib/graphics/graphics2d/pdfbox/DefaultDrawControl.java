package org.xbib.graphics.graphics2d.pdfbox;

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
    public Shape transformShapeBeforeFill(Shape shape, IDrawControlEnv env) {
        return shape;
    }

    @Override
    public Shape transformShapeBeforeDraw(Shape shape, IDrawControlEnv env) {
        return shape;
    }

    @Override
    public void afterShapeFill(Shape shape, IDrawControlEnv env) {
    }

    @Override
    public void afterShapeDraw(Shape shape, IDrawControlEnv env) {
    }
}
