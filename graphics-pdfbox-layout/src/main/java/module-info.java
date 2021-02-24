module org.xbib.graphics.layout.pdfbox {
    exports org.xbib.graphics.pdfbox.layout.elements;
    exports org.xbib.graphics.pdfbox.layout.elements.render;
    exports org.xbib.graphics.pdfbox.layout.shape;
    exports org.xbib.graphics.pdfbox.layout.text;
    exports org.xbib.graphics.pdfbox.layout.text.annotations;
    exports org.xbib.graphics.pdfbox.layout.util;
    requires transitive org.apache.pdfbox;
    requires transitive java.desktop;
}
