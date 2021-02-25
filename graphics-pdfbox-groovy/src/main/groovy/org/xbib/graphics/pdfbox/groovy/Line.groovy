package org.xbib.graphics.pdfbox.groovy

import groovy.transform.AutoClone

@AutoClone
class Line extends BaseNode {

    Integer startX = 0

    Integer startY = 0

    Integer endX = 0

    Integer endY = 0

    float strokewidth = 0.0f
}
