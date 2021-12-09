package org.xbib.graphics.pdfbox.layout.table.render;

import static org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment.CENTER;
import static org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment.JUSTIFY;
import static org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment.RIGHT;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.table.AbstractTextCell;
import org.xbib.graphics.pdfbox.layout.util.PdfUtil;
import org.xbib.graphics.pdfbox.layout.util.RenderUtil;

import java.awt.Color;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;

public class TextCellRenderer<T extends AbstractTextCell> extends AbstractCellRenderer<AbstractTextCell> {

    public TextCellRenderer(T cell) {
        this.cell = cell;
    }

    @Override
    public void renderContent(RenderContext renderContext) {
        float startX = renderContext.getStartingPoint().x;
        Font currentFont = cell.getFont();
        float currentFontSize = cell.getFontSize();
        Color currentTextColor = cell.getTextColor();
        float yOffset = renderContext.getStartingPoint().y + getAdaptionForVerticalAlignment();
        float xOffset = startX + cell.getPaddingLeft();
        final List<String> lines = calculateAndGetLines(currentFont, currentFontSize, cell.getMaxWidth());
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            yOffset -= calculateYOffset(currentFont, currentFontSize, i);
            final float textWidth = PdfUtil.getStringWidth(line, currentFont, currentFontSize);
            if (cell.isHorizontallyAligned(RIGHT)) {
                xOffset = startX + (cell.getWidth() - (textWidth + cell.getPaddingRight()));
            } else if (cell.isHorizontallyAligned(CENTER)) {
                xOffset = startX + (cell.getWidth() - textWidth) / 2;
            } else if (cell.isHorizontallyAligned(JUSTIFY) && isNotLastLine(lines, i)) {
                try {
                    renderContext.getContentStream().setCharacterSpacing(calculateCharSpacingFor(line));
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            }
            PositionedStyledText positionedStyledText = new PositionedStyledText(xOffset, yOffset, line,
                    currentFont, currentFontSize, currentTextColor);
            RenderUtil.drawText(renderContext.getContentStream(), positionedStyledText);
        }
    }

    @Override
    protected float calculateInnerHeight() {
        return cell.getTextHeight();
    }

    private float calculateYOffset(Font currentFont, float currentFontSize, int lineIndex) {
        return PdfUtil.getFontHeight(currentFont, currentFontSize) +
                (lineIndex > 0 ? PdfUtil.getFontHeight(currentFont, currentFontSize) * cell.getLineSpacing() : 0f);
    }

    private static boolean isNotLastLine(List<String> lines, int i) {
        return i != lines.size() - 1;
    }

    private float calculateCharSpacingFor(String line) {
        float charSpacing = 0;
        if (line.length() > 1) {
            float size = PdfUtil.getStringWidth(line, cell.getFont(), cell.getFontSize());
            float free = cell.getWidthOfText() - size;
            if (free > 0) {
                charSpacing = free / (line.length() - 1);
            }
        }
        return charSpacing;
    }

    private List<String> calculateAndGetLines(Font currentFont, float currentFontSize, float maxWidth) {
        return cell.isWordBreak()
                ? PdfUtil.getOptimalTextBreakLines(cell.getText(), currentFont, currentFontSize, maxWidth)
                : Collections.singletonList(cell.getText());
    }
}
