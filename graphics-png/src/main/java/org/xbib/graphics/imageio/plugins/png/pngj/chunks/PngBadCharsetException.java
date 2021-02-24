package org.xbib.graphics.imageio.plugins.png.pngj.chunks;

import org.xbib.graphics.imageio.plugins.png.pngj.PngjException;

public class PngBadCharsetException extends PngjException {
    private static final long serialVersionUID = 1L;

    public PngBadCharsetException(String message, Throwable cause) {
        super(message, cause);
    }

    public PngBadCharsetException(String message) {
        super(message);
    }

    public PngBadCharsetException(Throwable cause) {
        super(cause);
    }

}
