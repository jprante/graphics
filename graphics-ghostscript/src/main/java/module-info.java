module org.xbib.graphics.ghostscript {
    exports org.xbib.graphics.ghostscript;
    requires com.sun.jna;
    requires java.logging;
    requires transitive java.desktop;
    requires transitive org.apache.pdfbox;
}
