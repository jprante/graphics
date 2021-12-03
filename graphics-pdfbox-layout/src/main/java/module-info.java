module org.xbib.graphics.layout.pdfbox {
    exports org.xbib.graphics.pdfbox.layout.boxable;
    exports org.xbib.graphics.pdfbox.layout.elements;
    exports org.xbib.graphics.pdfbox.layout.elements.render;
    exports org.xbib.graphics.pdfbox.layout.font;
    exports org.xbib.graphics.pdfbox.layout.shape;
    exports org.xbib.graphics.pdfbox.layout.table;
    exports org.xbib.graphics.pdfbox.layout.table.render;
    exports org.xbib.graphics.pdfbox.layout.text;
    exports org.xbib.graphics.pdfbox.layout.text.annotations;
    exports org.xbib.graphics.pdfbox.layout.util;
    requires transitive org.xbib.graphics.barcode;
    requires transitive org.xbib.graphics.pdfbox;
    requires org.xbib.settings.datastructures;
    requires transitive java.desktop;
    requires java.logging;
}
