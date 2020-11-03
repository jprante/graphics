package org.xbib.graphics.layout.pdfbox.util;

/**
 * Defines an enumerator.
 */
public interface Enumerator {

    /**
     * @return the next enumeration.
     */
    String next();

    /**
     * @return the default separator.
     */
    String getDefaultSeperator();
}
