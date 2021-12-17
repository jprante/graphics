package org.xbib.graphics.pdfbox.layout.element;

/*
import io.sf.carte.echosvg.anim.dom.SAXSVGDocumentFactory;
import io.sf.carte.echosvg.bridge.BridgeContext;
import io.sf.carte.echosvg.bridge.DocumentLoader;
import io.sf.carte.echosvg.bridge.GVTBuilder;
import io.sf.carte.echosvg.bridge.UserAgent;
import io.sf.carte.echosvg.bridge.UserAgentAdapter;
import io.sf.carte.echosvg.gvt.GraphicsNode;
*/
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.xbib.graphics.pdfbox.PdfBoxGraphics2D;
import org.xbib.graphics.pdfbox.color.DefaultColorMapper;
import org.xbib.graphics.pdfbox.color.RGBtoCMYKColorMapper;
import org.xbib.graphics.pdfbox.font.DefaultFontDrawer;
import org.xbib.graphics.pdfbox.font.FontDrawer;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;

import java.awt.color.ICC_Profile;
import java.io.IOException;

public class SVGElement extends ImageElement {

    private String svg;

    @Override
    public void setImage(String svg) {
        this.svg = svg;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
                     Position upperLeft, DrawListener drawListener) throws IOException {
        if (svg == null) {
            return;
        }
        float x = upperLeft.getX();
        float y = upperLeft.getY() - getHeight();
        PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(pdDocument, getWidth(), getHeight());
        ICC_Profile icc_profile = ICC_Profile.getInstance(PDDocument.class.getResourceAsStream(
                "/org/apache/pdfbox/resources/icc/ISOcoated_v2_300_bas.icc"));
        DefaultColorMapper colorMapper = new RGBtoCMYKColorMapper(icc_profile, pdDocument);
        pdfBoxGraphics2D.setColorMapper(colorMapper);
        FontDrawer fontDrawer = new DefaultFontDrawer();
        pdfBoxGraphics2D.setFontTextDrawer(fontDrawer);
        pdfBoxGraphics2D.scale(getScaleX(), getScaleY());
        //load(svg).paint(pdfBoxGraphics2D);
        pdfBoxGraphics2D.dispose();
        PDFormXObject xFormObject = pdfBoxGraphics2D.getXFormObject();
        Matrix matrix = new Matrix();
        matrix.translate(x, y);
        contentStream.saveGraphicsState();
        contentStream.transform(matrix);
        contentStream.drawForm(xFormObject);
        contentStream.restoreGraphicsState();
        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, getWidth(), getHeight());
        }
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() {
        return this;
    }

    /*private GraphicsNode load(String svg) throws IOException {
        try {
            XMLReader xmlReader = SAXParserFactory.newDefaultInstance().newSAXParser().getXMLReader();
            SAXSVGDocumentFactory documentFactory = new SAXSVGDocumentFactory(xmlReader);
            Document document = documentFactory.createDocument(null, new StringReader(svg));
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader documentLoader = new DocumentLoader(userAgent);
            BridgeContext bridgeContext = new BridgeContext(userAgent, documentLoader);
            bridgeContext.setDynamicState(BridgeContext.STATIC);
            GVTBuilder gvtBuilder = new GVTBuilder();
            return gvtBuilder.build(bridgeContext, document);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }*/
}
