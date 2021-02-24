module org.xbib.graphics.io.vector.eps {
    exports org.xbib.graphics.io.vector.eps;
    requires transitive org.xbib.graphics.io.vector;
    provides org.xbib.graphics.io.vector.VectorGraphics2DProvider with
            org.xbib.graphics.io.vector.eps.EPSGraphics2DProvider;
}
