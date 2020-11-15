package org.xbib.graphics.io.pdfbox;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xbib.graphics.io.pdfbox.color.DefaultColorMapper;
import org.xbib.graphics.io.pdfbox.color.RGBtoCMYKColorMapper;
import org.xbib.graphics.io.pdfbox.font.DefaultFontDrawer;
import org.xbib.graphics.io.pdfbox.font.FontDrawer;

import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.IOException;

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

    private void renderSVG(String name, final double scale) throws IOException {
        String uri = RenderSVGsTest.class.getResource(name).toString();
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
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
        });
    }

    private void renderSVGCMYK(String name, final double scale) throws IOException {
        String uri = RenderSVGsTest.class.getResource(name).toString();
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        Document document = f.createDocument(uri, RenderSVGsTest.class.getResourceAsStream(name));
        UserAgent userAgent = new UserAgentAdapter();
        DocumentLoader loader = new DocumentLoader(userAgent);
        BridgeContext bctx = new BridgeContext(userAgent, loader);
        bctx.setDynamicState(BridgeContext.STATIC);
        GVTBuilder builder = new GVTBuilder();
        final GraphicsNode gvtRoot = builder.build(bctx, document);
        PDDocument pdfDocument = new PDDocument();
        File parentDir = new File("build/test/svg");
        parentDir.mkdirs();
        PDPage page = new PDPage(PDRectangle.A4);
        pdfDocument.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);
        PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(pdfDocument, 400, 400);
        ICC_Profile icc_profile = ICC_Profile.getInstance(PDDocument.class.getResourceAsStream(
                "/org/apache/pdfbox/resources/icc/ISOcoated_v2_300_bas.icc"));
        DefaultColorMapper colorMapper = new RGBtoCMYKColorMapper(icc_profile, pdfDocument);
        pdfBoxGraphics2D.setColorMapper(colorMapper);
        FontDrawer fontDrawer;
        contentStream.beginText();
        contentStream.setStrokingColor(0.0f, 0.0f, 0.0f, 1.0f);
        contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f, 1.0f);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 15);
        contentStream.setTextMatrix(Matrix.getTranslateInstance(10, 800));
        contentStream.showText("Mode: CMYK colorspace");
        contentStream.endText();
        fontDrawer = new DefaultFontDrawer();
        pdfBoxGraphics2D.setFontTextDrawer(fontDrawer);
        pdfBoxGraphics2D.scale(scale, scale);
        gvtRoot.paint(pdfBoxGraphics2D);
        pdfBoxGraphics2D.dispose();
        PDFormXObject appearanceStream = pdfBoxGraphics2D.getXFormObject();
        Matrix matrix = new Matrix();
        matrix.translate(0, 300);
        contentStream.transform(matrix);
        contentStream.drawForm(appearanceStream);
        contentStream.close();
        String baseName = name.substring(0, name.lastIndexOf('.'));
        pdfDocument.save(new File(parentDir, baseName + ".pdf"));
        pdfDocument.close();
    }
}
