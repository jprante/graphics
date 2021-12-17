package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.Font;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.element.ShapeElement;
import org.xbib.graphics.svg.element.glyph.MissingGlyph;
import org.xbib.graphics.svg.util.FontUtil;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tspan extends ShapeElement {

    public static final String TAG_NAME = "tspan";

    float[] x = null;

    float[] y = null;

    float[] dx = null;

    float[] dy = null;

    float[] rotate = null;

    float textLength = -1;

    String lengthAdjust = "spacing";

    private final List<Object> content = new ArrayList<>();

    protected final ArrayList<TextSegment> segments = new ArrayList<>();

    protected Rectangle2D textBounds;

    protected Path2D fullPath;

    protected FontUtil.FontInfo fontInfo;

    private Font font;

    public Tspan() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
        super.loaderAddChild(helper, child);
        content.add(child);
    }

    @Override
    public void loaderAddText(SVGLoaderHelper helper, String text) {
        Matcher matchWs = Pattern.compile("\\s*").matcher(text);
        if (!matchWs.matches()) {
            content.add(text);
        }
    }

    public List<Object> getContent() {
        return content;
    }

    public void clearContent() {
        content.clear();
    }

    public void appendText(String text) {
        content.add(text);
    }

    public void appendTspan(Tspan tspan) throws SVGElementException {
        super.loaderAddChild(null, tspan);
        content.add(tspan);
    }

    protected void buildAttributes(StyleAttribute sty) throws SVGException {
        if (getPres(sty.setName("x"))) {
            x = sty.getFloatListWithUnits();
        }
        if (getPres(sty.setName("y"))) {
            y = sty.getFloatListWithUnits();
        }
        if (getPres(sty.setName("dx"))) {
            dx = sty.getFloatListWithUnits();
        }
        if (getPres(sty.setName("dy"))) {
            dy = sty.getFloatListWithUnits();
        }
        if (getPres(sty.setName("rotate"))) {
            rotate = sty.getFloatList();
            for (int i = 0; i < this.rotate.length; i++) {
                rotate[i] = (float) Math.toRadians(this.rotate[i]);
            }
        }
        if (getStyle(sty.setName("textLength"))) {
            textLength = sty.getFloatValueWithUnits();
        } else {
            textLength = -1;
        }
        if (getStyle(sty.setName("lengthAdjust"))) {
            lengthAdjust = sty.getStringValue();
        } else {
            lengthAdjust = "spacing";
        }
    }

    protected void buildShapeInformation() throws SVGException {
        StyleAttribute sty = new StyleAttribute();
        buildAttributes(sty);
        fontInfo = FontUtil.parseFontInfo(this, sty);
        font = FontUtil.getFont(fontInfo, diagram);
    }

    protected void buildTextShape(Cursor cursor) throws SVGException {
        buildShapeInformation();
        fullPath = new GeneralPath();
        segments.clear();
        segments.ensureCapacity(content.size());
        int currentCursorOffset = cursor.offset;
        cursor.offset = 0;
        AffineTransform transform = new AffineTransform();
        float spaceAdvance = font.getGlyph(" ").getHorizAdvX();
        for (Object obj : content) {
            if (obj instanceof String) {
                String text = ((String) obj);
                String trimmed = text.trim();
                if (!text.isEmpty() && text.charAt(0) <= ' ')
                    cursor.x += font.getGlyph(" ").getHorizAdvX();
                Path2D textPath = createStringSegmentPath(trimmed, font, cursor, transform);
                if (!text.isEmpty() && text.charAt(text.length() - 1) <= ' ')
                    cursor.x += spaceAdvance;

                fullPath.append(textPath, false);
                segments.add(new TextSegment(textPath, this));
            } else if (obj instanceof Tspan) {
                Tspan tspan = (Tspan) obj;
                tspan.buildTextShape(cursor);
                fullPath.append(tspan.fullPath, false);
                segments.add(new TextSegment(null, (Tspan) obj));
            }
        }
        textBounds = fullPath.getBounds2D();
        cursor.offset += currentCursorOffset;
    }

    private Path2D createStringSegmentPath(String text, Font font, Cursor cursor, AffineTransform xform) {
        Path2D textPath = new GeneralPath();
        for (int i = 0; i < text.length(); i++) {
            cursor.x = getXCursorForIndex(cursor.x, cursor.offset + i);
            cursor.y = getYCursorForIndex(cursor.y, cursor.offset + i);
            xform.setToTranslation(cursor.x, cursor.y);
            if (rotate != null && cursor.offset + i < rotate.length) {
                xform.rotate(rotate[cursor.offset + i]);
            }
            String unicode = text.substring(i, i + 1);
            MissingGlyph glyph = font.getGlyph(unicode);
            Shape path = glyph.getPath();
            if (path != null) {
                path = xform.createTransformedShape(path);
                textPath.append(path, false);
            }
            cursor.x += glyph.getHorizAdvX() + fontInfo.letterSpacing;
        }
        cursor.offset += text.length();
        strokeWidthScalar = 1f;
        return textPath;
    }

    protected Cursor createInitialCursor() {
        return new Cursor(getXCursorForIndex(0, 0),
                getYCursorForIndex(0, 0));
    }

    private float getXCursorForIndex(float current, int index) {
        return getCursorForIndex(current, index, x, dx);
    }

    private float getYCursorForIndex(float current, int index) {
        return getCursorForIndex(current, index, y, dy);
    }

    private float getCursorForIndex(float current, int index, float[] absolutes, float[] deltas) {
        if (absolutes != null && index < absolutes.length) {
            current = absolutes[index];
        } else if (deltas != null && index < deltas.length) {
            current += deltas[index];
        }
        return current;
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        beginLayer(g);
        for (TextSegment segment : segments) {
            if (segment.textPath != null) {
                segment.element.renderShape(g, segment.textPath);
            } else {
                segment.element.doRender(g);
            }
        }
        finishLayer(g);
    }

    @Override
    public Shape getShape() {
        return shapeToParent(fullPath);
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return boundsToParent(textBounds);
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        return false;
    }

    public String getText() {
        return getText(new StringBuilder());
    }

    private String getText(StringBuilder builder) {
        for (Object object : content) {
            if (object instanceof Tspan) {
                ((Tspan) object).getText(builder);
                builder.append(' ');
            } else if (object != null) {
                builder.append(object).append(' ');
            }
        }
        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - 1);
        } else {
            return "";
        }
    }

    protected static class TextSegment {

        final Path2D textPath;

        final Tspan element;

        private TextSegment(Path2D textPath, Tspan element) {
            this.textPath = textPath;
            this.element = element;
        }
    }

    protected static class Cursor {

        float x;

        float y;

        int offset;

        public Cursor(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
