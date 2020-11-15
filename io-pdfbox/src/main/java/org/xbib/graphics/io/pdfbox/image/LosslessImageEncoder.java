package org.xbib.graphics.io.pdfbox.image;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Encodes all images using lossless compression. Tries to reuse images as much
 * as possible. You can share an instance of this class with multiple
 * PdfBoxGraphics2D objects.
 */
public class LosslessImageEncoder implements ImageEncoder {

    private Map<ImageSoftReference, SoftReference<PDImageXObject>> imageMap = new HashMap<>();

    private Map<ProfileSoftReference, SoftReference<PDColorSpace>> profileMap = new HashMap<>();

    private SoftReference<PDDocument> doc;

    @Override
    public PDImageXObject encodeImage(PDDocument document, PDPageContentStream contentStream, Image image) {
        final BufferedImage bi;

        if (image instanceof BufferedImage) {
            bi = (BufferedImage) image;
        } else {
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics graphics = bi.getGraphics();
            if (!graphics.drawImage(image, 0, 0, null, null)) {
                throw new IllegalStateException("Not fully loaded images are not supported");
            }
            graphics.dispose();
        }
        try {
            if (doc == null || doc.get() != document) {
                imageMap = new HashMap<>();
                profileMap = new HashMap<>();
                doc = new SoftReference<>(document);
            }
            SoftReference<PDImageXObject> pdImageXObjectSoftReference = imageMap.get(new ImageSoftReference(image));
            PDImageXObject imageXObject = pdImageXObjectSoftReference == null ? null
                    : pdImageXObjectSoftReference.get();
            if (imageXObject == null) {
                imageXObject = LosslessFactory.createFromImage(document, bi);
                if (bi.getColorModel().getColorSpace() instanceof ICC_ColorSpace) {
                    ICC_Profile profile = ((ICC_ColorSpace) bi.getColorModel().getColorSpace()).getProfile();
                    if (((ICC_ColorSpace) bi.getColorModel().getColorSpace()).getProfile() != ICC_Profile
                            .getInstance(ColorSpace.CS_sRGB)) {
                        SoftReference<PDColorSpace> pdProfileRef = profileMap.get(new ProfileSoftReference(profile));
                        PDColorSpace pdProfile = pdProfileRef == null ? null : pdProfileRef.get();
                        if (pdProfile == null) {
                            pdProfile = imageXObject.getColorSpace();
                            if (pdProfile instanceof PDICCBased) {
                                profileMap.put(new ProfileSoftReference(profile),
                                        new SoftReference<>(pdProfile));
                            }
                        }
                        imageXObject.setColorSpace(pdProfile);
                    }
                }
                imageMap.put(new ImageSoftReference(image), new SoftReference<>(imageXObject));
            }
            return imageXObject;
        } catch (IOException e) {
            throw new RuntimeException("Could not encode Image", e);
        }
    }

    private static class ImageSoftReference extends SoftReference<Image> {
        ImageSoftReference(Image referent) {
            super(referent);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ImageSoftReference)) {
                return false;
            }
            return ((ImageSoftReference) obj).get() == get();
        }

        @Override
        public int hashCode() {
            Image image = get();
            if (image == null) {
                return 0;
            }
            return image.hashCode();
        }
    }

    private static class ProfileSoftReference extends SoftReference<ICC_Profile> {
        ProfileSoftReference(ICC_Profile referent) {
            super(referent);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ProfileSoftReference)) {
                return false;
            }
            return ((ProfileSoftReference) obj).get() == get();
        }

        @Override
        public int hashCode() {
            ICC_Profile image = get();
            if (image == null) {
                return 0;
            }
            return image.hashCode();
        }
    }
}
