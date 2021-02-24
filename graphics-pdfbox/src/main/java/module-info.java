module org.xbib.graphics.pdfbox {
    exports org.xbib.graphics.pdfbox;
    exports org.xbib.graphics.pdfbox.color;
    exports org.xbib.graphics.pdfbox.draw;
    exports org.xbib.graphics.pdfbox.font;
    exports org.xbib.graphics.pdfbox.image;
    exports org.xbib.graphics.pdfbox.paint;
    requires transitive org.apache.pdfbox;
    requires org.apache.fontbox;
    requires transitive java.desktop;
    requires java.logging;
}
