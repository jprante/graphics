package org.xbib.graphics.pdfbox.layout.elements.render;

import org.xbib.graphics.pdfbox.layout.text.Alignment;

/**
 * The column layout hint provides currently the same possibilities as the
 * {@link VerticalLayoutHint}. See there for more details.
 */
public class ColumnLayoutHint extends VerticalLayoutHint {

    public final static ColumnLayoutHint LEFT = new ColumnLayoutHint(Alignment.LEFT);

    public final static ColumnLayoutHint CENTER = new ColumnLayoutHint(Alignment.CENTER);

    public final static ColumnLayoutHint RIGHT = new ColumnLayoutHint(Alignment.RIGHT);

    /**
     * Creates a layout hint with {@link Alignment#LEFT left alignment}.
     */
    public ColumnLayoutHint() {
        super(Alignment.LEFT);
    }

    /**
     * Creates a layout hint with the given alignment.
     *
     * @param alignment the element alignment.
     */
    public ColumnLayoutHint(Alignment alignment) {
        super(alignment);
    }

    /**
     * Creates a layout hint with the given alignment and margins.
     *
     * @param alignment    the element alignment.
     * @param marginLeft   the left alignment.
     * @param marginRight  the right alignment.
     * @param marginTop    the top alignment.
     * @param marginBottom the bottom alignment.
     */
    public ColumnLayoutHint(Alignment alignment, float marginLeft, float marginRight, float marginTop, float marginBottom) {
        super(alignment, marginLeft, marginRight, marginTop, marginBottom);
    }

    /**
     * Creates a layout hint with the given alignment and margins.
     *
     * @param alignment    the element alignment.
     * @param marginLeft   the left alignment.
     * @param marginRight  the right alignment.
     * @param marginTop    the top alignment.
     * @param marginBottom the bottom alignment.
     * @param resetY       if <code>true</code>, the y coordinate will be reset to the
     *                     point before layouting the element.
     */
    public ColumnLayoutHint(Alignment alignment, float marginLeft, float marginRight, float marginTop, float marginBottom,
                            boolean resetY) {
        super(alignment, marginLeft, marginRight, marginTop, marginBottom, resetY);
    }

    /**
     * @return a {@link VerticalLayoutHintBuilder} for creating a
     * {@link VerticalLayoutHint} using a fluent API.
     */
    public static ColumnLayoutHintBuilder builder() {
        return new ColumnLayoutHintBuilder();
    }

    /**
     * A builder for creating a {@link VerticalLayoutHint} using a fluent API.
     */
    public static class ColumnLayoutHintBuilder extends VerticalLayoutHintBuilder {

        public ColumnLayoutHint build() {
            return new ColumnLayoutHint(alignment, marginLeft, marginRight,
                    marginTop, marginBottom, resetY);
        }

    }
}
