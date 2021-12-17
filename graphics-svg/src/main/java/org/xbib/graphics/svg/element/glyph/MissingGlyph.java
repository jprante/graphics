package org.xbib.graphics.svg.element.glyph;

import org.xbib.graphics.svg.SVGElementException;
import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.SVGLoaderHelper;
import org.xbib.graphics.svg.element.Font;
import org.xbib.graphics.svg.element.RenderableElement;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.element.ShapeElement;
import org.xbib.graphics.svg.pathcmd.BuildHistory;
import org.xbib.graphics.svg.pathcmd.PathCommand;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class MissingGlyph extends ShapeElement {

    public static final String TAG_NAME = "missingglyph";

    private Shape path = null;

    private float horizAdvX = -1;

    private float vertOriginX = -1;

    private float vertOriginY = -1;

    private float vertAdvY = -1;

    public MissingGlyph() {
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

        String commandList = "";
        if (getPres(sty.setName("d"))) {
            commandList = sty.getStringValue();
        }
        if (commandList != null) {
            String fillRule = getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
            PathCommand[] commands = parsePathList(commandList);
            GeneralPath buildPath = new GeneralPath(
                    fillRule.equals("evenodd") ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO,
                    commands.length);
            BuildHistory hist = new BuildHistory();
            for (PathCommand cmd : commands) {
                cmd.appendPath(buildPath, hist);
            }
            AffineTransform at = new AffineTransform();
            at.scale(1, -1);
            path = at.createTransformedShape(buildPath);
        }
        if (getPres(sty.setName("horiz-adv-x"))) {
            horizAdvX = sty.getFloatValue();
        }
        if (getPres(sty.setName("vert-origin-x"))) {
            vertOriginX = sty.getFloatValue();
        }
        if (getPres(sty.setName("vert-origin-y"))) {
            vertOriginY = sty.getFloatValue();
        }
        if (getPres(sty.setName("vert-adv-y"))) {
            vertAdvY = sty.getFloatValue();
        }
    }

    public Shape getPath() {
        return path;
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        if (path != null) {
            renderShape(g, path);
        }
        for (SVGElement ele : children) {
            if (ele instanceof RenderableElement) {
                ((RenderableElement) ele).render(g);
            }
        }
    }

    public float getHorizAdvX() {
        if (horizAdvX == -1) {
            horizAdvX = ((Font) parent).getHorizAdvX();
        }
        return horizAdvX;
    }

    public float getVertOriginX() {
        if (vertOriginX == -1) {
            vertOriginX = getHorizAdvX() / 2;
        }
        return vertOriginX;
    }

    public float getVertOriginY() {
        if (vertOriginY == -1) {
            vertOriginY = ((Font) parent).getFontFace().getAscent();
        }
        return vertOriginY;
    }

    public float getVertAdvY() {
        if (vertAdvY == -1) {
            vertAdvY = ((Font) parent).getFontFace().getUnitsPerEm();
        }
        return vertAdvY;
    }

    @Override
    public Shape getShape() {
        if (path != null) {
            return shapeToParent(path);
        }
        return null;
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        if (path != null) {
            return boundsToParent(includeStrokeInBounds(path.getBounds2D()));
        }
        return null;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }

    public void setPath(Shape path) {
        this.path = path;
    }

    public void setHorizAdvX(float horizAdvX) {
        this.horizAdvX = horizAdvX;
    }

    public void setVertOriginX(float vertOriginX) {
        this.vertOriginX = vertOriginX;
    }

    public void setVertOriginY(float vertOriginY) {
        this.vertOriginY = vertOriginY;
    }

    public void setVertAdvY(float vertAdvY) {
        this.vertAdvY = vertAdvY;
    }
}
