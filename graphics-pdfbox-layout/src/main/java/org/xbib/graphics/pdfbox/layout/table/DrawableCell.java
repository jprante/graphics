package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.element.Drawable;
import org.xbib.graphics.pdfbox.layout.table.render.DrawableCellRenderer;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;

public class DrawableCell extends AbstractCell {

    private Drawable drawable;

    public DrawableCell() {
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    protected Renderer createDefaultRenderer() {
        return new DrawableCellRenderer(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Drawable drawable;

        private final Parameters parameters;

        private int colSpan;

        private int rowSpan;

        private Builder() {
            this.parameters = new Parameters();
        }

        public Builder drawable(Drawable drawable) {
            this.drawable = drawable;
            return this;
        }

        public Builder colSpan(int colSpan) {
            this.colSpan = colSpan;
            return this;
        }

        public Builder rowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
            return this;
        }

        public DrawableCell build() {
            DrawableCell cell = new DrawableCell();
            cell.setDrawable(drawable);
            cell.setParameters(parameters);
            if (colSpan > 0) {
                cell.setColSpan(colSpan);
            }
            if (rowSpan > 0) {
                cell.setRowSpan(rowSpan);
            }
            return cell;
        }
    }
}
