package org.xbib.graphics.pdfbox.groovy

class Image extends BaseNode {

    String name

    ImageType type

    Integer x = 0

    Integer y = 0

    Integer width

    Integer height

    byte[] data
    
    void setType(String value) { 
        type = Enum.valueOf(ImageType, value.toUpperCase()) 
    }
}
