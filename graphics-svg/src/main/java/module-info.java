module org.xbib.graphics.svg {
    requires transitive java.desktop;
    requires java.logging;
    exports org.xbib.graphics.svg;
    exports org.xbib.graphics.svg.element;
    exports org.xbib.graphics.svg.element.filtereffects;
    exports org.xbib.graphics.svg.element.glyph;
    exports org.xbib.graphics.svg.element.gradient;
    exports org.xbib.graphics.svg.element.shape;
    exports org.xbib.graphics.svg.pathcmd;
    exports org.xbib.graphics.svg.xml;
    exports org.xbib.graphics.svg.util;
}