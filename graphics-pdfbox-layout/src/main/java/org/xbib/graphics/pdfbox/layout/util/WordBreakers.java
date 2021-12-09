package org.xbib.graphics.pdfbox.layout.util;

import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container class for the default word breakers.
 */
public class WordBreakers {

    /**
     * May by used for legacy compatibility, does not break at all.
     */
    public static class NonBreakingWordBreaker implements WordBreaker {

        @Override
        public Pair<String> breakWord(String word,
                                      FontDescriptor fontDescriptor, float maxWidth,
                                      boolean breakHardIfNecessary) {
            return null;
        }

    }

    /**
     * Abstract base class for implementing (custom) word breakers. Tries to
     * break the word {@link #breakWordSoft(String, FontDescriptor, float)
     * softly}, or - if this is not possible -
     * {@link #breakWordHard(String, FontDescriptor, float) hard}.
     */
    public static abstract class AbstractWordBreaker implements WordBreaker {

        @Override
        public Pair<String> breakWord(final String word,
                                      final FontDescriptor fontDescriptor, final float maxWidth,
                                      final boolean breakHardIfNecessary) {

            Pair<String> brokenWord = breakWordSoft(word, fontDescriptor,
                    maxWidth);
            if (brokenWord == null && breakHardIfNecessary) {
                brokenWord = breakWordHard(word, fontDescriptor, maxWidth);
            }
            return brokenWord;
        }

        /**
         * To be implemented by subclasses. Give your best to break the word
         * softly using your strategy, otherwise return <code>null</code>.
         *
         * @param word           the word to break.
         * @param fontDescriptor describing the font's type and size.
         * @param maxWidth       the maximum width to obey.
         * @return the broken word, or <code>null</code> if it cannot be broken.
         */
        abstract protected Pair<String> breakWordSoft(String word,
                                                      FontDescriptor fontDescriptor, final float maxWidth);

        /**
         * Breaks the word hard at the outermost position that fits the given
         * max width.
         *
         * @param word           the word to break.
         * @param fontDescriptor describing the font's type and size.
         * @param maxWidth       the maximum width to obey.
         * @return the broken word, or <code>null</code> if it cannot be broken.
         */
        protected Pair<String> breakWordHard(final String word,
                                             final FontDescriptor fontDescriptor, final float maxWidth) {
            int cutIndex = (int) (maxWidth / TextSequenceUtil.getEmWidth(fontDescriptor));
            float currentWidth = TextSequenceUtil.getStringWidth(word.substring(0, cutIndex),
                    fontDescriptor);
            if (currentWidth > maxWidth) {
                while (currentWidth > maxWidth) {
                    --cutIndex;
                    currentWidth = TextSequenceUtil.getStringWidth(word.substring(0, cutIndex),
                            fontDescriptor);
                }
                ++cutIndex;
            } else if (currentWidth < maxWidth) {
                while (currentWidth < maxWidth) {
                    ++cutIndex;
                    currentWidth = TextSequenceUtil.getStringWidth(word.substring(0, cutIndex),
                            fontDescriptor);
                }
                --cutIndex;
            }

            return new Pair<String>(word.substring(0, cutIndex),
                    word.substring(cutIndex));
        }

    }

    /**
     * Breaks a word if one of the following characters is found after a
     * non-digit letter:
     * <ul>
     * <li>.</li>
     * <li>,</li>
     * <li>-</li>
     * <li>/</li>
     * </ul>
     */
    public static class DefaultWordBreaker extends AbstractWordBreaker {

        /**
         * A letter followed by either <code>-</code>, <code>.</code>,
         * <code>,</code> or <code>/</code>.
         */
        private final Pattern breakPattern =
                Pattern.compile("[A-Za-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF]([\\-\\.\\,/])");

        @Override
        protected Pair<String> breakWordSoft(final String word,
                                             final FontDescriptor fontDescriptor, final float maxWidth) {
            Matcher matcher = breakPattern.matcher(word);
            int breakIndex = -1;
            boolean maxWidthExceeded = false;
            while (!maxWidthExceeded && matcher.find()) {
                int currentIndex = matcher.end();
                if (currentIndex < word.length() - 1) {
                    if (TextSequenceUtil.getStringWidth(word.substring(0, currentIndex),
                            fontDescriptor) < maxWidth) {
                        breakIndex = currentIndex;
                    } else {
                        maxWidthExceeded = true;
                    }
                }
            }

            if (breakIndex < 0) {
                return null;
            }
            return new Pair<>(word.substring(0, breakIndex),
                    word.substring(breakIndex));
        }

    }

}
