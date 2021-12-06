package org.xbib.graphics.pdfbox.layout.table.render;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.util.Matrix;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.util.PdfUtil;
import org.xbib.graphics.pdfbox.layout.table.VerticalTextCell;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;

/**
 * Allows vertical text drawing. Note that this class is still not fully
 * developed, e.g. there is no support for text alignment settings yet.
 */
public class VerticalTextCellRenderer extends AbstractCellRenderer<VerticalTextCell> {

    public VerticalTextCellRenderer(VerticalTextCell cell) {
        this.cell = cell;
    }

    @Override
    public void renderContent(RenderContext renderContext) {
        final float startX = renderContext.getStartingPoint().x;
        final float startY = renderContext.getStartingPoint().y;
        final Font currentFont = cell.getFont();
        final int currentFontSize = cell.getFontSize();
        final Color currentTextColor = cell.getTextColor();
        float yOffset = startY + cell.getPaddingBottom();
        float height = cell.getRow().getHeight();
        if (cell.getRowSpan() > 1) {
            float rowSpanAdaption = cell.calculateHeightForRowSpan() - cell.getRow().getHeight();
            yOffset -= rowSpanAdaption;
            height = cell.calculateHeightForRowSpan();
        }
        final List<String> lines = cell.isWordBreak()
                ? PdfUtil.getOptimalTextBreakLines(cell.getText(), currentFont, currentFontSize, (height - cell.getVerticalPadding()))
                : Collections.singletonList(cell.getText());
        float xOffset = startX + cell.getPaddingLeft() - PdfUtil.getFontHeight(currentFont, currentFontSize);
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            xOffset += (PdfUtil.getFontHeight(currentFont, currentFontSize) + (i > 0 ? PdfUtil.getFontHeight(currentFont, currentFontSize) * cell.getLineSpacing() : 0f));
            drawText(line, currentFont, currentFontSize, currentTextColor, xOffset, yOffset, renderContext.getContentStream());
        }
    }

    // TODO this is currently not used
    @Override
    protected float calculateInnerHeight() {
        return 0;
    }

    protected void drawText(String text, Font font, int fontSize, Color color, float x, float y, PDPageContentStream contentStream) {
        try {
            // Rotate by 90 degrees counter clockwise
            AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
            transform.concatenate(AffineTransform.getRotateInstance(Math.PI * 0.5));
            transform.concatenate(AffineTransform.getTranslateInstance(-x, -y - fontSize));
            contentStream.moveTo(x, y);
            contentStream.beginText();
            contentStream.setTextMatrix(new Matrix(transform));
            contentStream.setNonStrokingColor(color);
            // TODO select correct font
            contentStream.setFont(font.getRegularFont(), fontSize);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(text);
            contentStream.endText();
            contentStream.setCharacterSpacing(0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
