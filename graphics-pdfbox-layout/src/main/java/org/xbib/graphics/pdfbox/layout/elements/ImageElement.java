package org.xbib.graphics.pdfbox.layout.elements;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.WidthRespecting;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageElement implements Element, Drawable, Dividable, WidthRespecting {

    /**
     * Set this to {@link #setWidth(float)} resp. {@link #setHeight(float)}
     * (usually both) in order to respect the {@link WidthRespecting width}.
     */
    public final static float SCALE_TO_RESPECT_WIDTH = -1f;

    private final BufferedImage image;

    private float width;

    private float height;

    private float scale;

    private float maxWidth = -1;

    private Position absolutePosition;

    public ImageElement(String base64) throws IOException {
        this(ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64))));
    }

    public ImageElement(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public void setScale(float scale) {
        this.scale = scale;
        setWidth(width * scale);
        setHeight(height * scale);
    }

    public float getScale() {
        return scale;
    }

    /**
     * Sets the width. Default is the image width. Set to
     * {@link #SCALE_TO_RESPECT_WIDTH} in order to let the image
     * {@link WidthRespecting respect any given width}.
     *
     * @param width the width to use.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getWidth() throws IOException {
        if (width == SCALE_TO_RESPECT_WIDTH) {
            if (getMaxWidth() > 0 && image.getWidth() > getMaxWidth()) {
                return getMaxWidth();
            }
            return image.getWidth();
        }
        return width;
    }

    /**
     * Sets the height. Default is the image height. Set to
     * {@link #SCALE_TO_RESPECT_WIDTH} in order to let the image
     * {@link WidthRespecting respect any given width}. Usually this makes only
     * sense if you also set the width to {@link #SCALE_TO_RESPECT_WIDTH}.
     *
     * @param height the height to use.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public float getHeight() throws IOException {
        if (height == SCALE_TO_RESPECT_WIDTH) {
            if (getMaxWidth() > 0 && image.getWidth() > getMaxWidth()) {
                return getMaxWidth() / (float) image.getWidth() * (float) image.getHeight();
            }
            return image.getHeight();
        }
        return height;
    }

    @Override
    public Divided divide(float remainingHeight, float nextPageHeight) throws IOException {
        if (getHeight() <= nextPageHeight) {
            return new Divided(new VerticalSpacer(remainingHeight), this);
        }
        return new Cutter(this).divide(remainingHeight, nextPageHeight);
    }

    @Override
    public float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public Position getAbsolutePosition() {
        return absolutePosition;
    }

    /**
     * Sets the absolute position to render at.
     *
     * @param absolutePosition the absolute position.
     */
    public void setAbsolutePosition(Position absolutePosition) {
        this.absolutePosition = absolutePosition;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
                     Position upperLeft, DrawListener drawListener) throws IOException {
        float x = upperLeft.getX();
        float y = upperLeft.getY() - height;
        PDImageXObject imageXObject = LosslessFactory.createFromImage(pdDocument, image);
        contentStream.drawImage(imageXObject, x, y, width, height);
        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, getWidth(), getHeight());
        }
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() {
        return this;
    }
}
