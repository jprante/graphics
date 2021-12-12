package org.xbib.graphics.pdfbox.groovy.test

import groovy.util.logging.Log
import org.junit.Test
import org.xbib.graphics.barcode.SymbolType
import org.xbib.graphics.pdfbox.groovy.builder.PdfDocumentBuilder

import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

import static org.junit.Assert.assertTrue

@Log
class PdfDocumentBuilderTest {

    @Test
    void testPdfDocumentBuilderCJK() {
        List fontdefs = [
                [name: 'Noto Sans CJK TC Regular', resource: 'fonts/NotoSansCJKtc-Regular.ttf', bold: false, italic: false]
        ]
        OutputStream outputStream = Files.newOutputStream(Paths.get("build/testcjk.pdf"))
        outputStream.withCloseable {
            PdfDocumentBuilder builder = new PdfDocumentBuilder(outputStream, fontdefs)
            builder.create {
                document(font: [family: 'Noto Sans CJK TC Regular', color: '#000000', size: 12.pt]) {
                    paragraph "北京 東京大学"
                }
            }
        }
    }

    @Test
    void testPdfDocumentBuilderA6LandscapePaperSize() {
        OutputStream outputStream = Files.newOutputStream(Paths.get("build/testa6.pdf"))
        outputStream.withCloseable {
            PdfDocumentBuilder builder = new PdfDocumentBuilder(outputStream)
            builder.create {
                document(papersize: 'A6', orientation: 'landscape') {
                    paragraph "Hello World"
                }
            }
        }
    }

    @Test
    void testTableDocument() {
        OutputStream outputStream = Files.newOutputStream(Paths.get("build/testtable.pdf"))
        outputStream.withCloseable {
            PdfDocumentBuilder builder = new PdfDocumentBuilder(outputStream)
            List layout = [
                    [key: 'Typ', value: 'Online', 'bold': true],
                    [key: 'Nummer', value: 'test'],
                    [key: 'Bestelldatum', value: 'test'],
                    [key: 'Eingangsdatum', value: 'test'],
                    [key: 'Besteller', value: 'test', line: true],
                    [key: 'TAN', value: 'test'],
                    [key: 'Benutzer', value: 'test'],
                    [key: 'Kostenübernahme', value: 'test'],
                    [key: 'Lieferart', value: 'test'],
                    [key: 'Abholort', value: 'test'],
                    [key: 'Abholcode', value: 'test'],
                    [key: 'Buch/Zeitschrift', value: 'test'],
                    [key: 'ISBN/ISSN', value: 'test'],
                    [key: 'Quelle', value: 'test'],
                    [key: 'ID', value: 'test'],
                    [key: 'Erscheinungsort', value: 'test'],
                    [key: 'Verlag', value: 'test'],
                    [key: 'Aufsatztitel', value: 'test'],
                    [key: 'Aufsatzautor', value: 'test'],
                    [key: 'Jahrgang', value: 'test'],
                    [key: 'Seitenangabe', value: 'test'],
                    [key: 'Lieferant', value: 'test', line: 'true', bold: 'true'],
                    [key: 'Lieferantencode', value: 'test', bold: 'true'],
                    [key: 'Signatur/Standort', value: 'test', bold: 'true']
            ]
            builder.create {
                document(font: [family: 'Helvetica'], margin: [top: 1.cm]) {
                    paragraph(margin: [left: 6.cm, right: 1.cm, top: 0.cm]) {
                        font.size = 24.pt
                        font.bold = true
                        text 'Table Test Document'
                    }
                    paragraph {
                        table(margin: [left: 1.cm, top: 2.cm], width: 19.cm, padding: 0.pt, border: [size: 0.pt]) {
                            layout.each { l ->
                                if (l.line) {
                                    row {
                                        cell(width: 19.cm) {
                                            line(startX: 0.cm, endX: 19.cm, startY: 6.pt, strokewidth: 0.5f)
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
                                            text l.value, font: [bold: true], heightfactor: 2
                                        } else {
                                            text l.value, heightfactor : 2
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testPdfWithBarcode() {
        OutputStream outputStream = Files.newOutputStream(Paths.get('build/barcode.pdf'))
        outputStream.withCloseable {
            PdfDocumentBuilder builder = new PdfDocumentBuilder(outputStream)
            builder.create {
                document(font: [family: 'Helvetica'], margin: [top: 1.cm]) {
                    paragraph(margin: [left: 7.mm, top: 0.cm]) {
                        text "Hello World 1"
                    }
                    paragraph(margin: [left: 7.mm, top: 2.cm]) {
                        barcode(width: 6.cm, height: 2.cm, value: '20180123456', type: SymbolType.CODE39)
                    }
                    paragraph(margin: [left: 7.mm, top: 0.cm]) {
                        text "Hello World 2"
                    }
                    paragraph(margin: [left: 7.mm, top: 1.cm, bottom: 1.cm]) {
                        barcode(width: 10.cm, height: 0.5.cm, value: 'ABCDEFGHIJKLMN', type: SymbolType.CODE39)
                    }
                    paragraph(margin: [left: 7.mm, top: 0.cm]) {
                        text "Hello World 3"
                    }
                    paragraph(margin: [left: 7.mm, top: 4.cm]) {
                        barcode(width: 6.cm, height: 4.cm, value: 'Hello', type: SymbolType.QRCODE)
                    }
                }
            }
        }
    }

    @Test
    void testPdfWithImage() {
        byte[] logo = getClass().getResourceAsStream('/img/img.png').bytes
        OutputStream outputStream = Files.newOutputStream(Paths.get("build/testimage.pdf"))
        outputStream.withCloseable {
            PdfDocumentBuilder builder = new PdfDocumentBuilder(outputStream)
            List layout = [
                    [key: 'A', value: '1', 'bold': true],
                    [key: 'B', value: '2', 'bold': true],
                    [key: 'C', value: '3', 'bold': true]
            ]
            builder.create {
                document(font: [family: 'Helvetica'], margin: [top: 1.cm]) {
                    paragraph(margin: [left: 7.mm]) {
                        image(data: logo, name: 'img.png', width: 125.px, height: 45.px)
                    }
                    paragraph(margin: [left: 6.cm, right: 1.cm, top: 0.cm]) {
                        font.size = 24.pt
                        font.bold = true
                        text 'Table Test Document'
                    }
                    paragraph {
                        table(margin: [left: 1.cm, top: 2.cm], width: 19.cm, padding: 0.pt, border: [size: 0.pt]) {
                            layout.each { l ->
                                if (l.line) {
                                    row {
                                        cell(width: 19.cm) {
                                            line(startX: 0.cm, endX: 19.cm, startY: 6.pt, strokewidth: 0.5f)
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
                                            text l.value, font: [bold: true], heightfactor: 2
                                        } else {
                                            text l.value, heightfactor : 2
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testPdfWithCaron() {
        List fontdefs = [
                [name: 'Noto Sans Regular', resource: 'fonts/NotoSans-Regular.ttf', bold: false, italic: false],
                [name: 'Noto Sans Bold', resource: 'fonts/NotoSans-Bold.ttf', bold: true, italic: false],
                [name: 'Noto Sans CJK TC Regular', resource: 'fonts/NotoSansCJKtc-Regular.ttf', bold: false, italic: false],
                [name: 'Noto Sans CJK TC Bold', resource: 'fonts/NotoSansCJKtc-Bold.ttf', bold: true, italic: false]
        ]
        OutputStream outputStream = Files.newOutputStream(Paths.get('build/caron.pdf'))
        outputStream.withCloseable {
            PdfDocumentBuilder builder = new PdfDocumentBuilder(outputStream, fontdefs)
            builder.create {
                document {
                    paragraph {
                        font.family = 'Noto Sans Regular'
                        text "Latin Small Letter C with Caron \u010d"
                    }
                }
            }
        }
    }

    @Test
    void testCharacters() {
        Pattern detectHan = Pattern.compile('.*\\p{script=Han}.*')
        Pattern detectLatin = Pattern.compile('.*\\p{script=Latin}.*')
        String chinese = "北京 東京大学"
        String caron = "Hey, this is Latin Small Letter C with Caron \u010d"
        log.info("chinese = ${detectHan.matcher(chinese).matches()}")
        assertTrue(detectHan.matcher(chinese).matches())
        log.info("caron = ${detectLatin.matcher(caron).matches()}")
        assertTrue(detectLatin.matcher(caron).matches())
        String normalized = caron.replaceAll("\\P{IsLatin}","")
        log.info("normalized=${normalized}")
    }
}
