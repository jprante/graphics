package org.xbib.graphics.pdfbox.layout.util;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.elements.Dividable.Divided;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Indent;
import org.xbib.graphics.pdfbox.layout.text.NewLine;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.ReplacedWhitespace;
import org.xbib.graphics.pdfbox.layout.text.StyledText;
import org.xbib.graphics.pdfbox.layout.text.TextFlow;
import org.xbib.graphics.pdfbox.layout.text.TextFragment;
import org.xbib.graphics.pdfbox.layout.text.TextLine;
import org.xbib.graphics.pdfbox.layout.text.TextSequence;
import org.xbib.graphics.pdfbox.layout.text.WrappingNewLine;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for dealing with text sequences.
 */
public class TextSequenceUtil {

    /**
     * Dissects the given sequence into {@link TextLine}s.
     *
     * @param text the text to extract the lines from.
     * @return the list of text lines.
     */
    public static List<TextLine> getLines(final TextSequence text) {
        final List<TextLine> result = new ArrayList<>();
        TextLine line = new TextLine();
        for (TextFragment fragment : text) {
            if (fragment instanceof NewLine) {
                line.setNewLine((NewLine) fragment);
                result.add(line);
                line = new TextLine();
            } else if (!(fragment instanceof ReplacedWhitespace)) {
                line.add((StyledText) fragment);
            }
        }
        if (!line.isEmpty()) {
            result.add(line);
        }
        return result;
    }

    /**
     * Word-wraps and divides the given text sequence.
     *
     * @param text      the text to divide.
     * @param maxWidth  the max width used for word-wrapping.
     * @param maxHeight the max height for divide.
     * @return the Divided element containing the parts.
     */
    public static Divided divide(final TextSequence text, final float maxWidth,
                                 final float maxHeight) {
        TextFlow wrapped = wordWrap(text, maxWidth);
        List<TextLine> lines = getLines(wrapped);
        Paragraph first = new Paragraph();
        Paragraph tail = new Paragraph();
        if (text instanceof TextFlow) {
            TextFlow flow = (TextFlow) text;
            first.setMaxWidth(flow.getMaxWidth());
            first.setLineSpacing(flow.getLineSpacing());
            tail.setMaxWidth(flow.getMaxWidth());
            tail.setLineSpacing(flow.getLineSpacing());
        }
        if (text instanceof Paragraph) {
            Paragraph paragraph = (Paragraph) text;
            first.setAlignment(paragraph.getAlignment());
            first.setApplyLineSpacingToFirstLine(paragraph.isApplyLineSpacingToFirstLine());
            tail.setAlignment(paragraph.getAlignment());
            tail.setApplyLineSpacingToFirstLine(paragraph.isApplyLineSpacingToFirstLine());
        }
        int index = 0;
        do {
            TextLine line = lines.get(index);
            first.add(line);
            ++index;
        } while (index < lines.size() && first.getHeight() < maxHeight);
        if (first.getHeight() > maxHeight) {
            --index;
            TextLine line = lines.get(index);
            for (TextFragment textFragment : line) {
                first.removeLast();
            }
        }
        for (int i = index; i < lines.size(); ++i) {
            tail.add(lines.get(i));
        }
        return new Divided(first, tail);
    }

    /**
     * Word-wraps the given text sequence in order to fit the max width.
     *
     * @param text     the text to word-wrap.
     * @param maxWidth the max width to fit.
     * @return the word-wrapped text.
     */
    public static TextFlow wordWrap(TextSequence text, float maxWidth) {
        float indentation = 0;
        TextFlow result = new TextFlow();
        float lineLength = indentation;
        boolean isWrappedLine = false;
        for (TextFragment fragment : text) {
            if (fragment instanceof NewLine) {
                isWrappedLine = fragment instanceof WrappingNewLine;
                result.add(fragment);
                lineLength = indentation;
                if (indentation > 0) {
                    result.add(new Indent(indentation).toStyledText());
                }
            } else if (fragment instanceof Indent) {
                if (indentation > 0) {
                    // reset indentation
                    result.removeLast();
                    indentation = 0;
                }
                indentation = fragment.getWidth();
                lineLength = fragment.getWidth();
                result.add(((Indent) fragment).toStyledText());
            } else {
                TextFlow words = splitWords(fragment);
                for (TextFragment word : words) {
                    WordWrapContext context = new WordWrapContext(word,
                            lineLength, indentation, isWrappedLine);
                    do {
                        context = wordWrap(context, maxWidth, result);
                    } while (context.isMoreToWrap());

                    indentation = context.getIndentation();
                    lineLength = context.getLineLength();
                    isWrappedLine = context.isWrappedLine();
                }
            }
        }
        return result;
    }

    private static WordWrapContext wordWrap(WordWrapContext context, float maxWidth, TextFlow result) {
        TextFragment word = context.getWord();
        TextFragment moreToWrap = null;
        float indentation = context.getIndentation();
        float lineLength = context.getLineLength();
        boolean isWrappedLine = context.isWrappedLine();
        if (isWrappedLine && lineLength == indentation) {
            TextFragment[] replaceLeadingBlanks = replaceLeadingBlanks(word);
            word = replaceLeadingBlanks[0];
            if (replaceLeadingBlanks.length > 1) {
                result.add(replaceLeadingBlanks[1]);
            }
        }
        FontDescriptor fontDescriptor = word.getFontDescriptor();
        float length = word.getWidth();
        if (maxWidth > 0 && lineLength + length > maxWidth) {
            boolean breakHard = indentation + length > maxWidth;
            Pair<TextFragment> brokenWord = breakWord(word, length, maxWidth
                    - lineLength, maxWidth - indentation, breakHard);
            if (brokenWord != null) {
                word = brokenWord.getFirst();
                length = word.getWidth();
                moreToWrap = brokenWord.getSecond();
                result.add(word);
                if (length > 0) {
                    lineLength += length;
                }
            } else {
                if (lineLength == indentation) {
                    result.add(word);
                    if (length > 0) {
                        lineLength += length;
                    }
                } else {
                    moreToWrap = word;
                    if (result.getLast() != null) {
                        fontDescriptor = result.getLast().getFontDescriptor();
                    }
                }
            }
            if (lineLength > indentation) {
                result.add(new WrappingNewLine(fontDescriptor));
                isWrappedLine = true;
                if (indentation > 0) {
                    result.add(new Indent(indentation).toStyledText());
                }
                lineLength = indentation;
            }
        } else {
            result.add(word);
            if (length > 0) {
                lineLength += length;
            }
        }
        return new WordWrapContext(moreToWrap, lineLength, indentation, isWrappedLine);
    }

    /**
     * Replaces leading whitespace by {@link ReplacedWhitespace}.
     *
     * @param word the fragment to replace
     * @return text fragments
     */
    private static TextFragment[] replaceLeadingBlanks(final TextFragment word) {
        String text = word.getText();
        int splitIndex = 0;
        while (splitIndex < text.length()
                && Character.isWhitespace(text.charAt(splitIndex))) {
            ++splitIndex;
        }
        if (splitIndex == 0) {
            return new TextFragment[]{word};
        } else {
            ReplacedWhitespace whitespace = new ReplacedWhitespace(
                    text.substring(0, splitIndex), word.getFontDescriptor());
            StyledText newWord = null;
            if (word instanceof StyledText) {
                newWord = ((StyledText) word).inheritAttributes(text
                        .substring(splitIndex));
            } else {
                newWord = new StyledText(text.substring(splitIndex),
                        word.getFontDescriptor(), word.getColor());
            }
            return new TextFragment[]{newWord, whitespace};
        }
    }

    /**
     * De-wraps the given text, means any new lines introduced by wrapping will
     * be removed. Also all whitespace removed by wrapping are re-introduced.
     *
     * @param text the text to de-wrap.
     * @return the de-wrapped text.
     */
    public static TextFlow deWrap(TextSequence text) {
        TextFlow result = new TextFlow();
        for (TextFragment fragment : text) {
            if (fragment instanceof WrappingNewLine) {
                // skip
            } else if (fragment instanceof ReplacedWhitespace) {
                result.add(((ReplacedWhitespace) fragment).toReplacedFragment());
            } else {
                result.add(fragment);
            }
        }
        if (text instanceof TextFlow) {
            result.setLineSpacing(((TextFlow) text).getLineSpacing());
        }
        return result;
    }

    /**
     * Convencience function that {@link #wordWrap(TextSequence, float)
     * word-wraps} into {@link #getLines(TextSequence)}.
     *
     * @param text     the text to word-wrap.
     * @param maxWidth the max width to fit.
     * @return the word-wrapped text lines.
     */
    public static List<TextLine> wordWrapToLines(TextSequence text, float maxWidth) {
        return getLines(wordWrap(text, maxWidth));
    }

    /**
     * Splits the fragment into words.
     *
     * @param text the text to split.
     * @return the words as a text flow.
     */
    public static TextFlow splitWords(final TextFragment text) {
        TextFlow result = new TextFlow();
        if (text instanceof NewLine) {
            result.add(text);
        } else {
            float leftMargin = 0;
            float rightMargin = 0;
            if (text instanceof StyledText && ((StyledText) text).hasMargin()) {
                leftMargin = ((StyledText) text).getLeftMargin();
                rightMargin = ((StyledText) text).getRightMargin();
            }
            String[] words = text.getText().split(" ", -1);
            for (int index = 0; index < words.length; ++index) {
                String newWord = index == 0 ? words[index] : " " + words[index];
                float currentLeftMargin = 0;
                float currentRightMargin = 0;
                if (index == 0) {
                    currentLeftMargin = leftMargin;
                }
                if (index == words.length - 1) {
                    currentRightMargin = rightMargin;
                }
                TextFragment derived = deriveFromExisting(text, newWord,
                        currentLeftMargin, currentRightMargin);
                result.add(derived);
            }
        }
        return result;
    }

    /**
     * Derive a new TextFragment from an existing one, means use attributes like
     * font, color etc.
     *
     * @param toDeriveFrom the fragment to derive from.
     * @param text         the new text.
     * @param leftMargin   the new left margin.
     * @param rightMargin  the new right margin.
     * @return the derived text fragment.
     */
    protected static TextFragment deriveFromExisting(TextFragment toDeriveFrom, String text, float leftMargin,
                                                     float rightMargin) {
        if (toDeriveFrom instanceof StyledText) {
            return ((StyledText) toDeriveFrom).inheritAttributes(text,
                    leftMargin, rightMargin);
        }
        return new StyledText(text, toDeriveFrom.getFontDescriptor(), toDeriveFrom.getColor(), 0, leftMargin, rightMargin);
    }

    private static Pair<TextFragment> breakWord(TextFragment word,
                                                float wordWidth, final float remainingLineWidth, float maxWidth,
                                                boolean breakHard) {

        float leftMargin = 0;
        float rightMargin = 0;
        if (word instanceof StyledText) {
            StyledText styledText = (StyledText) word;
            leftMargin = styledText.getLeftMargin();
            rightMargin = styledText.getRightMargin();
        }

        Pair<String> brokenWord = WordBreakerFactory.getWorkBreaker().breakWord(word.getText(), word.getFontDescriptor(),
                        remainingLineWidth - leftMargin, breakHard);
        if (brokenWord == null) {
            return null;
        }

        // break at calculated index
        TextFragment head = deriveFromExisting(word,
                brokenWord.getFirst(), leftMargin, 0);
        TextFragment tail = deriveFromExisting(word,
                brokenWord.getSecond(), 0, rightMargin);

        return new Pair<TextFragment>(head, tail);
    }

    /**
     * Returns the width of the character <code>M</code> in the given font.
     *
     * @param fontDescriptor font and size.
     * @return the width of <code>M</code>.
     */
    public static float getEmWidth(final FontDescriptor fontDescriptor) {
        return getStringWidth("M", fontDescriptor);
    }

    /**
     * Returns the width of the given text in the given font.
     *
     * @param text           the text to measure.
     * @param fontDescriptor font and size.
     * @return the width of given text.
     */
    public static float getStringWidth(String text, FontDescriptor fontDescriptor) {
        try {
            return fontDescriptor.getSize() * fontDescriptor.getSelectedFont().getStringWidth(text) / 1000;
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Draws the given text sequence to the PDPageContentStream at the given
     * position.
     *
     * @param text                        the text to draw.
     * @param contentStream               the stream to draw to
     * @param upperLeft                   the position of the start of the first line.
     * @param drawListener                the listener to
     *                                    {@link DrawListener#drawn(Object, Position, float, float)
     *                                    notify} on drawn objects.
     * @param alignment                   how to align the text lines.
     * @param maxWidth                    if &gt; 0, the text may be word-wrapped to match the width.
     * @param lineSpacing                 the line spacing factor.
     * @param applyLineSpacingToFirstLine indicates if the line spacing should be applied to the first
     *                                    line also. Makes sense in most cases to do so.
     */
    public static void drawText(TextSequence text,
                                PDPageContentStream contentStream,
                                Position upperLeft,
                                DrawListener drawListener,
                                Alignment alignment,
                                float maxWidth, final float lineSpacing, final boolean applyLineSpacingToFirstLine) {
        List<TextLine> lines = wordWrapToLines(text, maxWidth);
        float maxLineWidth = Math.max(maxWidth, getMaxWidth(lines));
        Position position = upperLeft;
        float lastLineHeight = 0;
        for (int i = 0; i < lines.size(); i++) {
            boolean applyLineSpacing = i > 0 || applyLineSpacingToFirstLine;
            TextLine textLine = lines.get(i);
            float currentLineHeight = textLine.getHeight();
            float lead = lastLineHeight;
            if (applyLineSpacing) {
                lead += (currentLineHeight * (lineSpacing - 1));
            }
            lastLineHeight = currentLineHeight;
            position = position.add(0, -lead);
            textLine.drawAligned(contentStream, position, alignment, maxLineWidth, drawListener);
        }

    }

    /**
     * Gets the (left) offset of the line with respect to the target width and
     * alignment.
     *
     * @param textLine    the text
     * @param targetWidth the target width
     * @param alignment   the alignment of the line.
     * @return the left offset.
     */
    public static float getOffset(final TextSequence textLine,
                                  final float targetWidth, final Alignment alignment) {
        switch (alignment) {
            case RIGHT:
                return targetWidth - textLine.getWidth();
            case CENTER:
                return (targetWidth - textLine.getWidth()) / 2f;
            default:
                return 0;
        }
    }

    /**
     * Calculates the max width of all text lines.
     *
     * @param lines the lines for which to calculate the max width.
     * @return the max width of the lines.
     */
    public static float getMaxWidth(Iterable<TextLine> lines) {
        float max = 0;
        for (TextLine line : lines) {
            max = Math.max(max, line.getWidth());
        }
        return max;
    }

    /**
     * Calculates the width of the text
     *
     * @param textSequence the text.
     * @param maxWidth     if &gt; 0, the text may be word-wrapped to match the width.
     * @return the width of the text.
     */
    public static float getWidth(TextSequence textSequence, float maxWidth) {
        List<TextLine> lines = wordWrapToLines(textSequence, maxWidth);
        float max = 0;
        for (TextLine line : lines) {
            max = Math.max(max, line.getWidth());
        }
        return max;
    }

    /**
     * Calculates the height of the text
     *
     * @param textSequence                the text.
     * @param maxWidth                    if &gt; 0, the text may be word-wrapped to match the width.
     * @param lineSpacing                 the line spacing factor.
     * @param applyLineSpacingToFirstLine indicates if the line spacing should be applied to the first
     *                                    line also. Makes sense in most cases to do so.
     * @return the height of the text.
     */
    public static float getHeight(final TextSequence textSequence,
                                  final float maxWidth, final float lineSpacing,
                                  final boolean applyLineSpacingToFirstLine) {
        List<TextLine> lines = wordWrapToLines(textSequence, maxWidth);
        float sum = 0;
        for (int i = 0; i < lines.size(); i++) {
            boolean applyLineSpacing = i > 0 || applyLineSpacingToFirstLine;
            TextLine line = lines.get(i);
            float lineHeight = line.getHeight();
            if (applyLineSpacing) {
                lineHeight *= lineSpacing;
            }
            sum += lineHeight;
        }
        return sum;
    }

    private static class WordWrapContext {

        private final TextFragment word;

        private final float lineLength;

        private final float indentation;

        boolean isWrappedLine;

        public WordWrapContext(TextFragment word, float lineLength,
                               float indentation, boolean isWrappedLine) {
            this.word = word;
            this.lineLength = lineLength;
            this.indentation = indentation;
            this.isWrappedLine = isWrappedLine;
        }

        public TextFragment getWord() {
            return word;
        }

        public float getLineLength() {
            return lineLength;
        }

        public float getIndentation() {
            return indentation;
        }

        public boolean isWrappedLine() {
            return isWrappedLine;
        }

        public boolean isMoreToWrap() {
            return getWord() != null;
        }
    }
}