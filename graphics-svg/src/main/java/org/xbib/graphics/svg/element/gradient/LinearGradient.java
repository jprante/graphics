package org.xbib.graphics.svg.element.gradient;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.element.gradient.Gradient;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class LinearGradient extends Gradient {

    public static final String TAG_NAME = "lineargradient";

    float x1 = 0f;

    float y1 = 0f;

    float x2 = 1f;

    float y2 = 0f;

    public LinearGradient() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("x1"))) {
            x1 = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("y1"))) {
            y1 = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("x2"))) {
            x2 = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("y2"))) {
            y2 = sty.getFloatValueWithUnits();
        }
    }

    @Override
    public Paint getPaint(Rectangle2D bounds, AffineTransform xform) {
        MultipleGradientPaint.CycleMethod method;
        switch (spreadMethod) {
            default:
            case SM_PAD:
                method = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                break;
            case SM_REPEAT:
                method = MultipleGradientPaint.CycleMethod.REPEAT;
                break;
            case SM_REFLECT:
                method = MultipleGradientPaint.CycleMethod.REFLECT;
                break;
        }
        Paint paint;
        Point2D.Float pt1 = new Point2D.Float(x1, y1);
        Point2D.Float pt2 = new Point2D.Float(x2, y2);
        if (pt1.equals(pt2)) {
            Color[] colors = getStopColors();
            paint = colors.length > 0 ? colors[0] : Color.black;
        } else if (gradientUnits == GU_USER_SPACE_ON_USE) {
            paint = new LinearGradientPaint(
                    pt1,
                    pt2,
                    getStopFractions(),
                    getStopColors(),
                    method,
                    MultipleGradientPaint.ColorSpaceType.SRGB,
                    gradientTransform == null
                            ? new AffineTransform()
                            : gradientTransform);
        } else {
            AffineTransform viewXform = new AffineTransform();
            viewXform.translate(bounds.getX(), bounds.getY());
            double width = Math.max(1, bounds.getWidth());
            double height = Math.max(1, bounds.getHeight());
            viewXform.scale(width, height);
            if (gradientTransform != null) {
                viewXform.concatenate(gradientTransform);
            }
            paint = new LinearGradientPaint(
                    pt1,
                    pt2,
                    getStopFractions(),
                    getStopColors(),
                    method,
                    MultipleGradientPaint.ColorSpaceType.SRGB,
                    viewXform);
        }
        return paint;
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        boolean changeState = super.updateTime(curTime);
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        if (getPres(sty.setName("x1"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x1) {
                x1 = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("y1"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y1) {
                y1 = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("x2"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x2) {
                x2 = newVal;
                shapeChange = true;
            }
        }
        if (getPres(sty.setName("y2"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y2) {
                y2 = newVal;
                shapeChange = true;
            }
        }
        return changeState || shapeChange;
    }
}
