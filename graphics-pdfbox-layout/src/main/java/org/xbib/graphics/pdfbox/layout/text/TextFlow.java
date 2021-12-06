package org.xbib.graphics.pdfbox.layout.text;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A text flow is a text sequence that {@link WidthRespecting respects a given
 * width} by word wrapping the text. The text may contain line breaks ('\n').<br>
 * In order to ease creation of styled text, this class supports a kind of
 * {@link #addMarkup(String, float, Font) markup}. The following raw text
 *
 * <pre>
 * Markup supports *bold*, _italic_, and *even _mixed* markup_.
 * </pre>
 * <p>
 * is rendered like this:
 *
 * <pre>
 * Markup supports <b>bold</b>, <em>italic</em>, and <b>even <em>mixed</b> markup</em>.
 * </pre>
 * <p>
 * Use backslash to escape special characters '*', '_' and '\' itself:
 *
 * <pre>
 * Escape \* with \\\* and \_ with \\\_ in markup.
 * </pre>
 * <p>
 * is rendered like this:
 *
 * <pre>
 * Escape * with \* and _ with \_ in markup.
 * </pre>
 */
public class TextFlow implements TextSequence, WidthRespecting {

    public static final float DEFAULT_LINE_SPACING = 1.2f;

    private static final String HEIGHT = "height";

    private static final String WIDTH = "width";

    private final Map<String, Object> cache = new HashMap<>();

    private final List<TextFragment> text = new ArrayList<>();

    private float lineSpacing = DEFAULT_LINE_SPACING;

    private float maxWidth = -1;

    private boolean applyLineSpacingToFirstLine = true;

    private void clearCache() {
        cache.clear();
    }

    private void setCachedValue(final String key, Object value) {
        cache.put(key, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getCachedValue(final String key, Class<T> type) {
        return (T) cache.get(key);
    }

    public void addText(String text, float fontSize, Font font) {
        add(TextFlowUtil.createTextFlow(text, new FontDescriptor(font, fontSize)));
    }

    public void addMarkup(String markup, FontDescriptor fontDescriptor) {
        add(TextFlowUtil.createTextFlowFromMarkup(markup, fontDescriptor));
    }

    public void addMarkup(String markup, float fontSize, Font font) {
        add(TextFlowUtil.createTextFlowFromMarkup(markup, new FontDescriptor(font, fontSize)));
    }

    public void addIndent(String label, float indentWidth, SpaceUnit indentUnit, float fontsize, Font font) {
        add(new Indent(label, indentWidth, indentUnit, new FontDescriptor(font, fontsize)));
    }

    public void addIndent(String label, float indentWidth, SpaceUnit indentUnit, float fontsize, Font font, Alignment alignment) {
        add(new Indent(label, indentWidth, indentUnit, new FontDescriptor(font, fontsize), alignment));
    }

    /**
     * Adds a text sequence to this flow.
     *
     * @param sequence the sequence to add.
     */
    public void add(TextSequence sequence) {
        for (TextFragment fragment : sequence) {
            add(fragment);
        }
    }

    /**
     * Adds a text fragment to this flow.
     *
     * @param fragment the fragment to add.
     */
    public void add(TextFragment fragment) {
        text.add(fragment);
        clearCache();
    }

    /**
     * Removes the last added fragment.
     *
     * @return the removed fragment (if any).
     */
    public TextFragment removeLast() {
        if (text.size() > 0) {
            clearCache();
            return text.remove(text.size() - 1);
        }
        return null;
    }

    /**
     * @return the last added fragment (if any).
     */
    public TextFragment getLast() {
        if (text.size() > 0) {
            clearCache();
            return text.get(text.size() - 1);
        }
        return null;
    }

    /**
     * @return <code>true</code> if this flow does not contain any fragments.
     */
    public boolean isEmpty() {
        return text.isEmpty();
    }

    @Override
    public Iterator<TextFragment> iterator() {
        return text.iterator();
    }

    @Override
    public float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        clearCache();
    }

    /**
     * @return the factor multiplied with the height to calculate the line
     * spacing.
     */
    public float getLineSpacing() {
        return lineSpacing;
    }

    /**
     * Sets the factor multiplied with the height to calculate the line spacing.
     *
     * @param lineSpacing the line spacing factor.
     */
    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
        clearCache();
    }

    /**
     * Indicates if the line spacing should be applied to the first line. Makes
     * sense if there is text above to achieve an equal spacing. In case you
     * want to position the text precisely on top, you may set this value to
     * <code>false</code>. Default is <code>true</code>.
     *
     * @return <code>true</code> if the line spacing should be applied to the
     * first line.
     */
    public boolean isApplyLineSpacingToFirstLine() {
        return applyLineSpacingToFirstLine;
    }

    /**
     * Sets the indicator whether to apply line spacing to the first line.
     *
     * @param applyLineSpacingToFirstLine <code>true</code> if the line spacing should be applied to the
     *                                    first line.
     * @see TextFlow#isApplyLineSpacingToFirstLine()
     */
    public void setApplyLineSpacingToFirstLine(
            boolean applyLineSpacingToFirstLine) {
        this.applyLineSpacingToFirstLine = applyLineSpacingToFirstLine;
    }

    @Override
    public float getWidth() {
        Float width = getCachedValue(WIDTH, Float.class);
        if (width == null) {
            width = TextSequenceUtil.getWidth(this, getMaxWidth());
            setCachedValue(WIDTH, width);
        }
        return width;
    }

    @Override
    public float getHeight() {
        Float height = getCachedValue(HEIGHT, Float.class);
        if (height == null) {
            height = TextSequenceUtil.getHeight(this, getMaxWidth(),
                    getLineSpacing(), isApplyLineSpacingToFirstLine());
            setCachedValue(HEIGHT, height);
        }
        return height;
    }

    @Override
    public void drawText(PDPageContentStream contentStream, Position upperLeft,
                         Alignment alignment, DrawListener drawListener) {
        TextSequenceUtil.drawText(this, contentStream, upperLeft, drawListener, alignment,
                getMaxWidth(), getLineSpacing(),
                isApplyLineSpacingToFirstLine());
    }

    public void drawTextRightAligned(PDPageContentStream contentStream,
                                     Position endOfFirstLine, DrawListener drawListener) {
        drawText(contentStream, endOfFirstLine.add(-getWidth(), 0),
                Alignment.RIGHT, drawListener);
    }

    /**
     * @return a copy of this text flow where all leading {@link NewLine}s are removed.
     */
    public TextFlow removeLeadingEmptyLines() {
        if (text.size() == 0 || !(text.get(0) instanceof NewLine)) {
            return this;
        }
        TextFlow result = createInstance();
        result.setApplyLineSpacingToFirstLine(this.isApplyLineSpacingToFirstLine());
        result.setLineSpacing(this.getLineSpacing());
        result.setMaxWidth(this.getMaxWidth());
        for (TextFragment fragment : this) {
            if (!result.isEmpty() || !(fragment instanceof NewLine)) {
                result.add(fragment);
            }
        }
        return result;
    }

    protected TextFlow createInstance() {
        return new TextFlow();
    }

    @Override
    public String toString() {
        return "TextFlow [text=" + text + "]";
    }

}
