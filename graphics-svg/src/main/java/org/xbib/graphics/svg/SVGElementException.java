package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.SVGElement;

@SuppressWarnings("serial")
public class SVGElementException extends SVGException {

    private final SVGElement element;

    public SVGElementException(SVGElement element, String msg) {
        this(element, msg, null);
    }

    public SVGElementException(SVGElement element, String msg, Throwable cause) {
        super(msg, cause);
        this.element = element;
    }

    public SVGElement getElement() {
        return element;
    }
}
