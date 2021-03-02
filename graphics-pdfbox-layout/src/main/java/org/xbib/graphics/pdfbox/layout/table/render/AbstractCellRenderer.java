package org.xbib.graphics.pdfbox.layout.table.render;

import static org.xbib.graphics.pdfbox.layout.table.VerticalAlignment.BOTTOM;
import static org.xbib.graphics.pdfbox.layout.table.VerticalAlignment.MIDDLE;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.table.AbstractCell;

import java.awt.Color;
import java.awt.geom.Point2D;

public abstract class AbstractCellRenderer<T extends AbstractCell> implements Renderer {

    protected T cell;

    public AbstractCellRenderer<T> withCell(T cell) {
        this.cell = cell;
        return this;
    }

    @Override
    public void renderBackground(RenderContext renderContext) {
        if (cell.hasBackgroundColor()) {
            final PDPageContentStream contentStream = renderContext.getContentStream();
            final Point2D.Float start = renderContext.getStartingPoint();
            final float rowHeight = cell.getRow().getHeight();
            final float height = Math.max(cell.getHeight(), rowHeight);
            final float y = rowHeight < cell.getHeight()
                    ? start.y + rowHeight - cell.getHeight()
                    : start.y;
            PositionedRectangle positionedRectangle =
                    new PositionedRectangle(start.x, y, cell.getWidth(), height, cell.getBackgroundColor());
            RenderUtil.drawRectangle(contentStream, positionedRectangle);
        }
    }

    @Override
    public abstract void renderContent(RenderContext renderContext);

    @Override
    public void renderBorders(RenderContext renderContext) {
        final Point2D.Float start = renderContext.getStartingPoint();
        final PDPageContentStream contentStream = renderContext.getContentStream();
        final float cellWidth = cell.getWidth();
        final float rowHeight = cell.getRow().getHeight();
        final float height = Math.max(cell.getHeight(), rowHeight);
        final float sY = rowHeight < cell.getHeight()
                ? start.y + rowHeight - cell.getHeight()
                : start.y;
        final Color cellBorderColor = cell.getBorderColor();
        final Color rowBorderColor = cell.getRow().getSettings().getBorderColor();
        if (cell.hasBorderTop() || cell.hasBorderBottom()) {
            final float correctionLeft = cell.getBorderWidthLeft() / 2;
            final float correctionRight = cell.getBorderWidthRight() / 2;
            if (cell.hasBorderTop()) {
                PositionedLine line = new PositionedLine();
                line.setStartX(start.x - correctionLeft);
                line.setStartY(start.y + rowHeight);
                line.setEndX(start.x + cellWidth + correctionRight);
                line.setEndY(start.y + rowHeight);
                line.setWidth(cell.getBorderWidthTop());
                line.setColor(cellBorderColor);
                line.setResetColor(rowBorderColor);
                line.setBorderStyle(cell.getBorderStyleTop());
                RenderUtil.drawLine(contentStream, line);
            }
            if (cell.hasBorderBottom()) {
                PositionedLine line = new PositionedLine();
                line.setStartX(start.x - correctionLeft);
                line.setStartY(sY);
                line.setEndX(start.x + cellWidth + correctionRight);
                line.setEndY(sY);
                line.setWidth(cell.getBorderWidthBottom());
                line.setColor(cellBorderColor);
                line.setResetColor(rowBorderColor);
                line.setBorderStyle(cell.getBorderStyleBottom());
                RenderUtil.drawLine(contentStream, line);
            }
        }
        if (cell.hasBorderLeft() || cell.hasBorderRight()) {
            final float correctionTop = cell.getBorderWidthTop() / 2;
            final float correctionBottom = cell.getBorderWidthBottom() / 2;
            if (cell.hasBorderLeft()) {
                PositionedLine line = new PositionedLine();
                line.setStartX(start.x);
                line.setStartY(sY - correctionBottom);
                line.setEndX(start.x);
                line.setEndY(sY + height + correctionTop);
                line.setWidth(cell.getBorderWidthLeft());
                line.setColor(cellBorderColor);
                line.setResetColor(rowBorderColor);
                line.setBorderStyle(cell.getBorderStyleLeft());
                RenderUtil.drawLine(contentStream, line);
            }
            if (cell.hasBorderRight()) {
                PositionedLine line = new PositionedLine();
                line.setStartX(start.x + cellWidth);
                line.setStartY(sY - correctionBottom);
                line.setEndX(start.x + cellWidth);
                line.setEndY(sY + height + correctionTop);
                line.setWidth(cell.getBorderWidthRight());
                line.setColor(cellBorderColor);
                line.setResetColor(rowBorderColor);
                line.setBorderStyle(cell.getBorderStyleRight());
                RenderUtil.drawLine(contentStream, line);
            }
        }
    }

    protected boolean rowHeightIsBiggerThanOrEqualToCellHeight() {
        return cell.getRow().getHeight() > cell.getHeight() ||
                isEqualInEpsilon(cell.getRow().getHeight(), cell.getHeight());
    }

    protected float getRowSpanAdaption() {
        return cell.getRowSpan() > 1 ? cell.calculateHeightForRowSpan() - cell.getRow().getHeight() : 0;
    }

    protected float calculateOuterHeight() {
        return cell.getRowSpan() > 1 ? cell.getHeight() : cell.getRow().getHeight();
    }

    protected float getAdaptionForVerticalAlignment() {
        if (rowHeightIsBiggerThanOrEqualToCellHeight() || cell.getRowSpan() > 1) {
            if (cell.isVerticallyAligned(MIDDLE)) {
                return (calculateOuterHeight() / 2 + calculateInnerHeight() / 2) - getRowSpanAdaption();
            } else if (cell.isVerticallyAligned(BOTTOM)) {
                return (calculateInnerHeight() + cell.getPaddingBottom()) - getRowSpanAdaption();
            }
        }
        return cell.getRow().getHeight() - cell.getPaddingTop();
    }

    protected abstract float calculateInnerHeight();

    private static boolean isEqualInEpsilon(float x, float y) {
        return Math.abs(y - x) < 0.0001;
    }
}
