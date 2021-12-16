package org.xbib.graphics.svg;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SVGDiagram {

    final Map<String, SVGElement> idMap = new HashMap<>();

    SVGRoot root;

    final SVGUniverse universe;

    private final Rectangle deviceViewport = new Rectangle(100, 100);

    protected boolean ignoreClipHeuristic = false;

    final URI xmlBase;

    public SVGDiagram(URI xmlBase, SVGUniverse universe) {
        this.universe = universe;
        this.xmlBase = xmlBase;
    }

    public void render(Graphics2D g) throws SVGException, IOException {
        root.renderToViewport(g);
    }

    public List<List<SVGElement>> pick(Point2D point, List<List<SVGElement>> retVec) throws SVGException, IOException {
        return pick(point, false, retVec);
    }

    public List<List<SVGElement>> pick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        if (retVec == null) {
            retVec = new ArrayList<>();
        }
        root.pick(point, boundingBox, retVec);
        return retVec;
    }

    public List<List<SVGElement>> pick(Rectangle2D pickArea, List<List<SVGElement>> retVec) throws SVGException, IOException {
        return pick(pickArea, false, retVec);
    }

    public List<List<SVGElement>> pick(Rectangle2D pickArea, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        if (retVec == null) {
            retVec = new ArrayList<>();
        }
        root.pick(pickArea, new AffineTransform(), boundingBox, retVec);
        return retVec;
    }

    public SVGUniverse getUniverse() {
        return universe;
    }

    public URI getXMLBase() {
        return xmlBase;
    }

    public float getWidth() {
        if (root == null) return 0;
        return root.getDeviceWidth();
    }

    public float getHeight() {
        if (root == null) return 0;
        return root.getDeviceHeight();
    }

    public Rectangle2D getViewRect(Rectangle2D rect) {
        if (root != null) return root.getDeviceRect(rect);
        return rect;
    }

    public Rectangle2D getViewRect() {
        return getViewRect(new Rectangle2D.Double());
    }

    public SVGElement getElement(String name) {
        return idMap.get(name);
    }

    public void setElement(String name, SVGElement node) {
        idMap.put(name, node);
    }

    public void removeElement(String name) {
        idMap.remove(name);
    }

    public SVGRoot getRoot() {
        return root;
    }

    public void setRoot(SVGRoot root) {
        this.root = root;
        root.setDiagram(this);
    }

    public boolean ignoringClipHeuristic() {
        return ignoreClipHeuristic;
    }

    public void setIgnoringClipHeuristic(boolean ignoreClipHeuristic) {
        this.ignoreClipHeuristic = ignoreClipHeuristic;
    }

    public void updateTime(double curTime) throws SVGException, IOException {
        if (root == null) return;
        root.updateTime(curTime);
    }

    public Rectangle getDeviceViewport() {
        return deviceViewport;
    }

    public void setDeviceViewport(Rectangle deviceViewport) {
        this.deviceViewport.setBounds(deviceViewport);
        if (root != null) {
            try {
                root.build();
            } catch (SVGException | IOException ex) {
                Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, "Could not build document", ex);
            }
        }
    }
}
