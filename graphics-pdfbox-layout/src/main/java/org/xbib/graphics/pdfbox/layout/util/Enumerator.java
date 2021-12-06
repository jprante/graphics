package org.xbib.graphics.pdfbox.layout.util;

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
    String getDefaultSeparator();
}
