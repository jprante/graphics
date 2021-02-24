package org.xbib.graphics.pdfbox.image;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Image;

/**
 * Encode and compress an image as PDImageXObject
 */
public interface ImageEncoder {
    /**
     * Encode the given image into the a PDImageXObject
     *
     * @param document      the PDF document
     * @param contentStream the content stream of the page
     * @param image         the image to encode
     * @return the encoded image
     */
    PDImageXObject encodeImage(PDDocument document, PDPageContentStream contentStream, Image image);
}
