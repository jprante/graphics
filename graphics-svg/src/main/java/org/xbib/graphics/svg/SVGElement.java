package org.xbib.graphics.svg;

import org.xbib.graphics.svg.pathcmd.BuildHistory;
import org.xbib.graphics.svg.pathcmd.PathCommand;
import org.xbib.graphics.svg.pathcmd.PathParser;
import org.xbib.graphics.svg.xml.StyleAttribute;
import org.xbib.graphics.svg.xml.StyleSheet;
import org.xbib.graphics.svg.xml.XMLParseUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class SVGElement {

    public static final String SVG_NS = "http://www.w3.org/2000/svg";

    protected SVGElement parent;

    protected final ArrayList<SVGElement> children = new ArrayList<>();

    protected String id;

    protected String cssClass;

    protected final HashMap<String, StyleAttribute> inlineStyles = new HashMap<>();

    protected final HashMap<String, StyleAttribute> presAttribs = new HashMap<>();

    protected URI xmlBase = null;

    protected SVGDiagram diagram;

    private static final Pattern TRANSFORM_PATTERN = Pattern.compile("\\w+\\([^)]*\\)");

    public SVGElement() {
        this(null, null, null);
    }

    public SVGElement(String id, SVGElement parent) {
        this(id, null, parent);
    }

    public SVGElement(String id, String cssClass, SVGElement parent) {
        this.id = id;
        this.cssClass = cssClass;
        this.parent = parent;
    }

    abstract public String getTagName();

    public SVGElement getParent() {
        return parent;
    }

    void setParent(SVGElement parent) {
        this.parent = parent;
    }

    public List<SVGElement> getPath(List<SVGElement> retVec) {
        if (retVec == null) {
            retVec = new ArrayList<SVGElement>();
        }
        if (parent != null) {
            parent.getPath(retVec);
        }
        retVec.add(this);
        return retVec;
    }

    public List<SVGElement> getChildren(List<SVGElement> retVec) {
        if (retVec == null) {
            retVec = new ArrayList<SVGElement>();
        }

        retVec.addAll(children);

        return retVec;
    }

    public SVGElement getChild(String id) {
        for (SVGElement ele : children) {
            String eleId = ele.getId();
            if (eleId != null && eleId.equals(id)) {
                return ele;
            }
        }
        return null;
    }

    public int indexOfChild(SVGElement child) {
        return children.indexOf(child);
    }

    public void swapChildren(int i, int j) throws SVGException, IOException {
        if (i < 0 || i >= children.size() || j < 0 || j >= children.size()) {
            return;
        }
        SVGElement temp = children.get(i);
        children.set(i, children.get(j));
        children.set(j, temp);
        build();
    }

    public void loaderStartElement(SVGLoaderHelper helper, Attributes attrs, SVGElement parent) throws SAXException {
        this.parent = parent;
        this.diagram = helper.diagram;
        this.id = attrs.getValue("id");
        if (this.id != null && !this.id.equals("")) {
            this.id = this.id.intern();
            diagram.setElement(this.id, this);
        }
        String className = attrs.getValue("class");
        this.cssClass = (className == null || className.equals("")) ? null : className.intern();
        String style = attrs.getValue("style");
        if (style != null) {
            HashMap<?, ?> map = XMLParseUtil.parseStyle(style, inlineStyles);
        }
        String base = attrs.getValue("xml:base");
        if (base != null && !base.equals("")) {
            try {
                xmlBase = new URI(base);
            } catch (Exception e) {
                throw new SAXException(e);
            }
        }
        int numAttrs = attrs.getLength();
        for (int i = 0; i < numAttrs; i++) {
            String name = attrs.getQName(i).intern();
            String value = attrs.getValue(i);
            presAttribs.put(name, new StyleAttribute(name, value == null ? null : value.intern()));
        }
    }

    public Set<String> getInlineAttributes() {
        return inlineStyles.keySet();
    }

    public Set<String> getPresentationAttributes() {
        return presAttribs.keySet();
    }

    public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
        children.add(child);
        child.parent = this;
        child.setDiagram(diagram);
    }

    protected void setDiagram(SVGDiagram diagram) {
        this.diagram = diagram;
        diagram.setElement(id, this);
        for (SVGElement ele : children) {
            ele.setDiagram(diagram);
        }
    }

    public void removeChild(SVGElement child) throws SVGElementException {
        if (!children.contains(child)) {
            throw new SVGElementException(this, "Element does not contain child " + child);
        }
        children.remove(child);
    }

    public void loaderAddText(SVGLoaderHelper helper, String text) {
    }

    public void loaderEndElement(SVGLoaderHelper helper) throws SVGParseException {
    }

    protected void build() throws SVGException, IOException {
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("id"))) {
            String newId = sty.getStringValue();
            if (!newId.equals(id)) {
                diagram.removeElement(id);
                id = newId;
                diagram.setElement(this.id, this);
            }
        }
        if (getPres(sty.setName("class"))) {
            cssClass = sty.getStringValue();
        }
        if (getPres(sty.setName("xml:base"))) {
            xmlBase = sty.getURIValue();
        }
        for (SVGElement ele : children) {
            ele.build();
        }
    }

    public URI getXMLBase() {
        return xmlBase != null ? xmlBase : (parent != null ? parent.getXMLBase() : diagram.getXMLBase());
    }

    public String getId() {
        return id;
    }

    LinkedList<SVGElement> contexts = new LinkedList<>();

    protected void pushParentContext(SVGElement context) {
        contexts.addLast(context);
    }

    protected SVGElement popParentContext() {
        return contexts.removeLast();
    }

    protected SVGElement getParentContext() {
        return contexts.isEmpty() ? null : contexts.getLast();
    }

    public SVGRoot getRoot() {
        return parent == null ? null : parent.getRoot();
    }

    public boolean getStyle(StyleAttribute attrib) throws SVGException {
        return getStyle(attrib, true);
    }

    public boolean getStyle(StyleAttribute attrib, boolean recursive) throws SVGException {
        return getStyle(attrib, recursive, true);
    }

    public boolean getStyle(StyleAttribute attrib, boolean recursive, boolean evalAnimation)
            throws SVGException {
        String styName = attrib.getName();
        StyleAttribute styAttr = inlineStyles.get(styName);
        attrib.setStringValue(styAttr == null ? "" : styAttr.getStringValue());
        if (styAttr != null) {
            return true;
        }
        StyleAttribute presAttr = presAttribs.get(styName);
        attrib.setStringValue(presAttr == null ? "" : presAttr.getStringValue());
        if (presAttr != null) {
            return true;
        }
        SVGRoot root = getRoot();
        if (root != null) {
            StyleSheet ss = root.getStyleSheet();
            if (ss != null) {
                return ss.getStyle(attrib, getTagName(), cssClass);
            }
        }
        if (recursive) {
            SVGElement parentContext = getParentContext();
            if (parentContext != null) {
                return parentContext.getStyle(attrib, true);
            }
            if (parent != null) {
                return parent.getStyle(attrib, true);
            }
        }
        return false;
    }

    public StyleAttribute getStyleAbsolute(String styName) {
        //Check for local inline styles
        return inlineStyles.get(styName);
    }

    public boolean getPres(StyleAttribute attrib) throws SVGException {
        String presName = attrib.getName();
        StyleAttribute presAttr = presAttribs.get(presName);
        attrib.setStringValue(presAttr == null ? "" : presAttr.getStringValue());
        return presAttr != null;
    }

    public StyleAttribute getPresAbsolute(String styName) {
        return presAttribs.get(styName);
    }

    static protected AffineTransform parseTransform(String val) throws SVGException {
        final Matcher matchExpression = TRANSFORM_PATTERN.matcher("");

        AffineTransform retXform = new AffineTransform();

        matchExpression.reset(val);
        while (matchExpression.find()) {
            retXform.concatenate(parseSingleTransform(matchExpression.group()));
        }

        return retXform;
    }

    private static final Pattern WORD_PATTERN = Pattern.compile("([a-zA-Z]+|-?\\d+(\\.\\d+)?([eE]-?\\d+)?|-?\\.\\d+([eE]-?\\d+)?)");

    static public AffineTransform parseSingleTransform(String val) throws SVGException {
        final Matcher matchWord = WORD_PATTERN.matcher("");
        AffineTransform retXform = new AffineTransform();
        matchWord.reset(val);
        if (!matchWord.find()) {
            return retXform;
        }
        String function = matchWord.group().toLowerCase();
        LinkedList<String> termList = new LinkedList<String>();
        while (matchWord.find()) {
            termList.add(matchWord.group());
        }
        double[] terms = new double[termList.size()];
        Iterator<String> it = termList.iterator();
        int count = 0;
        while (it.hasNext()) {
            terms[count++] = XMLParseUtil.parseDouble(it.next());
        }
        switch (function) {
            case "matrix":
                retXform.setTransform(terms[0], terms[1], terms[2], terms[3], terms[4], terms[5]);
                break;
            case "translate":
                if (terms.length == 1) {
                    retXform.setToTranslation(terms[0], 0);
                } else {
                    retXform.setToTranslation(terms[0], terms[1]);
                }
                break;
            case "scale":
                if (terms.length > 1) {
                    retXform.setToScale(terms[0], terms[1]);
                } else {
                    retXform.setToScale(terms[0], terms[0]);
                }
                break;
            case "rotate":
                if (terms.length > 2) {
                    retXform.setToRotation(Math.toRadians(terms[0]), terms[1], terms[2]);
                } else {
                    retXform.setToRotation(Math.toRadians(terms[0]));
                }
                break;
            case "skewx":
                retXform.setToShear(Math.toRadians(terms[0]), 0.0);
                break;
            case "skewy":
                retXform.setToShear(0.0, Math.toRadians(terms[0]));
                break;
            default:
                throw new SVGException("Unknown transform type");
        }
        return retXform;
    }

    static protected PathCommand[] parsePathList(String list) {
        return new PathParser(list).parsePathCommand();
    }

    static protected GeneralPath buildPath(String text, int windingRule) {
        PathCommand[] commands = parsePathList(text);
        int numKnots = 2;
        for (PathCommand command : commands) {
            numKnots += command.getNumKnotsAdded();
        }
        GeneralPath path = new GeneralPath(windingRule, numKnots);
        BuildHistory hist = new BuildHistory();
        for (PathCommand cmd : commands) {
            cmd.appendPath(path, hist);
        }
        return path;
    }

    abstract public boolean updateTime(double curTime) throws SVGException, IOException;

    public int getNumChildren() {
        return children.size();
    }

    public SVGElement getChild(int i) {
        return children.get(i);
    }

    public double lerp(double t0, double t1, double alpha) {
        return (1 - alpha) * t0 + alpha * t1;
    }
}
