package org.xbib.graphics.pdfbox.layout.element;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.TextFlow;
import org.xbib.graphics.pdfbox.layout.util.TextSequenceUtil;
import org.xbib.graphics.pdfbox.layout.text.WidthRespecting;

/**
 * A paragraph is used as a container for {@link TextFlow text} that is drawn as
 * one element. A paragraph has a {@link #setAlignment(Alignment) (text-)
 * alignment}, and {@link WidthRespecting respects a given width} by applying
 * word-wrap.
 */
public class Paragraph extends TextFlow implements Drawable, Element, WidthRespecting, Dividable {

    private Position absolutePosition;

    private Alignment alignment = Alignment.LEFT;

    @Override
    public Position getAbsolutePosition() {
        return absolutePosition;
    }

    /**
     * Sets the absolute position to render at.
     *
     * @param absolutePosition the absolute position.
     */
    public void setAbsolutePosition(Position absolutePosition) {
        this.absolutePosition = absolutePosition;
    }

    /**
     * @return the text alignment to apply. Default is left.
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Sets the alignment to apply.
     *
     * @param alignment the text alignment.
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
                     Position upperLeft, DrawListener drawListener) {
        drawText(contentStream, upperLeft, getAlignment(), drawListener);
    }

    @Override
    public Divided divide(float remainingHeight, final float pageHeight) {
        return TextSequenceUtil.divide(this, getMaxWidth(), remainingHeight);
    }

    @Override
    public Paragraph removeLeadingEmptyVerticalSpace() {
        return removeLeadingEmptyLines();
    }

    @Override
    public Paragraph removeLeadingEmptyLines() {
        Paragraph result = (Paragraph) super.removeLeadingEmptyLines();
        result.setAbsolutePosition(this.getAbsolutePosition());
        result.setAlignment(this.getAlignment());
        return result;
    }

    @Override
    protected Paragraph createInstance() {
        return new Paragraph();
    }

    @Override
    public Element add(Element element) {
        if (element instanceof TextElement) {
            TextElement textElement = (TextElement) element;
            addMarkup(textElement.getValue(), textElement.getSize(), textElement.getFont());
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }
}
