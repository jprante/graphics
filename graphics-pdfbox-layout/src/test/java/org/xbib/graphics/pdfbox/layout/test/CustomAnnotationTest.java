package org.xbib.graphics.pdfbox.layout.test;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.PageFormat;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.text.DrawContext;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.annotations.Annotated;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotatedStyledText;
import org.xbib.graphics.pdfbox.layout.text.annotations.Annotation;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotationCharacters;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotationCharacters.AnnotationControlCharacter;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotationCharacters.AnnotationControlCharacterFactory;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotationProcessor;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotationProcessorFactory;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomAnnotationTest {

    /**
     * Represents a highlight annotation that might be added to a
     * {@link AnnotatedStyledText}.
     */
    public static class HighlightAnnotation implements Annotation {

        private final Color color;

        public HighlightAnnotation(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    /**
     * Processes {@link HighlightAnnotation}s by adding a colored highlight to
     * the pdf.
     */
    public static class HighlightAnnotationProcessor implements
            AnnotationProcessor {

        @Override
        public void annotatedObjectDrawn(Annotated drawnObject,
                                         DrawContext drawContext, Position upperLeft, float width,
                                         float height) {
            Iterable<HighlightAnnotation> HighlightAnnotations = drawnObject
                    .getAnnotationsOfType(HighlightAnnotation.class);
            for (HighlightAnnotation highlightAnnotation : HighlightAnnotations) {
                // use PDF text markup to implement the highlight
                PDAnnotationTextMarkup markup = new PDAnnotationTextMarkup(
                        PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
                // use the bounding box of the drawn object to position the
                // highlight
                PDRectangle bounds = new PDRectangle();
                bounds.setLowerLeftX(upperLeft.getX());
                bounds.setLowerLeftY(upperLeft.getY() - height);
                bounds.setUpperRightX(upperLeft.getX() + width);
                bounds.setUpperRightY(upperLeft.getY() + 1);
                markup.setRectangle(bounds);
                float[] quadPoints = toQuadPoints(bounds);
                quadPoints = transformToPageRotation(quadPoints, drawContext.getCurrentPage());
                markup.setQuadPoints(quadPoints);
                // set the highlight color if given
                if (highlightAnnotation.getColor() != null) {
                    markup.setColor(toPDColor(highlightAnnotation.getColor()));
                }
                // finally add the markup to the PDF
                try {
                    drawContext.getCurrentPage().getAnnotations().add(markup);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        @Override
        public void beforePage(DrawContext drawContext) {
            // nothing to do here for us
        }

        @Override
        public void afterPage(DrawContext drawContext) {
            // nothing to do here for us
        }

        @Override
        public void afterRender(PDDocument document) {
            // nothing to do here for us
        }

    }

    /**
     * The control character is a representation of the parsed markup. It
     * contains any information passed by the markup necessary for rendering, in
     * our case here it is just the color for the highlight.
     */
    public static class HighlightControlCharacter extends AnnotationControlCharacter<HighlightAnnotation> {

        private final HighlightAnnotation annotation;

        protected HighlightControlCharacter(final Color color) {
            super("HIGHLIGHT", HighlightControlCharacterFactory.TO_ESCAPE);
            annotation = new HighlightAnnotation(color);
        }

        @Override
        public HighlightAnnotation getAnnotation() {
            return annotation;
        }

        @Override
        public Class<HighlightAnnotation> getAnnotationType() {
            return HighlightAnnotation.class;
        }
    }

    /**
     * Provides a regex pattern to match the highlight markup, and creates an
     * appropriate control character. In our case here the markup syntax is
     * either <code>{hl}</code> or with optional color information
     * <code>{hl:#ee22aa}</code>, where the color is given as hex RGB code
     * (ee22aa in this case). It can be escaped with a backslash ('\').
     */
    private static class HighlightControlCharacterFactory implements
            AnnotationControlCharacterFactory<HighlightControlCharacter> {

        private final static Pattern PATTERN = Pattern
                .compile("(?<!\\\\)(\\\\\\\\)*\\{hl(:#(\\p{XDigit}{6}))?\\}");

        private final static String TO_ESCAPE = "{";

        @Override
        public HighlightControlCharacter createControlCharacter(String text,
                                                                Matcher matcher, final List<CharSequence> charactersSoFar) {
            Color color = null;
            String hex = matcher.group(3);
            if (hex != null) {
                int r = Integer.parseUnsignedInt(hex.substring(0, 2), 16);
                int g = Integer.parseUnsignedInt(hex.substring(2, 4), 16);
                int b = Integer.parseUnsignedInt(hex.substring(4, 6), 16);
                color = new Color(r, g, b);
            }
            return new HighlightControlCharacter(color);
        }

        @Override
        public Pattern getPattern() {
            return PATTERN;
        }

        @Override
        public String unescape(String text) {
            return text
                    .replaceAll("\\\\" + Pattern.quote(TO_ESCAPE), TO_ESCAPE);
        }

        @Override
        public boolean patternMatchesBeginOfLine() {
            return false;
        }

    }

    @Test
    public void test() throws Exception {

        // register our custom highlight annotation processor
        AnnotationProcessorFactory.register(HighlightAnnotationProcessor.class);

        Document document = new Document(PageFormat.with().A4()
                .margins(40, 60, 40, 60).portrait().build());

        Paragraph paragraph = new Paragraph();
        paragraph.addText("Hello there, here is ", 10, BaseFont.HELVETICA);

        // now add some annotated text using our custom highlight annotation
        HighlightAnnotation annotation = new HighlightAnnotation(Color.green);
        FontDescriptor fontDescriptor = new FontDescriptor(BaseFont.HELVETICA, 10);
        AnnotatedStyledText highlightedText = new AnnotatedStyledText(
                "highlighted text", fontDescriptor,  Color.black, 0f, 0, 0,
                Collections.singleton(annotation));
        paragraph.add(highlightedText);
        paragraph.addText(". Do whatever you want here...strike, squiggle, whatsoever\n\n",
                        10, BaseFont.HELVETICA);
        paragraph.setMaxWidth(150);
        document.add(paragraph);

        // register markup processing for the highlight annotation
        AnnotationCharacters.register(new HighlightControlCharacterFactory());
        paragraph = new Paragraph();
        paragraph.addMarkup("Hello there, here is {hl:#ffff00}highlighted text{hl}. "
                                + "Do whatever you want here...strike, squiggle, whatsoever\n\n",
                        10, BaseFont.HELVETICA);
        paragraph.setMaxWidth(150);
        document.add(paragraph);
        final OutputStream outputStream = new FileOutputStream("build/customannotation.pdf");
        document.save(outputStream);
    }

    private static PDColor toPDColor(final Color color) {
        float[] components = {
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f
        };
        return new PDColor(components, PDDeviceRGB.INSTANCE);
    }

    private static float[] transformToPageRotation(final float[] quadPoints, final PDPage page) {
        AffineTransform transform = transformToPageRotation(page);
        if (transform == null) {
            return quadPoints;
        }
        float[] rotatedPoints = new float[quadPoints.length];
        transform.transform(quadPoints, 0, rotatedPoints, 0, 4);
        return rotatedPoints;
    }

    private static AffineTransform transformToPageRotation(final PDPage page) {
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

    /**
     * Return the quad points representation of the given rect.
     *
     * @param rect the rectangle.
     * @return the quad points.
     */
    public static float[] toQuadPoints(final PDRectangle rect) {
        return toQuadPoints(rect, 0, 0);
    }

    /**
     * Return the quad points representation of the given rect.
     *
     * @param rect    the rectangle.
     * @param xOffset the offset in x-direction to add.
     * @param yOffset the offset in y-direction to add.
     * @return the quad points.
     */
    private static float[] toQuadPoints(final PDRectangle rect, float xOffset, float yOffset) {
        float[] quads = new float[8];
        quads[0] = rect.getLowerLeftX() + xOffset; // x1
        quads[1] = rect.getUpperRightY() + yOffset; // y1
        quads[2] = rect.getUpperRightX() + xOffset; // x2
        quads[3] = quads[1]; // y2
        quads[4] = quads[0]; // x3
        quads[5] = rect.getLowerLeftY() + yOffset; // y3
        quads[6] = quads[2]; // x4
        quads[7] = quads[5]; // y5
        return quads;
    }
}
