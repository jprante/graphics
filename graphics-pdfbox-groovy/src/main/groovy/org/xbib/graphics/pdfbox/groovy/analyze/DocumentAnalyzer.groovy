package org.xbib.graphics.pdfbox.groovy.analyze

import groovy.util.logging.Log4j2
import org.apache.logging.log4j.Level
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.cos.COSStream
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.graphics.image.PDImage
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject

import java.awt.geom.Point2D

@Log4j2
class DocumentAnalyzer {

    private final Map result = [:]

    private final Set<COSStream> seen = new HashSet<>()

    DocumentAnalyzer(InputStream inputStream) {
        inputStream.withCloseable {
            PDDocument document = PDDocument.load(inputStream)
            result."author" = document.getDocumentInformation().author
            result."creator" = document.getDocumentInformation().creator
            result."producer" = document.getDocumentInformation().producer
            result."title" = document.getDocumentInformation().title
            result."pagecount" = document.getNumberOfPages()
            try {
                result."creationDate" = document.getDocumentInformation().creationDate?.toInstant()
                result."modificationDate" = document.getDocumentInformation().modificationDate?.toInstant()
            } catch (Exception e) {
                // NPE if creation/modification dates are borked
                /**
                 * java.lang.NullPointerException: null
                 *         at java.text.SimpleDateFormat.matchZoneString(SimpleDateFormat.java:1695) ~[?:?]
                 *         at java.text.SimpleDateFormat.subParseZoneString(SimpleDateFormat.java:1763) ~[?:?]
                 *         at java.text.SimpleDateFormat.subParse(SimpleDateFormat.java:2169) ~[?:?]
                 *         at java.text.SimpleDateFormat.parse(SimpleDateFormat.java:1541) ~[?:?]
                 *         at org.apache.pdfbox.util.DateConverter.parseSimpleDate(DateConverter.java:587) ~[pdfbox-2.0.12.jar:2.0.12]
                 *         at org.apache.pdfbox.util.DateConverter.parseDate(DateConverter.java:658) ~[pdfbox-2.0.12.jar:2.0.12]
                 *         at org.apache.pdfbox.util.DateConverter.toCalendar(DateConverter.java:723) ~[pdfbox-2.0.12.jar:2.0.12]
                 *         at org.apache.pdfbox.util.DateConverter.toCalendar(DateConverter.java:701) ~[pdfbox-2.0.12.jar:2.0.12]
                 *         at org.apache.pdfbox.cos.COSDictionary.getDate(COSDictionary.java:790) ~[pdfbox-2.0.12.jar:2.0.12]
                 *         at org.apache.pdfbox.pdmodel.PDDocumentInformation.getCreationDate(PDDocumentInformation.java:212) ~[pdfbox-2.0.12.jar:2.0.12]
                 */
                log.log(Level.WARN, e.getMessage() as String, e)
            }
            result."pages" = []
            document.withCloseable {
                int images = 0
                int pagecount = result."pagecount" as int
                for (int i = 0; i < pagecount; i++) {
                    PDPage pdPage = document.getPage(i)
                    Map pageMap = analyze(i, pdPage)
                    images += pageMap."images".size()
                    result."pages" << pageMap
                }
                result."imagecount" = images
            }
        }
    }

    Map<Object, Object> getResult() {
        result
    }

    Map analyze(int i, PDPage page) {
        def m = [:]
        m."page" = i
        m."bbox" = [height: page.getBBox().getHeight(), width: page.getBBox().getWidth()]
        m."cropbox" = [height: page.getCropBox().getHeight(), width: page.getCropBox().getWidth()]
        m."mediabox" = [height: page.getMediaBox().getHeight(), width: page.getMediaBox().getWidth()]
        m."bleedbox" = [height: page.getBleedBox().getHeight(), width: page.getBleedBox().getWidth()]
        m."rotation" = page.getRotation()
        m."images" = []
        ImageGraphicsExtractor extractor = new ImageGraphicsExtractor(m."images" as List, page)
        extractor.process()
        m."fonts" = []
        PDResources res = page.getResources()
        for (COSName cosName : res.getFontNames()) {
            PDFont font = res.getFont(cosName)
            if (font) {
                def f = [:]
                f."name" = font.name
                f."damaged" = font.damaged
                f."embedded" = font.embedded
                f."type" = font.type
                f."subtype" = font.subType
                m."fonts" << f
            }
        }
        m
    }

    class ImageGraphicsExtractor extends PDFGraphicsStreamEngine {

        private final List list

        protected ImageGraphicsExtractor(List list, PDPage page) {
            super(page)
            this.list = list
        }

        void process() throws IOException {
            processPage(getPage())
        }

        @Override
        void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {

        }

        @Override
        void drawImage(PDImage pdImage) throws IOException {
            if (pdImage instanceof PDImageXObject) {
                PDImageXObject xobject = pdImage as PDImageXObject
                if (seen.contains(xobject.getCOSObject())) {
                    // skip duplicate image
                    return
                }
                seen.add(xobject.getCOSObject())
                def m = [:]
                m."width" = xobject.width
                m."height" = xobject.height
                m."bitspercomponent" = xobject.bitsPerComponent
                m."colorspace" = xobject.colorSpace.name
                m."suffix" = xobject.suffix
                list << m
            }
        }

        @Override
        void clip(int windingRule) throws IOException {

        }

        @Override
        void moveTo(float x, float y) throws IOException {

        }

        @Override
        void lineTo(float x, float y) throws IOException {

        }

        @Override
        void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {

        }

        @Override
        Point2D getCurrentPoint() throws IOException {
            null
        }

        @Override
        void closePath() throws IOException {

        }

        @Override
        void endPath() throws IOException {

        }

        @Override
        void strokePath() throws IOException {

        }

        @Override
        void fillPath(int windingRule) throws IOException {

        }

        @Override
        void fillAndStrokePath(int windingRule) throws IOException {

        }

        @Override
        void shadingFill(COSName shadingName) throws IOException {

        }
    }
}
