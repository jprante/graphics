package org.xbib.graphics.pdfbox.groovy.test

import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.builder.DocumentBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

abstract class BaseBuilderSpec extends Specification {

    @Shared
    ByteArrayOutputStream out

    @Shared
    DocumentBuilder builder

    @Shared
    byte[] imageData = getClass().classLoader.getResource('cat.jpg').bytes

    @Shared
    testMargins = [
            [top: 0, bottom: 0, left: 0, right: 0],
            [top: 2 * 72, bottom: 3 * 72, left: 1.25 * 72, right: 2.5 * 72],
            [top: 72 / 4, bottom: 72 / 2, left: 72 / 4, right: 72 / 2]
    ]

    byte[] getData() { out.toByteArray() }

    abstract DocumentBuilder getBuilderInstance(OutputStream out)

    abstract Document getDocument(byte[] data)

    def setup() {
        out = new ByteArrayOutputStream()
        builder = getBuilderInstance(out)
    }

    @Unroll
    def "set document margins"() {
        when:
        builder.create {
            document(margin: [top: margin.top, bottom: margin.bottom, left: margin.left, right: margin.right]) {
                paragraph 'Content'
            }
        }

        def document = getDocument(data)

        then:
        document.margin.left == margin.left

        and:
        document.margin.right == margin.right

        and:
        document.margin.top == margin.top

        and:
        document.margin.bottom == margin.bottom

        where:
        margin << testMargins
    }

    @Unroll
    def "set paragraph margins"() {
        when:
        builder.create {
            document {
                paragraph(margin: currentMargin) {
                    text 'Foo'
                }
            }
        }

        def paragraph = getDocument(data).children[0]

        then:
        paragraph.margin.left == currentMargin.left

        and:
        paragraph.margin.right >= currentMargin.right

        //and:
        //paragraph.margin.top == currentMargin.top

        where:
        currentMargin << testMargins
    }

    def "create a simple table"() {
        when:
        builder.create {
            document {
                table {
                    row {
                        cell {
                            text 'FOOBAR'
                        }
                    }
                }
            }
        }

        def table = getDocument(data).children[0]

        then:
        table.children[0].children[0].children[0].text == 'FOOBAR'
    }

    def "set table options"() {
        when:
        builder.create {
            document {
                table(width: 403.px, columns: [100.px, 300.px], border: [size: 1.px]) {
                    row {
                        cell('Cell 1')
                        cell('Cell 2')
                    }
                }
            }
        }

        def table = getDocument(data).children[0]

        then:
        table.width == 403

        and:
        table.children[0].children[0].width == 100

        and:
        table.children[0].children[1].width == 300
    }

    def "set paragraph text"() {
        when:
        builder.create {
            document {
                paragraph 'Foo'
                paragraph('Foo') {
                    text 'Ba'
                    text 'r'
                }
                paragraph {
                    text 'B'
                    text 'a'
                    text 'r'
                }
            }
        }

        def paragraphs = getDocument(data).children

        then:
        paragraphs[0].text == 'Foo'

        and:
        paragraphs[1].text == 'FooBar'

        and:
        paragraphs[2].text == 'Bar'
    }

    def "create a table with multiple columns"() {
        when:
        builder.create {
            document {
                table {
                    row {
                        cell 'Cell1'
                        cell 'Cell2'
                        cell {
                            text 'Cell3'
                        }
                    }

                }
            }
        }

        then:
        notThrown(Exception)
    }

    def "create a table with lots of rows"() {
        when:
        builder.create {
            document {
                table {
                    50.times { i ->
                        row {
                            cell {
                                text 'TEST ' * (i + 1)
                            }
                            cell {
                                text 'FOO ' * (i + 1)
                            }
                            cell {
                                text 'BAR ' * (i + 1)
                            }
                        }
                    }
                }
            }
        }

        then:
        notThrown(Exception)
    }

    def "add an image"() {
        when:
        builder.create {
            document {
                paragraph {
                    image(data: imageData, width: 500.px, height: 431.px)
                }
            }
        }

        then:
        notThrown(Exception)
    }

    def "add a barcode"() {
        when:
        builder.create {
            document {
                paragraph {
                    barcode(height: 150.px, width: 500.px, value: '12345678', type: 'CODE39')
                }
            }
        }

        then:
        notThrown(Exception)
    }


    def "paragraph header"() {
        when:
        builder.create {
            document(header: { paragraph 'HEADER' }) {
                paragraph 'Content'
            }
        }

        then:
        notThrown(Exception)
    }

    def "paragraph footer"() {
        when:
        builder.create {
            document(footer: { paragraph 'FOOTER' }) {
                paragraph 'Content'
            }
        }

        then:
        notThrown(Exception)
    }

    def "paragraph header and footer"() {
        when:
        builder.create {
            document(header: { paragraph 'HEADER' }, footer: { paragraph 'FOOTER' }) {
                paragraph 'Content'
            }
        }

        then:
        notThrown(Exception)
    }

    def "table header"() {
        when:
        builder.create {
            document(header: { table { row { cell 'HEADER' } } }) {
                paragraph 'Content'
            }
        }

        then:
        notThrown(Exception)
    }

    def "table footer"() {
        when:
        builder.create {
            document(footer: { table { row { cell 'FOOTER' } } }) {
                paragraph 'Content'
            }
        }

        then:
        notThrown(Exception)
    }

    /*def "table within table"() {
        when:
        builder.create {
            document {
                table {
                    row {
                        cell 'OUTER TABLE'
                        cell {
                            table {
                                row {
                                    cell 'INNER TABLE'
                                }
                            }
                        }
                    }
                }
            }
        }

        then:
        notThrown(Exception)
    }*/

    def "table with rowspan"() {
        when:
        builder.create {
            document {
                table {
                    row {
                        cell 'FOO\nBAR', rowspan: 3
                        cell('COL1-2')
                    }
                    row {
                        cell('COL2-1')
                    }
                    row {
                        cell('COL3-1')
                    }
                    row {
                        cell('COL4-1')
                        cell('COL4-2')
                    }
                }
            }
        }

        then:
        notThrown(Exception)
    }

    def "table with line in cell paragraph"() {
        when:
        builder.create {
            document {
                table {
                    row {
                        cell {
                           line(startX: 1.cm, endX: 2.cm, strokewidth: 1.pt )
                        }
                    }
                }
            }
        }

        then:
        notThrown(Exception)
    }

    def "custom table with line"() {
        when:
        List layout = [
                [ key: 'Typ', value: 'Online', 'bold':true],
                [ key: 'Nummer', value: 'test'],
                [ key: 'Bestelldatum', value: 'test'],
                [ key: 'Eingangsdatum', value: 'test'],
                [ key: 'Besteller', value: 'test', line: true],
                [ key: 'TAN', value: 'test'],
                [ key: 'Benutzer', value: 'test'],
                [ key: 'Kostenübernahme', value: 'test'],
                [ key: 'Lieferart', value: 'test'],
                [ key: 'Abholort', value: 'test'],
                [ key: 'Abholcode', value: 'test'],
                [ key: 'Buch/Zeitschrift', value: 'test'],
                [ key: 'ISBN/ISSN', value: 'test'],
                [ key: 'Quelle', value: 'test' ],
                [ key: 'ID', value: 'test' ],
                [ key: 'Erscheinungsort', value: 'test'],
                [ key: 'Verlag', value: 'test'],
                [ key: 'Aufsatztitel', value: 'test'],
                [ key: 'Aufsatzautor', value: 'test'],
                [ key: 'Jahrgang', value: 'test'],
                [ key: 'Seitenangabe', value: 'test'],
                [ key: 'Lieferant', value: 'test'],
                [ key: 'Lieferantencode', value :'test' ],
                [ key: 'Signatur/Standort', value : 'test' ]
        ]
        builder.create {
            document(font: [family: 'Helvetica'], margin: [top: 1.cm]) {
                paragraph(margin:[left: 6.cm, right: 1.cm, top: -1.5.cm]) {
                    font.size = 24.pt
                    font.bold = true
                    text 'Lieferschein'
                }
                paragraph {
                    table(margin:[left: 1.cm, top: 2.cm], width: 19.cm, padding: 0.pt, border:[size:0.pt]) {
                        layout.each { l ->
                            if (l.line) {
                                row {
                                    cell(width: 19.cm) {
                                        line(startX: 0.cm, endX: 19.cm, strokewidth: 1.pt)
                                    }
                                }
                            }
                            row {
                                cell(width: 4.cm, align: 'left') {
                                    if (l.bold) {
                                        text l.key, font: [bold: true]
                                        text ':', font: [bold: true]
                                    } else {
                                        text l.key
                                        text ':'
                                    }
                                }
                                cell(width: 15.cm, align: 'left') {
                                    if (l.bold) {
                                        text l.value, font: [bold: true]
                                    } else {
                                        text l.value
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        then:
        notThrown(Exception)

    }

}
