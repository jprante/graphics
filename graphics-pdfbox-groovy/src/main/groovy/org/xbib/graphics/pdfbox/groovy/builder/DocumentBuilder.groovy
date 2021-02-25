package org.xbib.graphics.pdfbox.groovy.builder

import org.xbib.graphics.pdfbox.groovy.BackgroundAssignable
import org.xbib.graphics.pdfbox.groovy.BaseNode
import org.xbib.graphics.pdfbox.groovy.BlockNode
import org.xbib.graphics.pdfbox.groovy.Bookmarkable
import org.xbib.graphics.pdfbox.groovy.Cell
import org.xbib.graphics.pdfbox.groovy.Document
import org.xbib.graphics.pdfbox.groovy.Font
import org.xbib.graphics.pdfbox.groovy.Heading
import org.xbib.graphics.pdfbox.groovy.Linkable
import org.xbib.graphics.pdfbox.groovy.Stylable
import org.xbib.graphics.pdfbox.groovy.TextBlock
import org.xbib.graphics.pdfbox.groovy.UnitCategory
import org.xbib.graphics.pdfbox.groovy.factory.*

/**
 *
 */
abstract class DocumentBuilder extends FactoryBuilderSupport {

    final OutputStream out

    final List<Map> fontDefs

    RenderState renderState = RenderState.PAGE

    Document document

    protected List<String> imageFileNames = []

    DocumentBuilder(OutputStream out) {
        this(out, null)
    }

    DocumentBuilder(OutputStream out, List fontDefs) {
        super(true)
        this.out = out
        this.fontDefs = fontDefs
    }

    OutputStream getOutputStream() {
        out
    }

    Font getFont() {
        current.font
    }

    def invokeMethod(String name, args) {
        use(UnitCategory) {
            super.invokeMethod(name, args)
        }
    }

    void setNodeProperties(BaseNode node, Map attributes, String nodeKey) {
        String[] templateKeys = getTemplateKeys(node, nodeKey)
        def nodeProperties = []
        templateKeys.each { String key ->
            if (document.template && document.templateMap.containsKey(key)) {
                nodeProperties << document.templateMap[key]
            }
        }
        nodeProperties << attributes
        if (node instanceof Stylable) {
            setNodeFont(node, nodeProperties)
        }
        if (node instanceof BlockNode) {
            setBlockProperties(node, nodeProperties)
        }
        if (node instanceof BackgroundAssignable) {
            setNodeBackground(node, nodeProperties)
        }
        if (node instanceof Linkable) {
            String parentUrl = (node.parent instanceof Linkable) ? node.parent.url : null
            node.url = node.url ?: parentUrl
        }
        if (node instanceof Bookmarkable) {
            node.ref = attributes.ref
        }
    }

    protected void setNodeFont(Stylable node, nodeProperties) {
        node.font = (node instanceof Document) ? new Font() : node.parent.font.clone()
        nodeProperties.each {
            node.font << it.font
        }
    }

    protected void setBlockProperties(BlockNode node, nodeProperties) {
        node.margin = node.getClass().defaultMargin.clone()
        nodeProperties.each {
            node.margin << it.margin
            if (it.border) {
                node.border << it.border
            }
        }
    }

    protected void setNodeBackground(BackgroundAssignable node, nodeProperties) {
        nodeProperties.each { Map properties ->
            if (properties.containsKey('background')) {
                node.background = properties.background
            }
        }
        if (!node.background && (node.parent instanceof BackgroundAssignable) && node.parent.background) {
            node.background = "#${node.parent.background.hex}"
        }
    }

    static String[] getTemplateKeys(BaseNode node, String nodeKey) {
        def keys = [nodeKey]
        if (node instanceof Heading) {
            keys << "heading${node.level}"
        }
        if (node instanceof Stylable && node.style) {
            keys << "${nodeKey}.${node.style}"
            if (node instanceof Heading) {
                keys << "heading${node.level}.${node.style}"
            }
        }
        keys
    }

    TextBlock getColumnParagraph(Cell column) {
        if (column.children && column.children[0] instanceof TextBlock) {
            column.children[0]
        } else {
            TextBlock paragraph = new TextBlock(font: column.font.clone(), parent: column, align: column.align)
            setNodeProperties(paragraph, [margin: [top: 0, left: 0, bottom: 0, right: 0]], 'paragraph')
            column.children << paragraph
            paragraph
        }
    }

    abstract void initializeDocument(Document document)

    abstract void writeDocument(Document document)

    def onTextBlockComplete
    def onTableComplete
    def onLineComplete
    def onRowComplete
    def onCellComplete

    def registerObjectFactories() {
        registerFactory('create', new CreateFactory())
        registerFactory('document', new DocumentFactory())
        registerFactory('pageBreak', new PageBreakFactory())
        registerFactory('paragraph', new ParagraphFactory())
        registerFactory('lineBreak', new LineBreakFactory())
        registerFactory('line', new LineFactory())
        registerFactory('image', new ImageFactory())
        registerFactory('barcode', new BarcodeFactory())
        registerFactory('text', new TextFactory())
        registerFactory('table', new TableFactory())
        registerFactory('row', new RowFactory())
        registerFactory('cell', new CellFactory())
        registerFactory('heading1', new HeadingFactory())
        registerFactory('heading2', new HeadingFactory())
        registerFactory('heading3', new HeadingFactory())
        registerFactory('heading4', new HeadingFactory())
        registerFactory('heading5', new HeadingFactory())
        registerFactory('heading6', new HeadingFactory())
    }
}
