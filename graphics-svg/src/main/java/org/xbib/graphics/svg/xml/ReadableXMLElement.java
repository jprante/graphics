package org.xbib.graphics.svg.xml;

import org.w3c.dom.Element;

import java.net.URL;

public interface ReadableXMLElement {

    void read(Element root, URL docRoot);
}
