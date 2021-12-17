package org.xbib.graphics.pdfbox.layout.element;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.xbib.graphics.pdfbox.layout.element.render.VerticalLayout;

import java.util.Locale;
import java.util.Objects;

import static org.xbib.graphics.pdfbox.layout.util.PdfUtil.MM_TO_UNITS;

/**
 * Defines the size and orientation of a page. The default is A4 portrait without margins.
 */
public class PageFormat implements Element {

    public static final PDRectangle A0 = new PDRectangle(Math.round(841f * MM_TO_UNITS), Math.round(1189f * MM_TO_UNITS));

    public static final PDRectangle A1 = new PDRectangle(Math.round(594f * MM_TO_UNITS), Math.round(841f * MM_TO_UNITS));

    public static final PDRectangle A2 = new PDRectangle(Math.round(420f * MM_TO_UNITS), Math.round(594f * MM_TO_UNITS));

    public static final PDRectangle A3 = new PDRectangle(Math.round(297f * MM_TO_UNITS), Math.round(420f * MM_TO_UNITS));

    public static final PDRectangle A4 = new PDRectangle(Math.round(210f * MM_TO_UNITS), Math.round(297f * MM_TO_UNITS));

    public static final PDRectangle A5 = new PDRectangle(Math.round(148f * MM_TO_UNITS), Math.round(210f * MM_TO_UNITS));

    public static final PDRectangle A6 = new PDRectangle(Math.round(105f * MM_TO_UNITS), Math.round(148f * MM_TO_UNITS));

    public static final PDRectangle Letter = new PDRectangle(Math.round(215.9f * MM_TO_UNITS), Math.round(279.4f * MM_TO_UNITS));

    private final float marginLeft;

    private final float marginRight;

    private final float marginTop;

    private final float marginBottom;

    private final PDRectangle mediaBox;

    private final Orientation orientation;

    private final int rotation;

    /**
     * Creates a PageFormat with A4 portrait without margins.
     */
    public PageFormat() {
        this(A4);
    }

    /**
     * Creates a PageFormat with a given size and orientation portrait.
     *
     * @param mediaBox the size.
     */
    public PageFormat(PDRectangle mediaBox) {
        this(mediaBox, Orientation.PORTRAIT);
    }

    /**
     * Creates a PageFormat with a given size and orientation.
     *
     * @param mediaBox    the size.
     * @param orientation the orientation.
     */
    public PageFormat(PDRectangle mediaBox, Orientation orientation) {
        this(mediaBox, orientation, 0, 0, 0, 0);
    }

    /**
     * Creates a Document based on the given media box and margins. By default,
     * a {@link VerticalLayout} is used.
     *
     * @param mediaBox     the media box to use.
     * @param orientation  the orientation to use.
     * @param marginLeft   the left margin
     * @param marginRight  the right margin
     * @param marginTop    the top margin
     * @param marginBottom the bottom margin
     */
    public PageFormat(PDRectangle mediaBox, Orientation orientation,
                      float marginLeft, float marginRight, float marginTop, float marginBottom) {
        this(mediaBox, orientation, 0, marginLeft, marginRight, marginTop, marginBottom);
    }

    /**
     * Creates a Document based on the given media box and margins. By default,
     * a {@link VerticalLayout} is used.
     *
     * @param mediaBox     the media box to use.
     * @param orientation  the orientation to use.
     * @param rotation     the rotation to apply to the page after rendering.
     * @param marginLeft   the left margin
     * @param marginRight  the right margin
     * @param marginTop    the top margin
     * @param marginBottom the bottom margin
     */
    public PageFormat(PDRectangle mediaBox, Orientation orientation, int rotation,
                      float marginLeft, float marginRight, float marginTop, float marginBottom) {
        this.mediaBox = mediaBox;
        this.orientation = orientation;
        this.rotation = rotation;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    /**
     * @return the orientation to use.
     */
    public Orientation getOrientation() {
        if (orientation != null) {
            return orientation;
        }
        if (getMediaBox().getWidth() > getMediaBox().getHeight()) {
            return Orientation.LANDSCAPE;
        }
        return Orientation.PORTRAIT;
    }

    /**
     * @return the rotation to apply to the page after rendering.
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * @return the left document margin.
     */
    public float getMarginLeft() {
        return marginLeft;
    }

    /**
     * @return the right document margin.
     */
    public float getMarginRight() {
        return marginRight;
    }

    /**
     * @return the top document margin.
     */
    public float getMarginTop() {
        return marginTop;
    }

    /**
     * @return the bottom document margin.
     */
    public float getMarginBottom() {
        return marginBottom;
    }

    /**
     * @return the media box to use.
     */
    public PDRectangle getMediaBox() {
        return mediaBox;
    }

    /**
     * @return a page format builder. The default of the builder is A4 portrait
     * without margins.
     */
    public static PageFormatBuilder builder() {
        return new PageFormatBuilder();
    }

    public static class PageFormatBuilder {

        private float marginLeft;

        private float marginRight;

        private float marginTop;

        private float marginBottom;

        private PDRectangle mediaBox = A4;

        private Orientation orientation;

        private int rotation;

        protected PageFormatBuilder() {
        }

        /**
         * Sets the left margin.
         *
         * @param marginLeft the left margin to use.
         * @return the builder.
         */
        public PageFormatBuilder marginLeft(float marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        /**
         * Sets the right margin.
         *
         * @param marginRight the right margin to use.
         * @return the builder.
         */
        public PageFormatBuilder marginRight(float marginRight) {
            this.marginRight = marginRight;
            return this;
        }

        /**
         * Sets the top margin.
         *
         * @param marginTop the top margin to use.
         * @return the builder.
         */
        public PageFormatBuilder marginTop(float marginTop) {
            this.marginTop = marginTop;
            return this;
        }

        /**
         * Sets the bottom margin.
         *
         * @param marginBottom the bottom margin to use.
         * @return the builder.
         */
        public PageFormatBuilder marginBottom(float marginBottom) {
            this.marginBottom = marginBottom;
            return this;
        }

        /**
         * Sets the margins.
         *
         * @param marginLeft   the left margin to use.
         * @param marginRight  the right margin to use.
         * @param marginTop    the top margin to use.
         * @param marginBottom the bottom margin to use.
         * @return the builder.
         */
        public PageFormatBuilder margins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
            this.marginLeft = marginLeft;
            this.marginRight = marginRight;
            this.marginTop = marginTop;
            this.marginBottom = marginBottom;
            return this;
        }

        /**
         * Sets the media box to the given size.
         *
         * @param mediaBox the media box to use.
         * @return the builder.
         */
        public PageFormatBuilder mediaBox(PDRectangle mediaBox) {
            this.mediaBox = mediaBox;
            return this;
        }

        /**
         * Sets the media box to size {@link #A0}.
         *
         * @return the builder.
         */
        public PageFormatBuilder A0() {
            this.mediaBox = A0;
            return this;
        }

        /**
         * Sets the media box to size {@link #A1}.
         *
         * @return the builder.
         */
        public PageFormatBuilder A1() {
            this.mediaBox = A1;
            return this;
        }

        /**
         * Sets the media box to size {@link #A2}.
         *
         * @return the builder.
         */
        public PageFormatBuilder A2() {
            this.mediaBox = A2;
            return this;
        }

        /**
         * Sets the media box to size {@link #A3}.
         *
         * @return the builder.
         */
        public PageFormatBuilder A3() {
            this.mediaBox = A3;
            return this;
        }

        /**
         * Sets the media box to size {@link #A4}.
         *
         * @return the builder.
         */
        public PageFormatBuilder A4() {
            this.mediaBox = A4;
            return this;
        }

        /**
         * Sets the media box to size {@link #A5}.
         *
         * @return the builder.
         */
        public PageFormatBuilder A5() {
            this.mediaBox = A5;
            return this;
        }

        /**
         * Sets the media box to size {@link #A6}.
         *
         * @return the builder.
         */
        public PageFormatBuilder A6() {
            this.mediaBox = A6;
            return this;
        }

        /**
         * Sets the media box to size {@link #Letter}.
         *
         * @return the builder.
         */
        public PageFormatBuilder letter() {
            this.mediaBox = Letter;
            return this;
        }

        /**
         * Sets the orientation to the given one.
         *
         * @param orientation the orientation to use.
         * @return the builder.
         */
        public PageFormatBuilder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public PageFormatBuilder orientation(String orientation) {
            this.orientation = Orientation.valueOf(orientation);
            return this;
        }

        /**
         * Sets the orientation to {@link Orientation#PORTRAIT}.
         *
         * @return the builder.
         */
        public PageFormatBuilder portrait() {
            this.orientation = Orientation.PORTRAIT;
            return this;
        }

        /**
         * Sets the orientation to {@link Orientation#LANDSCAPE}.
         *
         * @return the builder.
         */
        public PageFormatBuilder landscape() {
            this.orientation = Orientation.LANDSCAPE;
            return this;
        }

        /**
         * Sets the rotation to apply to the page after rendering.
         *
         * @param angle the angle to rotate.
         * @return the builder.
         */
        public PageFormatBuilder rotation(int angle) {
            this.rotation = angle;
            return this;
        }

        public PageFormatBuilder pageFormat(String format) {
            Objects.requireNonNull(format);
            switch (format.toUpperCase(Locale.ROOT)) {
                case "A0" :
                    A0();
                    break;
                case "A1" :
                    A1();
                    break;
                case "A2" :
                    A2();
                    break;
                case "A3" :
                    A3();
                    break;
                case "A4" :
                    A4();
                    break;
                case "A5" :
                    A5();
                    break;
                case "A6" :
                    A6();
                    break;
                case "LETTER" :
                    letter();
                    break;
            }
            return this;
        }

        /**
         * Actually builds the PageFormat.
         *
         * @return the resulting PageFormat.
         */
        public PageFormat build() {
            return new PageFormat(mediaBox, orientation, rotation, marginLeft, marginRight, marginTop, marginBottom);
        }
    }
}
