package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.FillElement;
import org.xbib.graphics.svg.element.RenderableElement;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

public class PatternSVG extends FillElement {

    public static final String TAG_NAME = "pattern";

    public static final int GU_OBJECT_BOUNDING_BOX = 0;

    public static final int GU_USER_SPACE_ON_USE = 1;

    int gradientUnits = GU_OBJECT_BOUNDING_BOX;

    float x;

    float y;

    float width;

    float height;

    AffineTransform patternXform = new AffineTransform();

    Rectangle2D.Float viewBox;

    Paint texPaint;

    public PatternSVG() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
        super.loaderAddChild(helper, child);
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        String href = null;
        if (getPres(sty.setName("xlink:href"))) {
            href = sty.getStringValue();
        }
        if (href != null) {
            URI src = getXMLBase().resolve(href);
            PatternSVG patSrc = (PatternSVG) diagram.getUniverse().getElement(src);
            gradientUnits = patSrc.gradientUnits;
            x = patSrc.x;
            y = patSrc.y;
            width = patSrc.width;
            height = patSrc.height;
            viewBox = patSrc.viewBox;
            patternXform.setTransform(patSrc.patternXform);
            children.addAll(patSrc.children);
        }
        String gradientUnits = "";
        if (getPres(sty.setName("gradientUnits"))) {
            gradientUnits = sty.getStringValue().toLowerCase();
        }
        if (gradientUnits.equals("userspaceonuse")) {
            this.gradientUnits = GU_USER_SPACE_ON_USE;
        } else {
            this.gradientUnits = GU_OBJECT_BOUNDING_BOX;
        }
        String patternTransform = "";
        if (getPres(sty.setName("patternTransform"))) {
            patternTransform = sty.getStringValue();
        }
        patternXform = parseTransform(patternTransform);
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
        if (getPres(sty.setName("viewBox"))) {
            float[] dim = sty.getFloatList();
            viewBox = new Rectangle2D.Float(dim[0], dim[1], dim[2], dim[3]);
        }
        preparePattern();
    }

    protected void preparePattern() throws SVGException, IOException {
        int tileWidth = (int) width;
        int tileHeight = (int) height;
        float stretchX = 1f, stretchY = 1f;
        if (!patternXform.isIdentity()) {
            float xlateX = (float) patternXform.getTranslateX();
            float xlateY = (float) patternXform.getTranslateY();
            Point2D.Float pt = new Point2D.Float(), pt2 = new Point2D.Float();
            pt.setLocation(width, 0);
            patternXform.transform(pt, pt2);
            pt2.x -= xlateX;
            pt2.y -= xlateY;
            stretchX = (float) Math.sqrt(pt2.x * pt2.x + pt2.y * pt2.y) * 1.5f / width;
            pt.setLocation(height, 0);
            patternXform.transform(pt, pt2);
            pt2.x -= xlateX;
            pt2.y -= xlateY;
            stretchY = (float) Math.sqrt(pt2.x * pt2.x + pt2.y * pt2.y) * 1.5f / height;
            tileWidth *= stretchX;
            tileHeight *= stretchY;
        }
        if (tileWidth == 0 || tileHeight == 0) {
            return;
        }
        BufferedImage buf = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buf.createGraphics();
        g.setClip(0, 0, tileWidth, tileHeight);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (SVGElement ele : children) {
            if (ele instanceof RenderableElement) {
                AffineTransform xform = new AffineTransform();
                if (viewBox == null) {
                    xform.translate(-x, -y);
                } else {
                    xform.scale(tileWidth / viewBox.width, tileHeight / viewBox.height);
                    xform.translate(-viewBox.x, -viewBox.y);
                }
                g.setTransform(xform);
                ((RenderableElement) ele).render(g);
            }
        }
        g.dispose();
        if (patternXform.isIdentity()) {
            texPaint = new TexturePaint(buf, new Rectangle2D.Float(x, y, width, height));
        } else {
            patternXform.scale(1 / stretchX, 1 / stretchY);
            texPaint = new PatternPaint(buf, patternXform);
        }
    }

    @Override
    public Paint getPaint(Rectangle2D bounds, AffineTransform xform) {
        return texPaint;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }
}
