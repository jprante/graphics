package org.xbib.graphics.pdfbox.layout.table.render;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment;
import org.xbib.graphics.pdfbox.layout.table.ImageCell;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ImageCellRenderer extends AbstractCellRenderer<ImageCell> {

    public ImageCellRenderer(ImageCell cell) {
        this.cell = cell;
    }

    @Override
    public void renderContent(RenderContext renderContext) {
        final PDPageContentStream contentStream = renderContext.getContentStream();
        final float moveX = renderContext.getStartingPoint().x;
        final Point2D.Float size = cell.getFitSize();
        final Point2D.Float drawAt = new Point2D.Float();
        float xOffset = moveX + cell.getPaddingLeft();
        if (cell.getSettings().getHorizontalAlignment() == HorizontalAlignment.RIGHT) {
            xOffset = moveX + (cell.getWidth() - (size.x + cell.getPaddingRight()));
        } else if (cell.getSettings().getHorizontalAlignment() == HorizontalAlignment.CENTER) {
            final float diff = (cell.getWidth() - size.x) / 2;
            xOffset = moveX + diff;
        }
        drawAt.x = xOffset;
        drawAt.y = renderContext.getStartingPoint().y + getAdaptionForVerticalAlignment() - size.y;
        try {
            contentStream.drawImage(cell.getImage(), drawAt.x, drawAt.y, size.x, size.y);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    protected float calculateInnerHeight() {
        return (float) cell.getFitSize().getY();
    }
}
