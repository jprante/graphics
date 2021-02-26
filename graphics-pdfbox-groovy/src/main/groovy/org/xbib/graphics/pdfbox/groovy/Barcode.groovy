package org.xbib.graphics.pdfbox.groovy

import org.xbib.graphics.barcode.SymbolType

class Barcode extends BaseNode {

    Integer x = 0

    Integer y = 0

    Integer width

    Integer height

    String value

    SymbolType symbolType

    void setType(String type) {
        this.symbolType = Enum.valueOf(SymbolType, type.toUpperCase())
    }
}
