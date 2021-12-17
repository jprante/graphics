package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class CubicSmooth extends PathCommand {

    public float x = 0f;

    public float y = 0f;

    public float k2x = 0f;

    public float k2y = 0f;

    public CubicSmooth() {
    }

    public CubicSmooth(boolean isRelative, float k2x, float k2y, float x, float y) {
        super(isRelative);
        this.k2x = k2x;
        this.k2y = k2y;
        this.x = x;
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = isRelative ? hist.lastPoint.x : 0f;
        float offy = isRelative ? hist.lastPoint.y : 0f;
        float oldKx = hist.lastKnot.x;
        float oldKy = hist.lastKnot.y;
        float oldX = hist.lastPoint.x;
        float oldY = hist.lastPoint.y;
        float k1x = oldX * 2f - oldKx;
        float k1y = oldY * 2f - oldKy;
        path.curveTo(k1x, k1y, k2x + offx, k2y + offy, x + offx, y + offy);
        hist.setLastPoint(x + offx, y + offy);
        hist.setLastKnot(k2x + offx, k2y + offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 6;
    }

    @Override
    public String toString() {
        return "S " + k2x + " " + k2y
                + " " + x + " " + y;
    }
}