package org.xbib.graphics.svg.element.gradient;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Color;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Radial extends Gradient {

    private float cx = 0.5f;

    private float cy = 0.5f;

    private boolean hasFocus = false;

    private float fx = 0f;

    private float fy = 0f;

    private float r = 0.5f;

    @Override
    public String getTagName() {
        return "radialgradient";
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("cx"))) {
            cx = sty.getFloatValueWithUnits();
        }
        if (getPres(sty.setName("cy"))) {
            cy = sty.getFloatValueWithUnits();
        }
        hasFocus = false;
        if (getPres(sty.setName("fx"))) {
            fx = sty.getFloatValueWithUnits();
            hasFocus = true;
        }
        if (getPres(sty.setName("fy"))) {
            fy = sty.getFloatValueWithUnits();
            hasFocus = true;
        }
        if (getPres(sty.setName("r"))) {
            r = sty.getFloatValueWithUnits();
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
        Point2D.Float pt1 = new Point2D.Float(cx, cy);
        Point2D.Float pt2 = hasFocus ? new Point2D.Float(fx, fy) : pt1;
        float[] stopFractions = getStopFractions();
        Color[] stopColors = getStopColors();
        if (gradientUnits == GU_USER_SPACE_ON_USE) {
            paint = new RadialGradientPaint(
                    pt1,
                    r,
                    pt2,
                    stopFractions,
                    stopColors,
                    method,
                    MultipleGradientPaint.ColorSpaceType.SRGB,
                    gradientTransform);
        } else {
            AffineTransform viewXform = new AffineTransform();
            viewXform.translate(bounds.getX(), bounds.getY());
            viewXform.scale(bounds.getWidth(), bounds.getHeight());
            viewXform.concatenate(gradientTransform);
            paint = new RadialGradientPaint(
                    pt1,
                    r,
                    pt2,
                    stopFractions,
                    stopColors,
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
        if (getPres(sty.setName("cx"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != cx) {
                cx = newVal;
            }
        }
        if (getPres(sty.setName("cy"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != cy) {
                cy = newVal;
            }
        }
        if (getPres(sty.setName("fx"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != fx) {
                fx = newVal;
            }
        }
        if (getPres(sty.setName("fy"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != fy) {
                fy = newVal;
            }
        }
        if (getPres(sty.setName("r"))) {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != r) {
                r = newVal;
            }
        }
        return changeState;
    }
}
