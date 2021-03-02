package org.xbib.graphics.pdfbox.layout.util;

import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import java.io.IOException;

/**
 * This interface may be used to implement different strategies on how to break
 * a word, if it does not fit into a line.
 */
public interface WordBreaker {

    /**
     * Breaks the word in order to fit the given maximum width.
     *
     * @param word                 the word to break.
     * @param fontDescriptor       describing the font's type and size.
     * @param maxWidth             the maximum width to obey.
     * @param breakHardIfNecessary indicates if the word should be broken hard to fit the width,
     *                             in case there is no suitable position for breaking it
     *                             adequately.
     * @return the broken word, or <code>null</code> if it cannot be broken.
     * @throws IOException by pdfbox
     */
    Pair<String> breakWord(String word,
                           FontDescriptor fontDescriptor, float maxWidth,
                           boolean breakHardIfNecessary);

}
