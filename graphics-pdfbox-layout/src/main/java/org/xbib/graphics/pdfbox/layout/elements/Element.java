package org.xbib.graphics.pdfbox.layout.elements;

/**
 * Base (tagging) interface for elements in a {@link Document}.
 */
public interface Element {

    default Element add(Element element) {
        throw new UnsupportedOperationException();
    }
}
