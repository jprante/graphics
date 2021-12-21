package org.xbib.graphics.svg.pathcmd;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

public class Arc extends PathCommand {

    private float rx = 0f;

    private float ry = 0f;

    private float xAxisRot = 0f;

    private boolean largeArc = false;

    private boolean sweep = false;

    private float x = 0f;

    private float y = 0f;

    public Arc() {
    }

    public Arc(boolean isRelative, float rx, float ry, float xAxisRot, boolean largeArc, boolean sweep, float x, float y) {
        super(isRelative);
        this.rx = rx;
        this.ry = ry;
        this.xAxisRot = xAxisRot;
        this.largeArc = largeArc;
        this.sweep = sweep;
        this.x = x;
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = isRelative() ? hist.getLastPoint().x : 0f;
        float offy = isRelative() ? hist.getLastPoint().y : 0f;
        arcTo(path, rx, ry, xAxisRot, largeArc, sweep,
                x + offx, y + offy,
                hist.getLastPoint().x, hist.getLastPoint().y);
        hist.setLastPoint(x + offx, y + offy);
        hist.setLastKnot(x + offx, y + offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 6;
    }

    public void arcTo(GeneralPath path, float rx, float ry,
                      float angle,
                      boolean largeArcFlag,
                      boolean sweepFlag,
                      float x, float y, float x0, float y0) {
        if (rx == 0 || ry == 0) {
            path.lineTo(x, y);
            return;
        }
        if (x0 == x && y0 == y) {
            return;
        }
        Arc2D arc = computeArc(x0, y0, rx, ry, angle, largeArcFlag, sweepFlag, x, y);
        AffineTransform t = AffineTransform.getRotateInstance
                (Math.toRadians(angle), arc.getCenterX(), arc.getCenterY());
        Shape s = t.createTransformedShape(arc);
        path.append(s, true);
    }

    public static Arc2D computeArc(double x0, double y0,
                                   double rx, double ry,
                                   double angle,
                                   boolean largeArcFlag,
                                   boolean sweepFlag,
                                   double x, double y) {
        double dx2 = (x0 - x) / 2.0;
        double dy2 = (y0 - y) / 2.0;
        angle = Math.toRadians(angle % 360.0);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        double x1 = (cosAngle * dx2 + sinAngle * dy2);
        double y1 = (-sinAngle * dx2 + cosAngle * dy2);
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double Prx = rx * rx;
        double Pry = ry * ry;
        double Px1 = x1 * x1;
        double Py1 = y1 * y1;
        double radiiCheck = Px1 / Prx + Py1 / Pry;
        if (radiiCheck > 1) {
            rx = Math.sqrt(radiiCheck) * rx;
            ry = Math.sqrt(radiiCheck) * ry;
            Prx = rx * rx;
            Pry = ry * ry;
        }
        double sign = (largeArcFlag == sweepFlag) ? -1 : 1;
        double sq = ((Prx * Pry) - (Prx * Py1) - (Pry * Px1)) / ((Prx * Py1) + (Pry * Px1));
        sq = (sq < 0) ? 0 : sq;
        double coef = (sign * Math.sqrt(sq));
        double cx1 = coef * ((rx * y1) / ry);
        double cy1 = coef * -((ry * x1) / rx);
        double sx2 = (x0 + x) / 2.0;
        double sy2 = (y0 + y) / 2.0;
        double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
        double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);
        double ux = (x1 - cx1) / rx;
        double uy = (y1 - cy1) / ry;
        double vx = (-x1 - cx1) / rx;
        double vy = (-y1 - cy1) / ry;
        double p, n;
        n = Math.sqrt((ux * ux) + (uy * uy));
        p = ux;
        sign = (uy < 0) ? -1d : 1d;
        double angleStart = Math.toDegrees(sign * Math.acos(p / n));
        n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
        p = ux * vx + uy * vy;
        sign = (ux * vy - uy * vx < 0) ? -1d : 1d;
        double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
        if (!sweepFlag && angleExtent > 0) {
            angleExtent -= 360f;
        } else if (sweepFlag && angleExtent < 0) {
            angleExtent += 360f;
        }
        angleExtent %= 360f;
        angleStart %= 360f;
        Arc2D.Double arc = new Arc2D.Double();
        arc.x = cx - rx;
        arc.y = cy - ry;
        arc.width = rx * 2.0;
        arc.height = ry * 2.0;
        arc.start = -angleStart;
        arc.extent = -angleExtent;
        return arc;
    }

    @Override
    public String toString() {
        return "A " + rx + " " + ry
                + " " + xAxisRot + " " + largeArc
                + " " + sweep
                + " " + x + " " + y;
    }
}
