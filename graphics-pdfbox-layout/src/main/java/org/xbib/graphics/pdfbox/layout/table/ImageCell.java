package org.xbib.graphics.pdfbox.layout.table;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import org.xbib.graphics.pdfbox.layout.table.render.ImageCellRenderer;

import java.awt.geom.Point2D;

public class ImageCell extends AbstractCell {

    private float scale = 1.0f;

    private PDImageXObject image;

    private float maxHeight;

    public void setImage(PDImageXObject image) {
        this.image = image;
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public PDImageXObject getImage() {
        return image;
    }

    @Override
    public float getMinHeight() {
        return Math.max((getFitSize().y + getVerticalPadding()), super.getMinHeight());
    }

    @Override
    protected Renderer createDefaultDrawer() {
        return new ImageCellRenderer(this);
    }

    public Point2D.Float getFitSize() {
        final Point2D.Float sizes = new Point2D.Float();
        float scaledWidth = image.getWidth() * getScale();
        float scaledHeight = image.getHeight() * getScale();
        final float resultingWidth = getWidth() - getHorizontalPadding();

        // maybe reduce the image to fit in column
        if (scaledWidth > resultingWidth) {
            scaledHeight = (resultingWidth / scaledWidth) * scaledHeight;
            scaledWidth = resultingWidth;
        }

        if (maxHeight > 0.0f && scaledHeight > maxHeight) {
            scaledWidth = (maxHeight / scaledHeight) * scaledWidth;
            scaledHeight = maxHeight;
        }

        sizes.x = scaledWidth;
        sizes.y = scaledHeight;

        return sizes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private PDImageXObject image;

        private Builder() {
        }

        public Builder image(PDImageXObject image) {
            this.image = image;
            return this;
        }

        public ImageCell build() {
            ImageCell cell = new ImageCell();
            cell.setImage(image);
            return cell;
        }
    }
}
