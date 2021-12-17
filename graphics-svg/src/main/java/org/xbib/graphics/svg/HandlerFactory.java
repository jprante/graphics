package org.xbib.graphics.svg;

import org.xbib.graphics.svg.Handler;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class HandlerFactory implements URLStreamHandlerFactory {

    private static final Handler handler = new Handler();

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("data".equals(protocol)) {
            return handler;
        }
        return null;
    }
}
