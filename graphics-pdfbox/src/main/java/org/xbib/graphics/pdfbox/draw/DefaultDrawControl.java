package org.xbib.graphics.pdfbox.draw;

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
    public Shape transformShapeBeforeFill(Shape shape, DrawControlEnvironment env) {
        return shape;
    }

    @Override
    public Shape transformShapeBeforeDraw(Shape shape, DrawControlEnvironment env) {
        return shape;
    }

    @Override
    public void afterShapeFill(Shape shape, DrawControlEnvironment env) {
    }

    @Override
    public void afterShapeDraw(Shape shape, DrawControlEnvironment env) {
    }
}
