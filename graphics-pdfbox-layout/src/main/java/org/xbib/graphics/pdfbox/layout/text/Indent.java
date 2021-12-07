package org.xbib.graphics.pdfbox.layout.text;

import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import java.awt.Color;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Control fragment that represents a indent in text.
 */
public class Indent extends ControlFragment {

    public static final FontDescriptor DEFAULT_FONT_DESCRIPTOR = new FontDescriptor(BaseFont.HELVETICA, 11);

    /**
     * Constant for the indentation of 0.
     */
    public static final Indent UNINDENT = new Indent(0);

    protected Alignment alignment = Alignment.LEFT;

    protected StyledText styledText;

    /**
     * Creates a new line with the given font descriptor.
     *
     * @param indentWidth the indentation.
     * @param indentUnit  the indentation unit.
     */
    public Indent(float indentWidth, SpaceUnit indentUnit) {
        this("", indentWidth, indentUnit, DEFAULT_FONT_DESCRIPTOR, Alignment.LEFT, Color.black);
    }

    /**
     * Creates a new line with the
     * {@link #DEFAULT_FONT_DESCRIPTOR}'s font and the given
     * height.
     *
     * @param label       the label of the indentation.
     * @param indentWidth the indentation.
     * @param indentUnit  the indentation unit.
     * @param descriptor    the font size, resp. the height of the new line, the font to use.
     */
    public Indent(String label, float indentWidth, SpaceUnit indentUnit, FontDescriptor descriptor) {
        this(label, indentWidth, indentUnit, descriptor, Alignment.LEFT, Color.black);
    }

    /**
     * Creates a new line with the
     * {@link #DEFAULT_FONT_DESCRIPTOR}'s font and the given
     * height.
     *
     * @param label       the label of the indentation.
     * @param indentWidth the indentation.
     * @param indentUnit  the indentation unit.
     * @param fontDescriptor    the font size, resp. the height of the new line, the font to use.
     * @param alignment   the alignment of the label.
     */
    public Indent(String label,
                  float indentWidth,
                  SpaceUnit indentUnit,
                  FontDescriptor fontDescriptor,
                  Alignment alignment) {
        this(label, indentWidth, indentUnit, fontDescriptor, alignment, Color.black);
    }

    /**
     * Creates a new line with the given font descriptor.
     *
     * @param label          the label of the indentation.
     * @param indentWidth    the indentation width.
     * @param indentUnit     the indentation unit.
     * @param fontDescriptor the font and size associated with this new line.
     * @param alignment      the alignment of the label.
     * @param color          the color to use.
     */
    public Indent(String label,
                  float indentWidth,
                  SpaceUnit indentUnit,
                  FontDescriptor fontDescriptor,
                  Alignment alignment,
                  Color color) {
        super("INDENT", label, fontDescriptor, color);
        if (label == null) {
            return;
        }
        try {
            float indent = calculateIndent(indentWidth, indentUnit, fontDescriptor);
            float textWidth = fontDescriptor.getSize() * fontDescriptor.getSelectedFont().getStringWidth(label) / 1000f;
            float marginLeft = 0;
            float marginRight = 0;
            if (textWidth < indent) {
                switch (alignment) {
                    case LEFT:
                        marginRight = indent - textWidth;
                        break;
                    case RIGHT:
                        marginLeft = indent - textWidth;
                        break;
                    default:
                        marginLeft = (indent - textWidth) / 2f;
                        marginRight = marginLeft;
                        break;
                }
            }
            styledText = new StyledText(label, fontDescriptor, getColor(), 0, marginLeft, marginRight);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Directly creates an indent of the given width in pt.
     *
     * @param indentPt the indentation in pt.
     */
    public Indent(final float indentPt) {
        super("", DEFAULT_FONT_DESCRIPTOR);
        styledText = new StyledText("", getFontDescriptor(), getColor(), 0, indentPt, 0);
    }

    private float calculateIndent(float indentWidth, SpaceUnit indentUnit, final FontDescriptor fontDescriptor)
            throws IOException {
        if (indentWidth < 0) {
            return 0;
        }
        return indentUnit.toPt(indentWidth, fontDescriptor);
    }

    @Override
    public float getWidth() {
        return styledText.getWidth();
    }

    public StyledText toStyledText() {
        return styledText;
    }

    @Override
    public String toString() {
        return "ControlFragment [" + getName() + ", " + styledText + "]";
    }
}
