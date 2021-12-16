package org.xbib.graphics.svg;

@SuppressWarnings("serial")
public class SVGException extends Exception {

    public SVGException() {
    }

    public SVGException(String msg) {
        super(msg);
    }

    public SVGException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SVGException(Throwable cause) {
        super(cause);
    }
}
