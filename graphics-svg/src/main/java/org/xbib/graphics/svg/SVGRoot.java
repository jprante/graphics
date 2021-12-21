package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.Defs;
import org.xbib.graphics.svg.element.shape.Group;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.element.Style;
import org.xbib.graphics.svg.xml.NumberWithUnits;
import org.xbib.graphics.svg.xml.StyleAttribute;
import org.xbib.graphics.svg.xml.StyleSheet;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;

public class SVGRoot extends Group {

    public static final String TAG_NAME = "svg";

    NumberWithUnits x;

    NumberWithUnits y;

    NumberWithUnits width;

    NumberWithUnits height;

    Rectangle2D.Float viewBox = null;

    public static final int PA_X_NONE = 0;

    public static final int PA_X_MIN = 1;

    public static final int PA_X_MID = 2;

    public static final int PA_X_MAX = 3;

    public static final int PA_Y_NONE = 0;

    public static final int PA_Y_MIN = 1;

    public static final int PA_Y_MID = 2;

    public static final int PA_Y_MAX = 3;

    public static final int PS_MEET = 0;

    public static final int PS_SLICE = 1;

    int parSpecifier = PS_MEET;
    
    int parAlignX = PA_X_MID;
    
    int parAlignY = PA_Y_MID;

    final AffineTransform viewXform = new AffineTransform();
    final Rectangle2D.Float clipRect = new Rectangle2D.Float();

    private StyleSheet styleSheet;

    public SVGRoot() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("x"))) {
            x = sty.getNumberWithUnits();
        }
        if (getPres(sty.setName("y"))) {
            y = sty.getNumberWithUnits();
        }
        if (getPres(sty.setName("width"))) {
            width = sty.getNumberWithUnits();
        }
        if (getPres(sty.setName("height"))) {
            height = sty.getNumberWithUnits();
        }
        if (getPres(sty.setName("viewBox"))) {
            float[] coords = sty.getFloatList();
            viewBox = new Rectangle2D.Float(coords[0], coords[1], coords[2], coords[3]);
        }
        if (getPres(sty.setName("preserveAspectRatio"))) {
            String preserve = sty.getStringValue();
            if (preserve.contains( "none")) {
                parAlignX = PA_X_NONE;
                parAlignY = PA_Y_NONE;
            } else if (preserve.contains( "xMinYMin")) {
                parAlignX = PA_X_MIN;
                parAlignY = PA_Y_MIN;
            } else if (preserve.contains( "xMidYMin")) {
                parAlignX = PA_X_MID;
                parAlignY = PA_Y_MIN;
            } else if (preserve.contains( "xMaxYMin")) {
                parAlignX = PA_X_MAX;
                parAlignY = PA_Y_MIN;
            } else if (preserve.contains( "xMinYMid")) {
                parAlignX = PA_X_MIN;
                parAlignY = PA_Y_MID;
            } else if (preserve.contains( "xMidYMid")) {
                parAlignX = PA_X_MID;
                parAlignY = PA_Y_MID;
            } else if (preserve.contains( "xMaxYMid")) {
                parAlignX = PA_X_MAX;
                parAlignY = PA_Y_MID;
            } else if (preserve.contains( "xMinYMax")) {
                parAlignX = PA_X_MIN;
                parAlignY = PA_Y_MAX;
            } else if (preserve.contains( "xMidYMax")) {
                parAlignX = PA_X_MID;
                parAlignY = PA_Y_MAX;
            } else if (preserve.contains( "xMaxYMax")) {
                parAlignX = PA_X_MAX;
                parAlignY = PA_Y_MAX;
            }
            if (preserve.contains( "meet")) {
                parSpecifier = PS_MEET;
            } else if (preserve.contains( "slice")) {
                parSpecifier = PS_SLICE;
            }
        }
        prepareViewport();
    }

    @Override
    public SVGRoot getRoot() {
        return this;
    }

    protected void prepareViewport() {
        Rectangle deviceViewport = diagram.getDeviceViewport();

        Rectangle2D defaultBounds;
        try {
            defaultBounds = getBoundingBox();
        } catch (SVGException ex) {
            defaultBounds = new Rectangle2D.Float();
        }

        //Determine destination rectangle
        float xx, yy, ww, hh;
        if (width != null) {
            xx = (x == null) ? 0 : StyleAttribute.convertUnitsToPixels(x.getUnits(), x.getValue());
            if (width.getUnits() == NumberWithUnits.UT_PERCENT) {
                ww = width.getValue() * deviceViewport.width;
            } else {
                ww = StyleAttribute.convertUnitsToPixels(width.getUnits(), width.getValue());
            }
        } else if (viewBox != null) {
            xx = viewBox.x;
            ww = viewBox.width;
            width = new NumberWithUnits(ww, NumberWithUnits.UT_PX);
            x = new NumberWithUnits(xx, NumberWithUnits.UT_PX);
        } else {
            //Estimate size from scene bounding box
            xx = (float) defaultBounds.getX();
            ww = (float) defaultBounds.getWidth();
            width = new NumberWithUnits(ww, NumberWithUnits.UT_PX);
            x = new NumberWithUnits(xx, NumberWithUnits.UT_PX);
        }

        if (height != null) {
            yy = (y == null) ? 0 : StyleAttribute.convertUnitsToPixels(y.getUnits(), y.getValue());
            if (height.getUnits() == NumberWithUnits.UT_PERCENT) {
                hh = height.getValue() * deviceViewport.height;
            } else {
                hh = StyleAttribute.convertUnitsToPixels(height.getUnits(), height.getValue());
            }
        } else if (viewBox != null) {
            yy = viewBox.y;
            hh = viewBox.height;
            height = new NumberWithUnits(hh, NumberWithUnits.UT_PX);
            y = new NumberWithUnits(yy, NumberWithUnits.UT_PX);
        } else {
            //Estimate size from scene bounding box
            yy = (float) defaultBounds.getY();
            hh = (float) defaultBounds.getHeight();
            height = new NumberWithUnits(hh, NumberWithUnits.UT_PX);
            y = new NumberWithUnits(yy, NumberWithUnits.UT_PX);
        }

        clipRect.setRect(xx, yy, ww, hh);
    }

    public void renderToViewport(Graphics2D g) throws SVGException, IOException {
        render(g);
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        prepareViewport();
        Rectangle targetViewport;
        Rectangle deviceViewport = diagram.getDeviceViewport();
        if (width != null && height != null) {
            float xx, yy, ww, hh;
            xx = (x == null) ? 0 : StyleAttribute.convertUnitsToPixels(x.getUnits(), x.getValue());
            if (width.getUnits() == NumberWithUnits.UT_PERCENT) {
                ww = width.getValue() * deviceViewport.width;
            } else {
                ww = StyleAttribute.convertUnitsToPixels(width.getUnits(), width.getValue());
            }
            yy = (y == null) ? 0 : StyleAttribute.convertUnitsToPixels(y.getUnits(), y.getValue());
            if (height.getUnits() == NumberWithUnits.UT_PERCENT) {
                hh = height.getValue() * deviceViewport.height;
            } else {
                hh = StyleAttribute.convertUnitsToPixels(height.getUnits(), height.getValue());
            }
            targetViewport = new Rectangle((int) xx, (int) yy, (int) ww, (int) hh);
        } else {
            targetViewport = new Rectangle(deviceViewport);
        }
        clipRect.setRect(targetViewport);
        viewXform.setTransform(calcViewportTransform(targetViewport));
        AffineTransform cachedXform = g.getTransform();
        g.transform(viewXform);
        super.doRender(g);
        g.setTransform(cachedXform);
    }

    public AffineTransform calcViewportTransform(Rectangle targetViewport) {
        AffineTransform xform = new AffineTransform();
        if (viewBox == null) {
            xform.setToIdentity();
        } else {
            xform.setToIdentity();
            xform.setToTranslation(targetViewport.x, targetViewport.y);
            xform.scale(targetViewport.width, targetViewport.height);
            xform.scale(1 / viewBox.width, 1 / viewBox.height);
            xform.translate(-viewBox.x, -viewBox.y);
        }
        return xform;
    }

    @Override
    public void doPick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        ltw = new AffineTransform(ltw);
        ltw.concatenate(viewXform);
        super.doPick(pickArea, ltw, boundingBox, retVec);
    }

    @Override
    public void doPick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException, IOException {
        Point2D xPoint = new Point2D.Double(point.getX(), point.getY());
        try {
            viewXform.inverseTransform(point, xPoint);
        } catch (NoninvertibleTransformException ex) {
            throw new SVGException(ex);
        }
        super.doPick(xPoint, boundingBox, retVec);
    }

    @Override
    public Shape getShape() {
        Shape shape = super.getShape();
        return viewXform.createTransformedShape(shape);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        Rectangle2D bbox = super.getBoundingBox();
        return viewXform.createTransformedShape(bbox).getBounds2D();
    }

    public float getDeviceWidth() {
        return clipRect.width;
    }

    public float getDeviceHeight() {
        return clipRect.height;
    }

    public Rectangle2D getDeviceRect(Rectangle2D rect) {
        rect.setRect(clipRect);
        return rect;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("x"))) {
            NumberWithUnits newVal = sty.getNumberWithUnits();
            if (!newVal.equals(x)) {
                x = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("y"))) {
            NumberWithUnits newVal = sty.getNumberWithUnits();
            if (!newVal.equals(y)) {
                y = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("width"))) {
            NumberWithUnits newVal = sty.getNumberWithUnits();
            if (!newVal.equals(width)) {
                width = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("height"))) {
            NumberWithUnits newVal = sty.getNumberWithUnits();
            if (!newVal.equals(height)) {
                height = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("viewBox"))) {
            float[] coords = sty.getFloatList();
            Rectangle2D.Float newViewBox = new Rectangle2D.Float(coords[0], coords[1], coords[2], coords[3]);
            if (!newViewBox.equals(viewBox)) {
                viewBox = newViewBox;
                shapeChange = true;
            }
        }
        if (shapeChange) {
            build();
        }
        return changeState || shapeChange;
    }

    public StyleSheet getStyleSheet() {
        if (styleSheet == null) {
            for (int i = 0; i < getNumChildren(); ++i) {
                SVGElement ele = getChild(i);
                if (ele instanceof Style) {
                    return ((Style) ele).getStyleSheet();
                } else if (ele instanceof Defs) {
                    return ((Defs) ele).getStyleSheet();
                }
            }
        }
        return styleSheet;
    }

    public void setStyleSheet(StyleSheet styleSheet) {
        this.styleSheet = styleSheet;
    }
}
