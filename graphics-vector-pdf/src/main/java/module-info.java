module org.xbib.graphics.io.vector.pdf {
    exports org.xbib.graphics.io.vector.pdf;
    requires transitive org.xbib.graphics.io.vector;
    provides org.xbib.graphics.io.vector.VectorGraphics2DProvider with
            org.xbib.graphics.io.vector.pdf.PDFGraphics2DProvider;
}
