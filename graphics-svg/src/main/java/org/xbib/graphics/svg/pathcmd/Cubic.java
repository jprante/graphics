package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class Cubic extends PathCommand {

    public float k1x = 0f;
    public float k1y = 0f;
    public float k2x = 0f;
    public float k2y = 0f;
    public float x = 0f;
    public float y = 0f;

    public Cubic() {
    }

    public Cubic(boolean isRelative, float k1x, float k1y, float k2x, float k2y, float x, float y) {
        super(isRelative);
        this.k1x = k1x;
        this.k1y = k1y;
        this.k2x = k2x;
        this.k2y = k2y;
        this.x = x;
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = isRelative ? hist.lastPoint.x : 0f;
        float offy = isRelative ? hist.lastPoint.y : 0f;
        path.curveTo(k1x + offx, k1y + offy,
                k2x + offx, k2y + offy,
                x + offx, y + offy);
        hist.setLastPoint(x + offx, y + offy);
        hist.setLastKnot(k2x + offx, k2y + offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 6;
    }

    @Override
    public String toString() {
        return "C " + k1x + " " + k1y
                + " " + k2x + " " + k2y
                + " " + x + " " + y;
    }
}
