package org.xbib.graphics.pdfbox.groovy

class Barcode extends BaseNode {

    Integer x = 0

    Integer y = 0

    Integer width = 0

    Integer height = 0

    String value

    BarcodeType type

    void setType(String type) {
        this.type = Enum.valueOf(BarcodeType, type.toUpperCase())
    }
}
