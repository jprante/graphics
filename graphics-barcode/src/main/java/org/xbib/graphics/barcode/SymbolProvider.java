package org.xbib.graphics.barcode;

public interface SymbolProvider<S extends Symbol> {

    boolean canProvide(SymbolType type);

    S provide();
}
