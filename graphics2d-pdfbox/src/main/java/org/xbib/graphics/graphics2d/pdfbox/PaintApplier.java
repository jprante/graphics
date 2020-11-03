package org.xbib.graphics.graphics2d.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.IOException;

/**
 * Apply the given paint on the Content Stream.
 */
public interface PaintApplier {
    /**
     * Apply the paint on the ContentStream
     *
     * @param paint            the paint which should be applied
     * @param contentStream    the content stream to apply the paint on
     * @param currentTransform the current transform of the Graphics2D relative to the
     *                         contentStream default coordinate space. This is always a copy of the
     *                         current transform, so we can modify it.
     * @param env              Environment for mapping the paint.
     * @return null or a PDShading which should be used to fill a shape.
     * @throws IOException if its not possible to write the paint into the contentStream
     */
    PDShading applyPaint(Paint paint, PDPageContentStream contentStream,
                         AffineTransform currentTransform, IPaintEnv env) throws IOException;

    /**
     * The different mappers used by the paint applier. This interface is
     * implemented internally by {@link PdfBoxGraphics2D}
     */
    interface IPaintEnv {
        /**
         * @return the color mapper
         */
        ColorMapper getColorMapper();

        /**
         * @return the image encoder
         */
        ImageEncoder getImageEncoder();

        /**
         * @return the document
         */
        PDDocument getDocument();

        /**
         * @return the resource of the content stream
         */
        PDResources getResources();

        /**
         * @return the {@link Graphics2D} {@link Composite}
         */
        Composite getComposite();

        /**
         * @return The PdfBoxGraphics2D
         */
        PdfBoxGraphics2D getGraphics2D();

        /**
         * @return the {@link Graphics2D} XOR Mode {@link Color} or null if paint mode
         * is active.
         */
        @SuppressWarnings("unused")
        Color getXORMode();

        /**
         * The shape information is need to be able to correctly render grandients.
         *
         * @return get the shape which will be drawn or filled with this paint. Null is
         * returned if no shape is known.
         */
        Shape getShapeToDraw();
    }
}
