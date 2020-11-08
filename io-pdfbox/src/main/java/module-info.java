module org.xbib.graphics.graphics2d.pdfbox {
    exports org.xbib.graphics.io.pdfbox;
    requires transitive org.apache.pdfbox;
    requires org.apache.fontbox;
    requires transitive java.desktop;
    requires java.logging;
}