package org.xbib.graphics.svg;

@SuppressWarnings("serial")
public class SVGParseException extends Exception {

    public SVGParseException() {
    }

    public SVGParseException(String msg) {
        super(msg);
    }

    public SVGParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SVGParseException(Throwable cause) {
        super(cause);
    }
}
