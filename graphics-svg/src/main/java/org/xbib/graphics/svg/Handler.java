package org.xbib.graphics.svg;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Handler extends URLStreamHandler {

    static class Connection extends URLConnection {

        String mime;

        byte[] buf;

        public Connection(URL url) {
            super(url);
            String path = url.getPath();
            int idx = path.indexOf(';');
            mime = path.substring(0, idx);
            String content = path.substring(idx + 1);
            if (content.startsWith("base64,")) {
                content = content.substring(7);
                buf = Base64.getDecoder().decode(content.getBytes(StandardCharsets.US_ASCII));
            }
        }

        @Override
        public void connect() {
        }

        @Override
        public String getHeaderField(String name) {
            if ("content-type".equals(name)) {
                return mime;
            }

            return super.getHeaderField(name);
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(buf);
        }
    }

    @Override
    protected URLConnection openConnection(URL u) {
        return new Connection(u);
    }
}
