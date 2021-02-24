module org.xbib.graphics.io.vector.svg {
    exports org.xbib.graphics.io.vector.svg;
    requires transitive org.xbib.graphics.io.vector;
    provides org.xbib.graphics.io.vector.VectorGraphics2DProvider with
            org.xbib.graphics.io.vector.svg.SVGGraphics2DProvider;
}
