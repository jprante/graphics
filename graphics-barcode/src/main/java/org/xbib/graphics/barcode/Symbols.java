package org.xbib.graphics.barcode;

public enum Symbols {
    AustraliaPost,
    AztecCode,
    ChannelCode,
    Codabar,
    CodablockF,
    Code2Of5,
    Code3Of9,
    Code3Of9Extended,
    Code11,
    Code16k,
    Code32,
    Code49,
    Code93,
    Code128,
    CodeOne,
    Composite,
    DataBar14,
    DataBarExpanded,
    DataBarLimited,
    DataMatrix,
    Ean,
    GridMatrix,
    JapanPost,
    KixCode,
    KoreaPost,
    Logmars,
    MaxiCode,
    MicroQrCode,
    MsiPlessey,
    Nve18,
    Pdf417,
    Pharmacode,
    Pharmacode2Track,
    Pharmazentralnummer,
    Postnet,
    QrCode,
    RoyalMail4State,
    Telepen,
    Upc,
    UspsOneCode,
    UspsPackage;

    @SuppressWarnings("unchecked")
    public Symbol getSymbol() throws Exception {
        Class<? extends AbstractSymbol> cl = (Class<? extends AbstractSymbol>) getClass().getClassLoader().loadClass(getClass().getPackageName() + "." + name());
        return cl.getDeclaredConstructor().newInstance();
    }
}
