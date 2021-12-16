package org.xbib.graphics.svg;

import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Group extends ShapeElement {

    public static final String TAG_NAME = "group";

    Rectangle2D boundingBox;
    Shape cachedShape;

    public Group() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
        super.loaderAddChild(helper, child);
    }

    protected boolean outsideClip(Graphics2D g) throws SVGException {
        Shape clip = g.getClip();
        if (clip == null) {
            return false;
        }
        Rectangle2D rect = getBoundingBox();
        return !clip.intersects(rect);
    }

    @Override
    protected void doPick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        Point2D xPoint = new Point2D.Double(point.getX(), point.getY());
        if (xform != null) {
            try {
                xform.inverseTransform(point, xPoint);
            } catch (NoninvertibleTransformException ex) {
                throw new SVGException(ex);
            }
        }
        for (SVGElement ele : children) {
            if (ele instanceof RenderableElement) {
                RenderableElement rendEle = (RenderableElement) ele;
                rendEle.pick(xPoint, boundingBox, retVec);
            }
        }
    }

    @Override
    protected void doPick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        if (xform != null) {
            ltw = new AffineTransform(ltw);
            ltw.concatenate(xform);
        }
        for (SVGElement ele : children) {
            if (ele instanceof RenderableElement) {
                RenderableElement rendEle = (RenderableElement) ele;
                rendEle.pick(pickArea, ltw, boundingBox, retVec);
            }
        }
    }

    @Override
    protected void doRender(Graphics2D g) throws SVGException, IOException {
        StyleAttribute styleAttrib = new StyleAttribute();
        if (getStyle(styleAttrib.setName("display"))) {
            if (styleAttrib.getStringValue().equals("none")) {
                return;
            }
        }
        boolean ignoreClip = diagram.ignoringClipHeuristic();
        beginLayer(g);
        Iterator<SVGElement> it = children.iterator();
        Shape clip = g.getClip();
        while (it.hasNext()) {
            SVGElement ele = it.next();
            if (ele instanceof RenderableElement) {
                RenderableElement rendEle = (RenderableElement) ele;
                if (!(ele instanceof Group)) {
                    if (!ignoreClip && clip != null
                            && !clip.intersects(rendEle.getBoundingBox())) {
                        continue;
                    }
                }
                rendEle.render(g);
            }
        }
        finishLayer(g);
    }

    @Override
    public Shape getShape() {
        if (cachedShape == null) {
            calcShape();
        }
        return cachedShape;
    }

    public void calcShape() {
        Area retShape = new Area();
        for (SVGElement ele : children) {
            if (ele instanceof ShapeElement) {
                ShapeElement shpEle = (ShapeElement) ele;
                Shape shape = shpEle.getShape();
                if (shape != null) {
                    retShape.add(new Area(shape));
                }
            }
        }
        cachedShape = shapeToParent(retShape);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        if (boundingBox == null) {
            calcBoundingBox();
        }
        return boundingBox;
    }

    public void calcBoundingBox() throws SVGException {
        Rectangle2D retRect = null;
        for (SVGElement ele : children) {
            if (ele instanceof RenderableElement) {
                RenderableElement rendEle = (RenderableElement) ele;
                Rectangle2D bounds = rendEle.getBoundingBox();
                if (bounds != null && (bounds.getWidth() != 0 || bounds.getHeight() != 0)) {
                    if (retRect == null) {
                        retRect = bounds;
                    } else {
                        if (retRect.getWidth() != 0 || retRect.getHeight() != 0) {
                            retRect = retRect.createUnion(bounds);
                        }
                    }
                }
            }
        }
        if (retRect == null) {
            retRect = new Rectangle2D.Float();
        }
        boundingBox = boundsToParent(retRect);
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        for (SVGElement ele : children) {
            boolean updateVal = ele.updateTime(curTime);
            if (updateVal && ele instanceof RenderableElement) {
                ((RenderableElement) ele).setBufferImage(null);
            }
            changeState = changeState || updateVal;
            if (ele instanceof ShapeElement) {
                cachedShape = null;
            }
            if (ele instanceof RenderableElement) {
                boundingBox = null;
            }
        }
        if (changeState) {
            setBufferImage(null);
        }
        return changeState;
    }
}
