package org.xbib.graphics.pdfbox.test;

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
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xbib.graphics.pdfbox.PdfBoxGraphics2D;
import org.xbib.graphics.pdfbox.color.DefaultColorMapper;
import org.xbib.graphics.pdfbox.color.RGBtoCMYKColorMapper;
import org.xbib.graphics.pdfbox.font.DefaultFontDrawer;
import org.xbib.graphics.pdfbox.font.FontDrawer;
import org.xbib.graphics.svg.SVGDiagram;
import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.SVGUniverse;

import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

public class RenderSVGsTest extends PdfBoxGraphics2DTestBase {

    @Test
    public void testSVGs() throws IOException {
        renderSVG("barChart.svg", 0.45);
        renderSVG("gump-bench.svg", 1);
        renderSVG("json.svg", 150);
        renderSVG("heart.svg", 200);
        renderSVG("displayWebStats.svg", 200);
        renderSVG("compuserver_msn_Ford_Focus.svg", 0.7);
        renderSVG("watermark.svg", 0.4);
    }

    @Test
    public void renderFailureCases() throws IOException {
        // renderSVG("openhtml_536.svg", 1);
        renderSVG("openhtml_538_gradient.svg", .5);
    }

    @Test
    public void testGradientSVGEmulateObjectBoundingBox() throws IOException {
        renderSVG("long-gradient.svg", 0.55);
        renderSVG("tall-gradient.svg", 0.33);
        renderSVG("near-square-gradient.svg", 0.30);
        renderSVG("square-gradient.svg", 0.55);
        renderSVG("tall-gradient-downward-slope.svg", 0.33);
        renderSVG("horizontal-gradient.svg", 0.55);
    }

    @Test
    public void testSVGinCMYKColorspace() throws IOException {
        renderSVGCMYK("atmospheric-composiition.svg", 0.7);
    }

    private void renderSVG(String name, final double scale) {
        URL url = getClass().getResource(name);
        SVGUniverse svgUniverse = new SVGUniverse();
        SVGDiagram diagram = svgUniverse.getDiagram(svgUniverse.loadSVG(url));
        exportGraphic("xbibsvg", name.replace(".svg", ""), gfx -> {
            gfx.scale(scale, scale);
            try {
                diagram.render(gfx);
            } catch (SVGException e) {
                throw new IllegalArgumentException(e);
            }
        });

        /*String uri = RenderSVGsTest.class.getResource(name).toString();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
        Document document = f.createDocument(uri, RenderSVGsTest.class.getResourceAsStream(name));
        UserAgent userAgent = new UserAgentAdapter();
        DocumentLoader loader = new DocumentLoader(userAgent);
        BridgeContext bctx = new BridgeContext(userAgent, loader);
        bctx.setDynamicState(BridgeContext.STATIC);
        GVTBuilder builder = new GVTBuilder();
        final GraphicsNode gvtRoot = builder.build(bctx, document);
        this.exportGraphic("svg", name.replace(".svg", ""), gfx -> {
            gfx.scale(scale, scale);
            gvtRoot.paint(gfx);
        });*/
    }

    private void renderSVGCMYK(String name, double scale) throws IOException {
        /*String uri = RenderSVGsTest.class.getResource(name).toString();
        SAXSVGDocumentFactory documentFactory = new SAXSVGDocumentFactory();
        Document document = documentFactory.createDocument(uri, RenderSVGsTest.class.getResourceAsStream(name));
        UserAgent userAgent = new UserAgentAdapter();
        DocumentLoader documentLoader = new DocumentLoader(userAgent);
        BridgeContext bridgeContext = new BridgeContext(userAgent, documentLoader);
        bridgeContext.setDynamicState(BridgeContext.STATIC);
        GVTBuilder gvtBuilder = new GVTBuilder();
        GraphicsNode graphicsNode = gvtBuilder.build(bridgeContext, document);
        PDDocument pdfDocument = new PDDocument();
        ICC_Profile icc_profile = ICC_Profile.getInstance(PDDocument.class.getResourceAsStream(
                "/org/apache/pdfbox/resources/icc/ISOcoated_v2_300_bas.icc"));
        DefaultColorMapper colorMapper = new RGBtoCMYKColorMapper(icc_profile, pdfDocument);
        File parentDir = new File("build/test/svg");
        parentDir.mkdirs();
        PDPage page = new PDPage(PDRectangle.A4);
        pdfDocument.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);
        PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(pdfDocument, 400, 400);
        pdfBoxGraphics2D.setColorMapper(colorMapper);
        contentStream.beginText();
        contentStream.setStrokingColor(0.0f, 0.0f, 0.0f, 1.0f);
        contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f, 1.0f);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 15);
        contentStream.setTextMatrix(Matrix.getTranslateInstance(10, 800));
        contentStream.showText("Mode: CMYK colorspace");
        contentStream.endText();
        FontDrawer fontDrawer = new DefaultFontDrawer();
        pdfBoxGraphics2D.setFontTextDrawer(fontDrawer);
        pdfBoxGraphics2D.scale(scale, scale);
        graphicsNode.paint(pdfBoxGraphics2D);
        pdfBoxGraphics2D.dispose();
        PDFormXObject xFormObject = pdfBoxGraphics2D.getXFormObject();
        Matrix matrix = new Matrix();
        matrix.translate(0, 300);
        contentStream.transform(matrix);
        contentStream.drawForm(xFormObject);
        contentStream.close();
        String baseName = name.substring(0, name.lastIndexOf('.'));
        pdfDocument.save(new File(parentDir, baseName + ".pdf"));
        pdfDocument.close();*/
    }
}
