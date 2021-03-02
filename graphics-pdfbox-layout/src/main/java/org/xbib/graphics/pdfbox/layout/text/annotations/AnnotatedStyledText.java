package org.xbib.graphics.pdfbox.layout.text.annotations;

import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.text.StyledText;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Extension of styled text that supports annotations.
 */
public class AnnotatedStyledText extends StyledText implements Annotated {

    private final List<Annotation> annotations = new ArrayList<>();

    /**
     * Creates a styled text.
     *
     * @param text           the text to draw. Must not contain line feeds ('\n').
     * @param fontDescriptor the font to use.
     * @param color          the color to use.
     * @param baselineOffset the offset of the baseline.
     * @param leftMargin     the margin left to the text.
     * @param rightMargin    the margin right to the text.
     * @param annotations    the annotations associated with the text.
     */
    public AnnotatedStyledText(final String text,
                               final FontDescriptor fontDescriptor,
                               final Color color,
                               final float leftMargin,
                               final float rightMargin,
                               final float baselineOffset,
                               Collection<? extends Annotation> annotations) {
        super(text, fontDescriptor, color, baselineOffset, leftMargin, rightMargin);
        if (annotations != null) {
            this.annotations.addAll(annotations);
        }
    }

    @Override
    public Iterator<Annotation> iterator() {
        return annotations.iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> Iterable<T> getAnnotationsOfType(Class<T> type) {
        List<T> result = null;
        for (Annotation annotation : annotations) {
            if (type.isAssignableFrom(annotation.getClass())) {
                if (result == null) {
                    result = new ArrayList<T>();
                }
                result.add((T) annotation);
            }
        }

        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * Adds an annotation.
     *
     * @param annotation the annotation to add.
     */
    public void addAnnotation(final Annotation annotation) {
        annotations.add(annotation);
    }

    /**
     * Adds all annotations.
     *
     * @param annos the annotations to add.
     */
    public void addAllAnnotation(final Collection<Annotation> annos) {
        annotations.addAll(annos);
    }

    @Override
    public AnnotatedStyledText inheritAttributes(String text, float leftMargin,
                                                 float rightMargin) {
        return new AnnotatedStyledText(text, getFontDescriptor(), getColor(),
                getBaselineOffset(), leftMargin, rightMargin, annotations);
    }
}
