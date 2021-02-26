import org.xbib.graphics.barcode.Code3Of9;
import org.xbib.graphics.barcode.SymbolProvider;

module org.xbib.graphics.barcode {
    exports org.xbib.graphics.barcode;
    exports org.xbib.graphics.barcode.util;
    exports org.xbib.graphics.barcode.render;
    requires transitive java.desktop;
    provides SymbolProvider with Code3Of9.Provider;
}
