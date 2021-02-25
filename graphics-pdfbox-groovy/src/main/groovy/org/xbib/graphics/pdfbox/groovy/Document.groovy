package org.xbib.graphics.pdfbox.groovy

class Document extends BlockNode {

    static final Margin defaultMargin = new Margin(top: UnitUtil.mmToPoint(5 as BigDecimal),
            bottom: UnitUtil.mmToPoint(5 as BigDecimal),
            left: UnitUtil.mmToPoint(5 as BigDecimal),
            right: UnitUtil.mmToPoint(5 as BigDecimal))

    static final BigDecimal defaultWidth = UnitUtil.mmToPoint(210 as BigDecimal)

    static final BigDecimal defaultHeight = UnitUtil.mmToPoint(297 as BigDecimal)

    String papersize = 'A4'

    String orientation = 'portrait'

    BigDecimal width = defaultWidth

    BigDecimal height = defaultHeight

    def template

    def header

    def footer

    private Map templateMap

    List children = []

    List<Map> fonts = []

    Map getTemplateMap() {
        if (templateMap == null) {
            loadTemplateMap()
        }
        templateMap
    }

    private void loadTemplateMap() {
        templateMap = [:]
        if (template && template instanceof Closure) {
            def templateDelegate = new Expando()
            templateDelegate.metaClass.methodMissing = { name, args ->
                templateMap[name] = args[0]
            }
            template.delegate = templateDelegate
            template()
        }
    }
}
