module org.xbib.graphics.graphics2d.pdfbox {
    exports org.xbib.graphics.io.pdfbox;
    exports org.xbib.graphics.io.pdfbox.color;
    exports org.xbib.graphics.io.pdfbox.draw;
    exports org.xbib.graphics.io.pdfbox.font;
    exports org.xbib.graphics.io.pdfbox.image;
    exports org.xbib.graphics.io.pdfbox.paint;
    requires transitive org.apache.pdfbox;
    requires org.apache.fontbox;
    requires transitive java.desktop;
    requires java.logging;
}
