package org.xbib.graphics.pdfbox.layout.elements;

import org.xbib.graphics.pdfbox.layout.elements.render.LayoutHint;

/**
 * Base (tagging) interface for elements in a {@link Document}.
 */
public interface Element {

    default Element add(Element element) {
        throw new UnsupportedOperationException();
    }

    default Element add(Element element, LayoutHint layoutHint) {
        throw new UnsupportedOperationException();
    }
}
