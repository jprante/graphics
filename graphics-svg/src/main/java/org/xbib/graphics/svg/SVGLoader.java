package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.Circle;
import org.xbib.graphics.svg.element.ClipPath;
import org.xbib.graphics.svg.element.Defs;
import org.xbib.graphics.svg.element.Desc;
import org.xbib.graphics.svg.element.Ellipse;
import org.xbib.graphics.svg.element.Filter;
import org.xbib.graphics.svg.element.Font;
import org.xbib.graphics.svg.element.Group;
import org.xbib.graphics.svg.element.Line;
import org.xbib.graphics.svg.element.Mask;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.element.ShapeElement;
import org.xbib.graphics.svg.element.Style;
import org.xbib.graphics.svg.element.filtereffects.GaussianBlur;
import org.xbib.graphics.svg.element.glyph.Glyph;
import org.xbib.graphics.svg.element.glyph.MissingGlyph;
import org.xbib.graphics.svg.element.gradient.LinearGradient;
import org.xbib.graphics.svg.element.gradient.RadialGradient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SVGLoader extends DefaultHandler {

    private static final Logger logger = Logger.getLogger(SVGLoader.class.getName());

    final Map<String, Class<?>> nodeClasses = new HashMap<>();

    final LinkedList<SVGElement> buildStack = new LinkedList<>();

    final Set<String> ignoreClasses = new HashSet<>();

    final SVGLoaderHelper helper;

    final SVGDiagram diagram;

    int skipNonSVGTagDepth = 0;

    int indent = 0;

    public SVGLoader(URI xmlBase, SVGUniverse universe) {
        diagram = new SVGDiagram(xmlBase, universe);
        nodeClasses.put("a", A.class);
        nodeClasses.put("circle", Circle.class);
        nodeClasses.put("clippath", ClipPath.class);
        nodeClasses.put("defs", Defs.class);
        nodeClasses.put("desc", Desc.class);
        nodeClasses.put("ellipse", Ellipse.class);
        nodeClasses.put("filter", Filter.class);
        nodeClasses.put(GaussianBlur.TAG_NAME, GaussianBlur.class);
        nodeClasses.put("font", Font.class);
        nodeClasses.put("font-face", FontFace.class);
        nodeClasses.put("g", Group.class);
        nodeClasses.put("glyph", Glyph.class);
        nodeClasses.put("hkern", Hkern.class);
        nodeClasses.put("image", ImageSVG.class);
        nodeClasses.put("line", Line.class);
        nodeClasses.put("lineargradient", LinearGradient.class);
        nodeClasses.put("marker", Marker.class);
        nodeClasses.put("mask", Mask.class);
        nodeClasses.put("metadata", Metadata.class);
        nodeClasses.put("missing-glyph", MissingGlyph.class);
        nodeClasses.put("path", Path.class);
        nodeClasses.put("pattern", PatternSVG.class);
        nodeClasses.put("polygon", Polygon.class);
        nodeClasses.put("polyline", Polyline.class);
        nodeClasses.put("radialgradient", RadialGradient.class);
        nodeClasses.put("rect", Rect.class);
        nodeClasses.put("shape", ShapeElement.class);
        nodeClasses.put("stop", Stop.class);
        nodeClasses.put("style", Style.class);
        nodeClasses.put("svg", SVGRoot.class);
        nodeClasses.put("symbol", Symbol.class);
        nodeClasses.put("text", Text.class);
        nodeClasses.put("title", Title.class);
        nodeClasses.put("tspan", Tspan.class);
        nodeClasses.put("use", Use.class);
        ignoreClasses.add("midpointstop");
        helper = new SVGLoaderHelper(xmlBase, universe, diagram);
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        indent++;
        if (skipNonSVGTagDepth != 0 || (!namespaceURI.equals("") && !namespaceURI.equals(SVGElement.SVG_NS))) {
            skipNonSVGTagDepth++;
            return;
        }
        sName = sName.toLowerCase();
        Object obj = nodeClasses.get(sName);
        if (obj == null) {
            return;
        }
        try {
            Class<?> cls = (Class<?>) obj;
            SVGElement svgEle = (SVGElement) cls.getDeclaredConstructor().newInstance();
            SVGElement parent = null;
            if (buildStack.size() != 0) parent = buildStack.getLast();
            svgEle.loaderStartElement(helper, attrs, parent);
            buildStack.addLast(svgEle);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load", e);
            throw new SAXException(e);
        }

    }

    @Override
    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
        indent--;
        if (skipNonSVGTagDepth != 0) {
            skipNonSVGTagDepth--;
            return;
        }
        sName = sName.toLowerCase();
        Object obj = nodeClasses.get(sName);
        if (obj == null) {
            return;
        }
        try {
            SVGElement svgEle = buildStack.removeLast();
            svgEle.loaderEndElement(helper);
            SVGElement parent = null;
            if (buildStack.size() != 0) {
                parent = buildStack.getLast();
            }
            if (parent != null) {
                parent.loaderAddChild(helper, svgEle);
            } else {
                diagram.setRoot((SVGRoot) svgEle);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not parse", e);
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] buf, int offset, int len) {
        if (skipNonSVGTagDepth != 0) {
            return;
        }
        if (buildStack.size() != 0) {
            SVGElement parent = buildStack.getLast();
            String s = new String(buf, offset, len);
            parent.loaderAddText(helper, s);
        }
    }

    @Override
    public void processingInstruction(String target, String data) {
    }

    public SVGDiagram getLoadedDiagram() {
        return diagram;
    }
}
