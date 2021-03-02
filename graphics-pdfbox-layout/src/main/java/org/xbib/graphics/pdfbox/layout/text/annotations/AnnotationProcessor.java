package org.xbib.graphics.pdfbox.layout.text.annotations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.xbib.graphics.pdfbox.layout.text.DrawContext;
import org.xbib.graphics.pdfbox.layout.text.Position;

/**
 * Processes an annotation.
 */
public interface AnnotationProcessor {

    /**
     * Called if an annotated object has been drawn.
     *
     * @param drawnObject the drawn object.
     * @param drawContext the drawing context.
     * @param upperLeft   the upper left position the object has been drawn to.
     * @param width       the width of the drawn object.
     * @param height      the height of the drawn object.
     */
    void annotatedObjectDrawn(final Annotated drawnObject,
                              final DrawContext drawContext, Position upperLeft, float width,
                              float height);

    /**
     * Called before a page is drawn.
     *
     * @param drawContext the drawing context.
     */
    void beforePage(final DrawContext drawContext);

    /**
     * Called after a page is drawn.
     *
     * @param drawContext the drawing context.
     */
    void afterPage(final DrawContext drawContext);

    /**
     * Called after all rendering has been performed.
     *
     * @param document the document.
     */
    void afterRender(final PDDocument document);

}
