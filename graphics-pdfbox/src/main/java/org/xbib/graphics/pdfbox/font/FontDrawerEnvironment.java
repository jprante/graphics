package org.xbib.graphics.pdfbox.font;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.xbib.graphics.pdfbox.PdfBoxGraphics2D;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.io.IOException;
import java.text.AttributedCharacterIterator;

/**
 * Environment for font based drawing of text
 */
public interface FontDrawerEnvironment {
    /**
     * @return the document we are writing to
     */
    PDDocument getDocument();

    /**
     * @return the content stream
     */
    PDPageContentStream getContentStream();

    /**
     * @return the current font set on the graphics. This is the "default" font to
     * use when no other font is set on the
     * {@link AttributedCharacterIterator}.
     */
    Font getFont();

    /**
     * @return the current paint set on the graphics. This is the "default" paint
     * when no other paint is set on on the
     * {@link AttributedCharacterIterator}.
     */
    Paint getPaint();

    /**
     * Apply the given paint on the current content stream
     *
     * @param paint       Paint to apply
     * @param shapeToDraw the shape to draw of the text, if known. This is needed to
     *                    calculate correct gradients.
     * @throws IOException if an IO error occurs when writing the paint to the content
     *                     stream.
     */
    void applyPaint(Paint paint, Shape shapeToDraw) throws IOException;

    /**
     * @return the {@link Graphics2D} {@link FontRenderContext}
     */
    FontRenderContext getFontRenderContext();

    /**
     * @return the bbox of the {@link PdfBoxGraphics2D}
     */
    PDRectangle getGraphicsBBox();

    /**
     * @return the resource of the content stream
     */
    PDResources getResources();

    /**
     * @return the default calcuation BufferedImage based graphics.
     */
    Graphics2D getCalculationGraphics();
}
