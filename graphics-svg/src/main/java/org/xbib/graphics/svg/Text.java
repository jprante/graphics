package org.xbib.graphics.svg;

import org.xbib.graphics.svg.util.FontUtil;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Text extends Tspan {

    public static final String TAG_NAME = "text";

    public static final int TXAN_START = 0;

    public static final int TXAN_MIDDLE = 1;

    public static final int TXAN_END = 2;

    public static final int TXST_NORMAL = 0;

    public static final int TXST_ITALIC = 1;

    public static final int TXST_OBLIQUE = 2;

    public static final int TXWE_NORMAL = 0;

    public static final int TXWE_BOLD = 1;

    public static final int TXWE_BOLDER = 2;

    public static final int TXWE_LIGHTER = 3;

    public static final int TXWE_100 = 4;

    public static final int TXWE_200 = 5;

    public static final int TXWE_300 = 6;

    public static final int TXWE_400 = 7;

    public static final int TXWE_500 = 8;

    public static final int TXWE_600 = 9;

    public static final int TXWE_700 = 10;

    public static final int TXWE_800 = 11;

    public static final int TXWE_900 = 12;

    int textAnchor = TXAN_START;

    public Text() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    public void rebuild() throws SVGException, IOException {
        build();
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        buildText();
    }

    protected void buildAttributes(StyleAttribute sty) throws SVGException {
        super.buildAttributes(sty);
        if (getStyle(sty.setName("text-anchor"))) {
            String s = sty.getStringValue();
            if (s.equals("middle")) {
                textAnchor = TXAN_MIDDLE;
            } else if (s.equals("end")) {
                textAnchor = TXAN_END;
            } else {
                textAnchor = TXAN_START;
            }
        } else {
            textAnchor = TXAN_START;
        }
    }

    private void buildText() throws SVGException {
        Cursor cursor = createInitialCursor();
        float xInitial = cursor.x;
        super.buildTextShape(cursor);
        alignSegmentsAtAnchor(fullPath, xInitial);
    }

    private void alignSegmentsAtAnchor(Path2D textPath, float xInitial) {
        AffineTransform tx;
        Rectangle2D bounds;
        switch (textAnchor) {
            case TXAN_MIDDLE:
                bounds = textPath.getBounds2D();
                tx = AffineTransform.getTranslateInstance(
                        -(bounds.getX() + bounds.getWidth() / 2.0 - xInitial), 0
                );
                break;
            case TXAN_END:
                bounds = textPath.getBounds2D();
                tx = AffineTransform.getTranslateInstance(
                        -(bounds.getX() + bounds.getWidth() - xInitial), 0
                );
                break;
            default:
                tx = null;
                break;
        }
        if (tx != null) {
            fullPath.transform(tx);
            textBounds = fullPath.getBounds2D();
            transformSegments(segments, tx);
        }
    }

    private void transformSegments(List<TextSegment> segments, AffineTransform transform) {
        for (TextSegment segment : segments) {
            if (segment.textPath != null) {
                segment.textPath.transform(transform);
            } else {
                segment.element.fullPath.transform(transform);
                segment.element.textBounds = segment.element.fullPath.getBounds2D();
                transformSegments(segment.element.segments, transform);
            }
        }
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        boolean changeState = super.updateTime(curTime);
        FontUtil.FontInfo fontInfoOld = fontInfo;
        float[] xOld = x;
        float[] yOld = y;
        float[] dxOld = dx;
        float[] dyOld = dy;
        buildShapeInformation();
        boolean shapeChange = !fontInfo.equals(fontInfoOld)
                || !Arrays.equals(xOld, x)
                || !Arrays.equals(yOld, y)
                || !Arrays.equals(dxOld, dx)
                || !Arrays.equals(dyOld, dy);
        if (shapeChange) {
            buildText();
        }
        return changeState || shapeChange;
    }
}
