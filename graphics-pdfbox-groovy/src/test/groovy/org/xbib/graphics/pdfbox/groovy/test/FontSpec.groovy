package org.xbib.graphics.pdfbox.groovy.test

import groovy.util.logging.Log4j2
import org.xbib.graphics.pdfbox.groovy.Font
import org.xbib.graphics.pdfbox.groovy.builder.PdfFont
import spock.lang.Specification

@Log4j2
class FontSpec extends Specification {

    def "override properties with left shift"() {
        Font font = new Font(family:'Initial', size:10)

        when:
        font << [family:'New']

        then:
        font.family == 'New'
        font.size == 10

        when:
        font << [size:12]

        then:
        font.family == 'New'
        font.size == 12

    }

    def "printable characters"() {
        String s = "\u0098 Hello Jörg"

        when:
        s = s.replaceAll("\\p{C}", "")

        then:
        s == " Hello Jörg"
    }

    def "glyph exists"() {
        String string = "Jörg \\u010d"
        Font font = new Font(family: 'Helvetica')
        Boolean b = false

        when:
        b = PdfFont.canEncode(font, string)

        then:
        log.info("b=${b}")
        b
    }
}