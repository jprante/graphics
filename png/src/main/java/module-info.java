module org.xbib.graphics.png {
    exports org.xbib.graphics.imageio.plugins.png;
    exports org.xbib.graphics.imageio.plugins.png.pngj;
    exports org.xbib.graphics.imageio.plugins.png.pngj.chunks;
    exports org.xbib.graphics.imageio.plugins.png.pngj.pixels;
    requires transitive java.desktop;
}
