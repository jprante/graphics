package org.xbib.graphics.pdfbox.draw;

import org.xbib.graphics.pdfbox.PdfBoxGraphics2D;
import java.awt.Paint;
import java.awt.Shape;

/**
 * Allows you to influence the fill and draw operations. You can alter the shape
 * to draw/fill, you can even filter out the complete draw/fill operation.
 * And you can draw additional stuff after the draw/fill operation, e.g. to
 * implement overfill.
 */
public interface DrawControl {
    /**
     * You may optional change the shape that is going to be filled. You can also do
     * other stuff here like drawing an overfill before the real shape.
     *
     * @param shape the shape that will be drawn
     * @param env   Environment
     * @return the shape to be filled. If you return null, nothing will be filled
     */
    Shape transformShapeBeforeFill(Shape shape, DrawControlEnvironment env);

    /**
     * You may optional change the shape that is going to be drawn. You can also do
     * other stuff here like drawing an overfill before the real shape.
     *
     * @param shape the shape that will be drawn
     * @param env   Environment
     * @return the shape to be filled. If you return null, nothing will be drawn
     */
    Shape transformShapeBeforeDraw(Shape shape, DrawControlEnvironment env);

    /**
     * Called after shape was filled. This method is always called, even if
     * {@link #transformShapeBeforeFill(Shape, DrawControlEnvironment)} returns
     * null.
     *
     * @param shape the shape that was filled. This is the original shape, not the one
     *              transformed by
     *              {@link #transformShapeBeforeFill(Shape, DrawControlEnvironment)}.
     * @param env   Environment
     */
    void afterShapeFill(Shape shape, DrawControlEnvironment env);

    /**
     * Called after shape was drawn. This method is always called, even if
     * {@link #transformShapeBeforeDraw(Shape, DrawControlEnvironment)} returns
     * null.
     *
     * @param shape the shape that was drawn. This is the original shape, not the one
     *              transformed by
     *              {@link #transformShapeBeforeDraw(Shape, DrawControlEnvironment)}.
     * @param env   Environment
     */
    void afterShapeDraw(Shape shape, DrawControlEnvironment env);

    /**
     * The environment of the draw operation
     */
    interface DrawControlEnvironment {
        /**
         * @return the current paint set on the graphics.
         */
        Paint getPaint();

        /**
         * @return the graphics currently drawn on
         */
        PdfBoxGraphics2D getGraphics();
    }
}
