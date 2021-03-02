package org.xbib.graphics.pdfbox.layout.text.annotations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.xbib.graphics.pdfbox.layout.text.DrawContext;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.annotations.Annotations.AnchorAnnotation;
import org.xbib.graphics.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation;
import org.xbib.graphics.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation.LinkStyle;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This annotation processor handles both {@link HyperlinkAnnotation}s and
 * {@link AnchorAnnotation}s, and adds the needed hyperlink metadata to the PDF
 * document.
 */
public class HyperlinkAnnotationProcessor implements AnnotationProcessor {

    private final Map<String, PageAnchor> anchorMap = new HashMap<>();

    private final Map<PDPage, List<Hyperlink>> linkMap = new HashMap<>();

    @Override
    public void annotatedObjectDrawn(Annotated drawnObject,
                                     DrawContext drawContext,
                                     Position upperLeft,
                                     float width,
                                     float height) {
        if (!(drawnObject instanceof AnnotatedStyledText)) {
            return;
        }
        AnnotatedStyledText annotatedText = (AnnotatedStyledText) drawnObject;
        handleHyperlinkAnnotations(annotatedText, drawContext, upperLeft, width, height);
        handleAnchorAnnotations(annotatedText, drawContext, upperLeft);
    }

    protected void handleAnchorAnnotations(AnnotatedStyledText annotatedText,
                                           DrawContext drawContext,
                                           Position upperLeft) {
        Iterable<AnchorAnnotation> anchorAnnotations =
                annotatedText.getAnnotationsOfType(AnchorAnnotation.class);
        for (AnchorAnnotation anchorAnnotation : anchorAnnotations) {
            anchorMap.put(anchorAnnotation.getAnchor(),
                    new PageAnchor(drawContext.getCurrentPage(), upperLeft.getX(), upperLeft.getY()));
        }
    }

    protected void handleHyperlinkAnnotations(AnnotatedStyledText annotatedText,
                                              DrawContext drawContext,
                                              Position upperLeft,
                                              float width,
                                              float height) {
        Iterable<HyperlinkAnnotation> hyperlinkAnnotations = annotatedText
                .getAnnotationsOfType(HyperlinkAnnotation.class);
        for (HyperlinkAnnotation hyperlinkAnnotation : hyperlinkAnnotations) {
            List<Hyperlink> links = linkMap.computeIfAbsent(drawContext.getCurrentPage(), k -> new ArrayList<>());
            PDRectangle bounds = new PDRectangle();
            bounds.setLowerLeftX(upperLeft.getX());
            bounds.setLowerLeftY(upperLeft.getY() - height);
            bounds.setUpperRightX(upperLeft.getX() + width);
            bounds.setUpperRightY(upperLeft.getY());
            links.add(new Hyperlink(bounds, annotatedText.getColor(),
                    hyperlinkAnnotation.getLinkStyle(), hyperlinkAnnotation.getHyperlinkURI()));
        }
    }

    @Override
    public void beforePage(DrawContext drawContext) {
        // nothing to do here
    }

    @Override
    public void afterPage(DrawContext drawContext) {
        // nothing to do here
    }

    @Override
    public void afterRender(PDDocument document) {
        for (Entry<PDPage, List<Hyperlink>> entry : linkMap.entrySet()) {
            PDPage page = entry.getKey();
            List<Hyperlink> links = entry.getValue();
            for (Hyperlink hyperlink : links) {
                PDAnnotationLink pdLink;
                if (hyperlink.getHyperlinkURI().startsWith("#")) {
                    pdLink = createGotoLink(hyperlink);
                } else {
                    pdLink = createLink(page,
                            hyperlink.getRect(), hyperlink.getColor(),
                            hyperlink.getLinkStyle(),
                            hyperlink.getHyperlinkURI());
                }
                try {
                    page.getAnnotations().add(pdLink);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

        }
    }

    private static PDAnnotationLink createLink(PDPage page,
                                               PDRectangle rect,
                                               Color color,
                                               LinkStyle linkStyle,
                                               String uri) {
        PDAnnotationLink pdLink = createLink(page, rect, color, linkStyle);
        PDActionURI actionUri = new PDActionURI();
        actionUri.setURI(uri);
        pdLink.setAction(actionUri);
        return pdLink;
    }

    private static PDBorderStyleDictionary noBorder;

    private static PDAnnotationLink createLink(PDPage page,
                                               PDRectangle rect,
                                               Color color,
                                               LinkStyle linkStyle) {
        PDAnnotationLink pdLink = new PDAnnotationLink();
        if (linkStyle == LinkStyle.none) {
            if (noBorder == null) {
                noBorder = new PDBorderStyleDictionary();
                noBorder.setWidth(0);
            }
            return pdLink;
        }
        PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
        borderStyle.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
        pdLink.setBorderStyle(borderStyle);
        PDRectangle rotatedRect = transformToPageRotation(rect, page);
        pdLink.setRectangle(rotatedRect);
        pdLink.setColor(toPDColor(color));
        return pdLink;
    }

    private static PDAnnotationLink createLink(PDPage page,
                                               PDRectangle rect,
                                               Color color,
                                               LinkStyle linkStyle,
                                               PDDestination destination) {
        PDAnnotationLink pdLink = createLink(page, rect, color, linkStyle);
        PDActionGoTo gotoAction = new PDActionGoTo();
        gotoAction.setDestination(destination);
        pdLink.setAction(gotoAction);
        return pdLink;
    }

    private static PDRectangle transformToPageRotation(PDRectangle rect,
                                                       PDPage page) {
        AffineTransform transform = transformToPageRotation(page);
        if (transform == null) {
            return rect;
        }
        float[] points = {
                rect.getLowerLeftX(),
                rect.getLowerLeftY(),
                rect.getUpperRightX(),
                rect.getUpperRightY()
        };
        float[] rotatedPoints = new float[4];
        transform.transform(points, 0, rotatedPoints, 0, 2);
        PDRectangle rotated = new PDRectangle();
        rotated.setLowerLeftX(rotatedPoints[0]);
        rotated.setLowerLeftY(rotatedPoints[1]);
        rotated.setUpperRightX(rotatedPoints[2]);
        rotated.setUpperRightY(rotatedPoints[3]);
        return rotated;
    }

    private static AffineTransform transformToPageRotation(PDPage page) {
        int pageRotation = page.getRotation();
        if (pageRotation == 0) {
            return null;
        }
        float pageWidth = page.getMediaBox().getHeight();
        float pageHeight = page.getMediaBox().getWidth();
        AffineTransform transform = new AffineTransform();
        transform.rotate(pageRotation * Math.PI / 180, pageHeight / 2, pageWidth / 2);
        double offset = Math.abs(pageHeight - pageWidth) / 2;
        transform.translate(-offset, offset);
        return transform;
    }

    private static PDColor toPDColor(final Color color) {
        float[] components = {
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f
        };
        return new PDColor(components, PDDeviceRGB.INSTANCE);
    }

    private PDAnnotationLink createGotoLink(Hyperlink hyperlink) {
        String anchor = hyperlink.getHyperlinkURI().substring(1);
        PageAnchor pageAnchor = anchorMap.get(anchor);
        if (pageAnchor == null) {
            throw new IllegalArgumentException(String.format("anchor named '%s' not found", anchor));
        }
        PDPageXYZDestination xyzDestination = new PDPageXYZDestination();
        xyzDestination.setPage(pageAnchor.getPage());
        xyzDestination.setLeft((int) pageAnchor.getX());
        xyzDestination.setTop((int) pageAnchor.getY());
        return createLink(pageAnchor.getPage(), hyperlink.getRect(),
                hyperlink.getColor(), hyperlink.getLinkStyle(), xyzDestination);
    }

    private static class PageAnchor {

        private final PDPage page;

        private final float x;

        private final float y;

        public PageAnchor(PDPage page, float x, float y) {
            this.page = page;
            this.x = x;
            this.y = y;
        }

        public PDPage getPage() {
            return page;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        @Override
        public String toString() {
            return "PageAnchor [page=" + page + ", x=" + x + ", y=" + y + "]";
        }
    }

    private static class Hyperlink {

        private final PDRectangle rect;

        private final Color color;

        private final String hyperlinkUri;

        private final LinkStyle linkStyle;

        public Hyperlink(PDRectangle rect, Color color, LinkStyle linkStyle,
                         String hyperlinkUri) {
            this.rect = rect;
            this.color = color;
            this.hyperlinkUri = hyperlinkUri;
            this.linkStyle = linkStyle;
        }

        public PDRectangle getRect() {
            return rect;
        }

        public Color getColor() {
            return color;
        }

        public String getHyperlinkURI() {
            return hyperlinkUri;
        }

        public LinkStyle getLinkStyle() {
            return linkStyle;
        }

        @Override
        public String toString() {
            return "Hyperlink [rect=" + rect + ", color=" + color
                    + ", hyperlinkUri=" + hyperlinkUri + ", linkStyle="
                    + linkStyle + "]";
        }
    }
}
