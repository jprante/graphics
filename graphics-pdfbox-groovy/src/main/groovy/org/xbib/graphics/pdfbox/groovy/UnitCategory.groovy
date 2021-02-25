package org.xbib.graphics.pdfbox.groovy

@Category(Number)
class UnitCategory {

    BigDecimal getCm() { this * UnitUtil.DPI / UnitUtil.CM_INCH }

    BigDecimal getMm() { this * UnitUtil.DPI / UnitUtil.MM_INCH }

    BigDecimal getInches() { this * UnitUtil.DPI }

    BigDecimal getInch() { this * UnitUtil.DPI }

    BigDecimal getPt() { this }

    BigDecimal getPx() { this }
}
