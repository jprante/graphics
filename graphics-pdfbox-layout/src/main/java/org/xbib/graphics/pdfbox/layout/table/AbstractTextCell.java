package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.font.Font;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractTextCell extends AbstractCell {

    protected float lineSpacing = 1f;

    public Font getFont() {
        return settings.getFont();
    }

    public Integer getFontSize() {
        return settings.getFontSize();
    }

    public Color getTextColor() {
        return settings.getTextColor();
    }

    private Float textHeight;

    public abstract String getText();

    public float getLineSpacing() {
        return lineSpacing;
    }

    @Override
    public float getMinHeight() {
        return Math.max((getVerticalPadding() + getTextHeight()), super.getMinHeight());
    }

    public float getTextHeight() {
        if (this.textHeight != null) {
            return this.textHeight;
        }
        this.textHeight = PdfUtil.getFontHeight(getFont(), getFontSize());
        if (settings.isWordBreak()) {
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
        if (settings.isWordBreak()) {
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
        if (getColSpan() > 1) {
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

    /*public abstract static class AbstractTextCellBuilder<C extends AbstractTextCell, B extends AbstractTextCell.AbstractTextCellBuilder<C, B>> extends AbstractCellBuilder<C, B> {

        public B font(final Font font) {
            settings.setFont(font);
            return (B) this;
        }

        public B fontSize(final Integer fontSize) {
            settings.setFontSize(fontSize);
            return (B) this;
        }

        public B textColor(final Color textColor) {
            settings.setTextColor(textColor);
            return (B) this;
        }

    }*/
}