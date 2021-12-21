package org.xbib.graphics.svg.element.shape;

import org.xbib.graphics.svg.util.MarkerPos;
import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Marker extends Group {

    public static final int MARKER_START = 0;

    public static final int MARKER_MID = 1;

    public static final int MARKER_END = 2;

    private AffineTransform markerXform;

    private Rectangle2D viewBox;

    private float refX;

    private float refY;

    private float markerWidth = 1;

    private float markerHeight = 1;

    private boolean markerUnitsStrokeWidth = true;

    @Override
    public String getTagName() {
        return "marker";
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("refX"))) {
            refX = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("refY"))) {
            refY = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("markerWidth"))) {
            markerWidth = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("markerHeight"))) {
            markerHeight = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("viewBox"))) {
            float[] dim = sty.getFloatList();
            viewBox = new Rectangle2D.Float(dim[0], dim[1], dim[2], dim[3]);
        }
        if (viewBox == null) {
            viewBox = new Rectangle(0, 0, 1, 1);
        }
        if (getPres(sty.setName("markerUnits"))) {
            String markerUnits = sty.getStringValue();
            if (markerUnits != null && markerUnits.equals("userSpaceOnUse")) {
                markerUnitsStrokeWidth = false;
            }
        }
        AffineTransform viewXform = new AffineTransform();
        viewXform.scale(1.0 / viewBox.getWidth(), 1.0 / viewBox.getHeight());
        viewXform.translate(-viewBox.getX(), -viewBox.getY());
        markerXform = new AffineTransform();
        markerXform.scale(markerWidth, markerHeight);
        markerXform.concatenate(viewXform);
        markerXform.translate(-refX, -refY);
    }

    @Override
    protected boolean outsideClip(Graphics2D g) throws SVGException {
        Shape clip = g.getClip();
        Rectangle2D rect = super.getBoundingBox();
        return clip != null && !clip.intersects(rect);
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        AffineTransform oldXform = g.getTransform();
        g.transform(markerXform);
        super.doRender(g);
        g.setTransform(oldXform);
    }

    public void render(Graphics2D g, MarkerPos pos, float strokeWidth) throws SVGException, IOException {
        AffineTransform cacheXform = g.getTransform();
        g.translate(pos.getX(), pos.getY());
        if (markerUnitsStrokeWidth) {
            g.scale(strokeWidth, strokeWidth);
        }
        g.rotate(Math.atan2(pos.getDy(), pos.getDx()));
        g.transform(markerXform);
        super.doRender(g);
        g.setTransform(cacheXform);
    }

    @Override
    public Shape getShape() {
        Shape shape = super.getShape();
        return markerXform.createTransformedShape(shape);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        Rectangle2D rect = super.getBoundingBox();
        return markerXform.createTransformedShape(rect).getBounds2D();
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException, IOException {
        boolean changeState = super.updateTime(curTime);
        build();
        return changeState;
    }
}
