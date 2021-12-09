package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.util.PdfUtil;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractTextCell extends AbstractCell {

    private Float textHeight;

    private float lineSpacing = 1f;

    @Override
    public float getMinHeight() {
        return Math.max((getVerticalPadding() + getTextHeight()), super.getMinHeight());
    }

    public Font getFont() {
        return parameters.getFont();
    }

    public Float getFontSize() {
        return parameters.getFontSize();
    }

    public Color getTextColor() {
        return parameters.getTextColor();
    }

    public abstract String getText();

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public float getTextHeight() {
        if (this.textHeight != null) {
            return this.textHeight;
        }
        this.textHeight = PdfUtil.getFontHeight(getFont(), getFontSize());
        if (parameters.isWordBreak()) {
            final int size = PdfUtil.getOptimalTextBreakLines(getText(), getFont(), getFontSize(), getMaxWidth()).size();
            final float heightOfTextLines = size * this.textHeight;
            final float heightOfLineSpacing = (size - 1) * this.textHeight * getLineSpacing();
            this.textHeight = heightOfTextLines + heightOfLineSpacing;
        }
        return this.textHeight;
    }

    public float getWidthOfText() {
        assertIsRendered();
        final float notBrokenTextWidth = PdfUtil.getStringWidth(getText(), getFont(), getFontSize());
        if (parameters.isWordBreak()) {
            final float maxWidth = getMaxWidthOfText() - getHorizontalPadding();
            List<String> textLines = PdfUtil.getOptimalTextBreakLines(getText(), getFont(), getFontSize(), maxWidth);
            return textLines.stream()
                    .map(line -> PdfUtil.getStringWidth(line, getFont(), getFontSize()))
                    .max(Comparator.naturalOrder())
                    .orElse(notBrokenTextWidth);
        }
        return notBrokenTextWidth;
    }

    private float getMaxWidthOfText() {
        float columnsWidth = getColumn().getWidth();
        if (getColSpan() > 0) {
            Column currentColumn = getColumn();
            for (int i = 1; i < getColSpan(); i++) {
                columnsWidth += currentColumn.getNext().getWidth();
                currentColumn = currentColumn.getNext();
            }
        }
        return columnsWidth;
    }

    public float getMaxWidth() {
        return getMaxWidthOfText() - getHorizontalPadding();
    }
}
