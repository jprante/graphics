package org.xbib.graphics.svg;

import org.xbib.graphics.svg.app.data.Handler;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageSVG extends RenderableElement {

    public static final String TAG_NAME = "image";

    float x = 0f;

    float y = 0f;

    float width = 0f;

    float height = 0f;

    URL imageSrc = null;

    AffineTransform xform;

    Rectangle2D bounds;

    public ImageSVG() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("x"))) {
            x = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("y"))) {
            y = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("width"))) {
            width = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("height"))) {
            height = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("xlink:href"))) {
            URI src = sty.getURIValue(getXMLBase());
            if ("data".equals(src.getScheme())) {
                imageSrc = new URL(null, src.toASCIIString(), new Handler());
            } else if (!diagram.getUniverse().isImageDataInlineOnly()) {
                try {
                    imageSrc = src.toURL();
                } catch (Exception e) {
                    Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                            "Could not parse xlink:href " + src, e);
                    imageSrc = null;
                }
            }
        }
        if (imageSrc != null) {
            diagram.getUniverse().registerImage(imageSrc);
            BufferedImage img = diagram.getUniverse().getImage(imageSrc);
            if (img == null) {
                xform = new AffineTransform();
                bounds = new Rectangle2D.Float();
                return;
            }
            if (width == 0) {
                width = img.getWidth();
            }
            if (height == 0) {
                height = img.getHeight();
            }
            xform = new AffineTransform();
            xform.translate(this.x, this.y);
            xform.scale(this.width / img.getWidth(), this.height / img.getHeight());
        }
        bounds = new Rectangle2D.Float(this.x, this.y, this.width, this.height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    protected void doPick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) {
        if (getBoundingBox().contains(point)) {
            retVec.add(getPath(null));
        }
    }

    @Override
    protected void doPick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) {
        if (ltw.createTransformedShape(getBoundingBox()).intersects(pickArea)) {
            retVec.add(getPath(null));
        }
    }

    @Override
    protected void doRender(Graphics2D g) throws SVGException, IOException {
        StyleAttribute styleAttrib = new StyleAttribute();
        if (getStyle(styleAttrib.setName("visibility"))) {
            if (!styleAttrib.getStringValue().equals("visible")) {
                return;
            }
        }
        if (getStyle(styleAttrib.setName("display"))) {
            if (styleAttrib.getStringValue().equals("none")) {
                return;
            }
        }
        beginLayer(g);
        float opacity = 1f;
        if (getStyle(styleAttrib.setName("opacity"))) {
            opacity = styleAttrib.getRatioValue();
        }
        if (opacity <= 0) {
            return;
        }
        Composite oldComp = null;
        if (opacity < 1) {
            oldComp = g.getComposite();
            Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
            g.setComposite(comp);
        }
        BufferedImage img = diagram.getUniverse().getImage(imageSrc);
        if (img == null) {
            return;
        }
        AffineTransform curXform = g.getTransform();
        g.transform(xform);
        g.drawImage(img, 0, 0, null);
        g.setTransform(curXform);
        if (oldComp != null) {
            g.setComposite(oldComp);
        }
        finishLayer(g);
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return boundsToParent(bounds);
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("x"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x) {
                x = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("y"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y) {
                y = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("width"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != width) {
                width = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("height"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != height) {
                height = newVal;
                shapeChange = true;
            }
        }
        try {
            if (getPres(sty.setName("xlink:href"))) {
                URI src = sty.getURIValue(getXMLBase());
                URL newVal = null;
                if ("data".equals(src.getScheme())) {
                    newVal = new URL(null, src.toASCIIString(), new Handler());
                } else if (!diagram.getUniverse().isImageDataInlineOnly()) {
                    newVal = src.toURL();
                }
                if (newVal != null && !newVal.equals(imageSrc)) {
                    imageSrc = newVal;
                    shapeChange = true;
                }
            }
        } catch (IllegalArgumentException ie) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Image provided with illegal value for href: \""
                            + sty.getStringValue() + '"', ie);
        } catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not parse xlink:href", e);
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }
}
