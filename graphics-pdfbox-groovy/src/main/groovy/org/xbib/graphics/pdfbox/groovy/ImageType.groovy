package org.xbib.graphics.pdfbox.groovy

enum ImageType {
    PNG('png'),
    JPG('jpg'),
    TIF('tif'),
    GIF('gif'),
    BMP('bmp')

    String value

    ImageType(String value) {
        this.value = value
    }
}
