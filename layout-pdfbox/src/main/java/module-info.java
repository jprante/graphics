module org.xbib.graphics.layout.pdfbox {
    exports org.xbib.graphics.layout.pdfbox.elements;
    exports org.xbib.graphics.layout.pdfbox.elements.render;
    exports org.xbib.graphics.layout.pdfbox.shape;
    exports org.xbib.graphics.layout.pdfbox.text;
    exports org.xbib.graphics.layout.pdfbox.text.annotations;
    exports org.xbib.graphics.layout.pdfbox.util;
    requires transitive org.apache.pdfbox;
    requires transitive java.desktop;
}
