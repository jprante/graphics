package org.xbib.graphics.svg;

import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Marker extends Group {

    public static final String TAG_NAME = "marker";

    AffineTransform viewXform;

    AffineTransform markerXform;

    Rectangle2D viewBox;

    float refX;

    float refY;

    float markerWidth = 1;

    float markerHeight = 1;

    float orient = Float.NaN;

    boolean markerUnitsStrokeWidth = true;

    @Override
    public String getTagName() {
        return TAG_NAME;
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

        if (getPres(sty.setName("orient"))) {
            if ("auto".equals(sty.getStringValue())) {
                orient = Float.NaN;
            } else {
                orient = sty.getFloatValue();
            }
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
        viewXform = new AffineTransform();
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
    protected void doRender(Graphics2D g) throws SVGException, IOException {
        AffineTransform oldXform = g.getTransform();
        g.transform(markerXform);
        super.doRender(g);
        g.setTransform(oldXform);
    }

    public void render(Graphics2D g, MarkerPos pos, float strokeWidth) throws SVGException, IOException {
        AffineTransform cacheXform = g.getTransform();
        g.translate(pos.x, pos.y);
        if (markerUnitsStrokeWidth) {
            g.scale(strokeWidth, strokeWidth);
        }
        g.rotate(Math.atan2(pos.dy, pos.dx));
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

    public static final int MARKER_START = 0;

    public static final int MARKER_MID = 1;

    public static final int MARKER_END = 2;

    public static class MarkerPos {

        int type;

        double x;

        double y;

        double dx;

        double dy;

        public MarkerPos(int type, double x, double y, double dx, double dy) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }
    }

    public static class MarkerLayout {

        private final List<MarkerPos> markerList = new ArrayList<>();

        boolean started = false;

        public void layout(Shape shape) {
            double px = 0;
            double py = 0;
            double[] coords = new double[6];
            for (PathIterator it = shape.getPathIterator(null);
                 !it.isDone(); it.next()) {
                switch (it.currentSegment(coords)) {
                    case PathIterator.SEG_MOVETO:
                        px = coords[0];
                        py = coords[1];
                        started = false;
                        break;
                    case PathIterator.SEG_CLOSE:
                        started = false;
                        break;
                    case PathIterator.SEG_LINETO: {
                        double x = coords[0];
                        double y = coords[1];
                        markerIn(px, py, x - px, y - py);
                        markerOut(x, y, x - px, y - py);
                        px = x;
                        py = y;
                        break;
                    }
                    case PathIterator.SEG_QUADTO: {
                        double k0x = coords[0];
                        double k0y = coords[1];
                        double x = coords[2];
                        double y = coords[3];
                        if (px != k0x || py != k0y) {
                            markerIn(px, py, k0x - px, k0y - py);
                        } else {
                            markerIn(px, py, x - px, y - py);
                        }
                        if (x != k0x || y != k0y) {
                            markerOut(x, y, x - k0x, y - k0y);
                        } else {
                            markerOut(x, y, x - px, y - py);
                        }
                        markerIn(px, py, k0x - px, k0y - py);
                        markerOut(x, y, x - k0x, y - k0y);
                        px = x;
                        py = y;
                        break;
                    }
                    case PathIterator.SEG_CUBICTO: {
                        double k0x = coords[0];
                        double k0y = coords[1];
                        double k1x = coords[2];
                        double k1y = coords[3];
                        double x = coords[4];
                        double y = coords[5];
                        if (px != k0x || py != k0y) {
                            markerIn(px, py, k0x - px, k0y - py);
                        } else if (px != k1x || py != k1y) {
                            markerIn(px, py, k1x - px, k1y - py);
                        } else {
                            markerIn(px, py, x - px, y - py);
                        }
                        if (x != k1x || y != k1y) {
                            markerOut(x, y, x - k1x, y - k1y);
                        } else if (x != k0x || y != k0y) {
                            markerOut(x, y, x - k0x, y - k0y);
                        } else {
                            markerOut(x, y, x - px, y - py);
                        }
                        px = x;
                        py = y;
                        break;
                    }
                }
            }
            for (int i = 1; i < markerList.size(); ++i) {
                MarkerPos prev = markerList.get(i - 1);
                MarkerPos cur = markerList.get(i);

                if (cur.type == MARKER_START) {
                    prev.type = MARKER_END;
                }
            }
            MarkerPos last = markerList.get(markerList.size() - 1);
            last.type = MARKER_END;
        }

        private void markerIn(double x, double y, double dx, double dy) {
            if (!started) {
                started = true;
                markerList.add(new MarkerPos(MARKER_START, x, y, dx, dy));
            }
        }

        private void markerOut(double x, double y, double dx, double dy) {
            markerList.add(new MarkerPos(MARKER_MID, x, y, dx, dy));
        }

        public List<MarkerPos> getMarkerList() {
            return markerList;
        }
    }
}
