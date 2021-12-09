package org.xbib.graphics.pdfbox.layout.table.render;

import org.xbib.graphics.pdfbox.layout.elements.Drawable;
import org.xbib.graphics.pdfbox.layout.table.DrawableCell;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.WidthRespecting;

import java.io.IOException;
import java.io.UncheckedIOException;

public class DrawableCellRenderer extends AbstractCellRenderer<DrawableCell> {

    public DrawableCellRenderer(DrawableCell cell) {
        this.cell = cell;
    }

    @Override
    public void renderContent(RenderContext renderContext) {
        Drawable drawable = cell.getDrawable();
        if (drawable instanceof WidthRespecting) {
            WidthRespecting widthRespecting = (WidthRespecting) drawable;
            widthRespecting.setMaxWidth(cell.getWidth());
        }
        float x = renderContext.getStartingPoint().x + cell.getPaddingLeft();
        float y = renderContext.getStartingPoint().y + getAdaptionForVerticalAlignment();
        Position position = new Position(x, y);
        try {
            drawable.draw(renderContext.getPdDocument(), renderContext.getContentStream(), position,null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected float calculateInnerHeight() {
        return 0;
    }
}
