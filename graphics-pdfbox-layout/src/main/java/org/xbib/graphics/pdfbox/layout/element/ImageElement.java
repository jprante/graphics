package org.xbib.graphics.pdfbox.layout.element;

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

    private BufferedImage image;

    private float width;

    private float height;

    private float scaleX = 1.0f;

    private float scaleY = 1.0f;

    private float maxWidth = -1;

    private Position absolutePosition;

    public ImageElement() {
    }

    public void setImage(String base64) throws IOException {
        setImage(ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64))));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleY() {
        return scaleY;
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
        return width * scaleX;
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
        return height * scaleY;
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
        float y = upperLeft.getY() - getHeight();
        PDImageXObject imageXObject = LosslessFactory.createFromImage(pdDocument, image);
        contentStream.drawImage(imageXObject, x, y, getWidth(), getHeight());
        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, getWidth(), getHeight());
        }
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() {
        return this;
    }
}
