package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import org.xbib.graphics.pdfbox.layout.table.render.VerticalTextCellRenderer;

public class VerticalTextCell extends AbstractTextCell {

    private String text;

    protected Renderer createDefaultDrawer() {
        return new VerticalTextCellRenderer(this);
    }

    @Override
    public String getText() {
        return text;
    }
}
