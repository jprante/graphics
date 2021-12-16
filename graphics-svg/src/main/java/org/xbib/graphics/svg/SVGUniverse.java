package org.xbib.graphics.svg;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xbib.graphics.svg.app.beans.SVGIcon;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class SVGUniverse {

    final HashMap<URI, SVGDiagram> loadedDocs = new HashMap<URI, SVGDiagram>();

    final HashMap<String, Font> loadedFonts = new HashMap<String, Font>();

    final HashMap<URL, SoftReference<BufferedImage>> loadedImages = new HashMap<URL, SoftReference<BufferedImage>>();

    public static final String INPUTSTREAM_SCHEME = "svgSalamander";

    protected double curTime = 0.0;

    private boolean verbose = false;

    private boolean imageDataInlineOnly = false;

    public SVGUniverse() {
    }

    public void clear() {
        loadedDocs.clear();
        loadedFonts.clear();
        loadedImages.clear();
    }

    public double getCurTime() {
        return curTime;
    }

    public void setCurTime(double curTime) {
        double oldTime = this.curTime;
        this.curTime = curTime;
    }

    public void updateTime() throws SVGException, IOException {
        for (SVGDiagram dia : loadedDocs.values()) {
            dia.updateTime(curTime);
        }
    }

    void registerFont(Font font) {
        loadedFonts.put(font.getFontFace().getFontFamily(), font);
    }

    public Font getDefaultFont() {
        for (Font font : loadedFonts.values()) {
            return font;
        }
        return null;
    }

    public Font getFont(String fontName) {
        return loadedFonts.get(fontName);
    }

    URL registerImage(URI imageURI) {
        String scheme = imageURI.getScheme();
        if (scheme.equals("data")) {
            String path = imageURI.getRawSchemeSpecificPart();
            int idx = path.indexOf(';');
            String mime = path.substring(0, idx);
            String content = path.substring(idx + 1);
            if (content.startsWith("base64")) {
                content = content.substring(6);
                try {
                    byte[] b = Base64.getDecoder().decode(content.getBytes(StandardCharsets.US_ASCII));
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(b));
                    URL url;
                    int urlIdx = 0;
                    while (true) {
                        url = new URL("inlineImage", "localhost", "img" + urlIdx);
                        if (!loadedImages.containsKey(url)) {
                            break;
                        }
                        urlIdx++;
                    }
                    SoftReference<BufferedImage> ref = new SoftReference<>(img);
                    loadedImages.put(url, ref);
                    return url;
                } catch (IOException ex) {
                    Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                            "Could not decode inline image", ex);
                }
            }
            return null;
        } else {
            try {
                URL url = imageURI.toURL();
                registerImage(url);
                return url;
            } catch (MalformedURLException ex) {
                Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                        "Bad url", ex);
            }
            return null;
        }
    }

    void registerImage(URL imageURL) {
        if (loadedImages.containsKey(imageURL)) {
            return;
        }
        SoftReference<BufferedImage> ref;
        try {
            String fileName = imageURL.getFile();
            if (".svg".equalsIgnoreCase(fileName.substring(fileName.length() - 4))) {
                SVGIcon icon = new SVGIcon();
                icon.setSvgURI(imageURL.toURI());
                BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                icon.paintIcon(null, g, 0, 0);
                g.dispose();
                ref = new SoftReference<BufferedImage>(img);
            } else {
                BufferedImage img = ImageIO.read(imageURL);
                ref = new SoftReference<BufferedImage>(img);
            }
            loadedImages.put(imageURL, ref);
        } catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not load image: " + imageURL, e);
        }
    }

    BufferedImage getImage(URL imageURL) throws IOException {
        SoftReference<BufferedImage> ref = loadedImages.get(imageURL);
        if (ref == null) {
            return null;
        }
        BufferedImage img = ref.get();
        if (img == null) {
            img = ImageIO.read(imageURL);
            ref = new SoftReference<>(img);
            loadedImages.put(imageURL, ref);
        }
        return img;
    }

    public SVGElement getElement(URI path) {
        return getElement(path, true);
    }

    public SVGElement getElement(URL path) {
        try {
            URI uri = new URI(path.toString());
            return getElement(uri, true);
        } catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not parse url " + path, e);
        }
        return null;
    }

    public URI cleanUri(URI uri) {
        String scheme = uri.getScheme();
        String schemeSpecific = uri.getSchemeSpecificPart();
        String frag = uri.getFragment();
        if (schemeSpecific.startsWith("file:///")) {
            schemeSpecific = "file:/" + schemeSpecific.substring(8);
        } else {
            return uri;
        }
        try {
            return new URI(scheme, schemeSpecific, frag);
        } catch (URISyntaxException ex) {
            Logger.getLogger(SVGUniverse.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SVGElement getElement(URI path, boolean loadIfAbsent) {
        try {
            path = cleanUri(path);
            URI xmlBase = new URI(path.getScheme(), path.getSchemeSpecificPart(), null);
            SVGDiagram dia = loadedDocs.get(xmlBase);
            if (dia == null && loadIfAbsent) {
                URL url = xmlBase.toURL();
                loadSVG(url, false);
                dia = loadedDocs.get(xmlBase);
                if (dia == null) {
                    return null;
                }
            }
            String fragment = path.getFragment();
            return fragment == null ? dia.getRoot() : dia.getElement(fragment);
        } catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not parse path " + path, e);
            return null;
        }
    }

    public SVGDiagram getDiagram(URI xmlBase) {
        return getDiagram(xmlBase, true);
    }

    public SVGDiagram getDiagram(URI xmlBase, boolean loadIfAbsent) {
        if (xmlBase == null) {
            return null;
        }
        SVGDiagram dia = loadedDocs.get(xmlBase);
        if (dia != null || !loadIfAbsent) {
            return dia;
        }
        try {
            URL url;
            if ("jar".equals(xmlBase.getScheme()) && xmlBase.getPath() != null && !xmlBase.getPath().contains("!/")) {
                url = SVGUniverse.class.getResource(xmlBase.getPath());
            } else {
                url = xmlBase.toURL();
            }
            loadSVG(url, false);
            dia = loadedDocs.get(xmlBase);
            return dia;
        } catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not parse", e);
        }
        return null;
    }
    private InputStream createDocumentInputStream(InputStream is) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(is);
        bin.mark(2);
        int b0 = bin.read();
        int b1 = bin.read();
        bin.reset();
        if ((b1 << 8 | b0) == GZIPInputStream.GZIP_MAGIC) {
            return new GZIPInputStream(bin);
        } else {
            //Plain text
            return bin;
        }
    }

    public URI loadSVG(URL docRoot) {
        return loadSVG(docRoot, false);
    }

    public URI loadSVG(URL docRoot, boolean forceLoad) {
        try {
            URI uri = new URI(docRoot.toString());
            if (loadedDocs.containsKey(uri) && !forceLoad) {
                return uri;
            }
            InputStream is = docRoot.openStream();
            URI result = loadSVG(uri, new InputSource(createDocumentInputStream(is)));
            is.close();
            return result;
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not parse", ex);
        }
        return null;
    }

    public URI loadSVG(InputStream is, String name) throws IOException {
        return loadSVG(is, name, false);
    }

    public URI loadSVG(InputStream is, String name, boolean forceLoad) throws IOException {
        URI uri = getStreamBuiltURI(name);
        if (uri == null) {
            return null;
        }
        if (loadedDocs.containsKey(uri) && !forceLoad) {
            return uri;
        }

        return loadSVG(uri, new InputSource(createDocumentInputStream(is)));
    }

    public URI loadSVG(Reader reader, String name) {
        return loadSVG(reader, name, false);
    }

    public URI loadSVG(Reader reader, String name, boolean forceLoad) {
        URI uri = getStreamBuiltURI(name);
        if (uri == null) {
            return null;
        }
        if (loadedDocs.containsKey(uri) && !forceLoad) {
            return uri;
        }
        return loadSVG(uri, new InputSource(reader));
    }

    public URI getStreamBuiltURI(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        if (name.charAt(0) != '/') {
            name = '/' + name;
        }
        try {
            return new URI(INPUTSTREAM_SCHEME, name, null);
        } catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not parse", e);
            return null;
        }
    }

    static ThreadLocal<SAXParser> threadSAXParser = new ThreadLocal<>();

    private XMLReader getXMLReader() throws SAXException, ParserConfigurationException {
        SAXParser saxParser = threadSAXParser.get();
        if (saxParser == null) {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            saxParser = saxParserFactory.newSAXParser();
            threadSAXParser.set(saxParser);
        }
        return saxParser.getXMLReader();
    }

    protected URI loadSVG(URI xmlBase, InputSource is) {
        xmlBase = cleanUri(xmlBase);
        SVGLoader handler = new SVGLoader(xmlBase, this);
        loadedDocs.put(xmlBase, handler.getLoadedDiagram());
        try {
            XMLReader reader = getXMLReader();
            reader.setEntityResolver((publicId, systemId) -> new InputSource(new ByteArrayInputStream(new byte[0])));
            reader.setContentHandler(handler);
            reader.parse(is);
            handler.getLoadedDiagram().updateTime(curTime);
            return xmlBase;
        } catch (SAXParseException sex) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Error processing " + xmlBase, sex);
            loadedDocs.remove(xmlBase);
            return null;
        } catch (Throwable e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not load SVG " + xmlBase, e);
        }
        return null;
    }

    public ArrayList<URI> getLoadedDocumentURIs() {
        return new ArrayList<URI>(loadedDocs.keySet());
    }

    public void removeDocument(URI uri) {
        uri = cleanUri(uri);
        loadedDocs.remove(uri);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isImageDataInlineOnly() {
        return imageDataInlineOnly;
    }

    public void setImageDataInlineOnly(boolean imageDataInlineOnly) {
        this.imageDataInlineOnly = imageDataInlineOnly;
    }
}
