package org.xbib.graphics.pdfbox.groovy

class UnitUtil {

    static final BigDecimal DPI = 72

    static final BigDecimal MM_INCH = 25.4

    static final BigDecimal CM_INCH = 2.54

    static final BigDecimal PICA_POINTS = 6

    static final BigDecimal TWIP_POINTS = 20

    static final BigDecimal EIGTH_POINTS = 8

    static final BigDecimal HALF_POINTS = 2

    static final BigDecimal EMU_POINTS = 12700

    static BigDecimal mmToPoint(BigDecimal mm) {
        mm * DPI / MM_INCH
    }

    static BigDecimal cmToPoint(BigDecimal cm) {
        cm * DPI / CM_INCH
    }

    static BigDecimal pointToMm(BigDecimal point) {
        point / DPI * MM_INCH
    }

    static BigDecimal pointToCm(BigDecimal point) {
        point / DPI * CM_INCH
    }

    static BigDecimal inchToPoint(BigDecimal inch) {
        inch * DPI
    }

    static BigDecimal pointToInch(BigDecimal point) {
        point / DPI
    }

    static BigDecimal pointToPica(BigDecimal point) {
        point * PICA_POINTS
    }

    static BigDecimal picaToPoint(BigDecimal pica) {
        pica / PICA_POINTS
    }

    static BigDecimal pointToEigthPoint(BigDecimal point) {
        point * EIGTH_POINTS
    }

    static BigDecimal eightPointToPoint(BigDecimal eigthPoint) {
        eigthPoint / EIGTH_POINTS
    }

    static BigDecimal pointToHalfPoint(BigDecimal point) {
        point * HALF_POINTS
    }

    static BigDecimal halfPointToPoint(BigDecimal halfPoint) {
        halfPoint / HALF_POINTS
    }

    static BigDecimal pointToTwip(BigDecimal point) {
        point * TWIP_POINTS
    }

    static BigDecimal twipToPoint(BigDecimal twip) {
        twip / TWIP_POINTS
    }

    static BigDecimal pointToEmu(BigDecimal point) {
        point * EMU_POINTS
    }

    static BigDecimal emuToPoint(BigDecimal emu) {
        emu / EMU_POINTS
    }

}
