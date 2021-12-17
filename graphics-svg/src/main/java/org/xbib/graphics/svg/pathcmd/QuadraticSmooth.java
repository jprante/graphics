package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class QuadraticSmooth extends PathCommand {

    public float x = 0f;

    public float y = 0f;

    public QuadraticSmooth() {
    }

    @Override
    public String toString() {
        return "T " + x + " " + y;
    }

    public QuadraticSmooth(boolean isRelative, float x, float y) {
        super(isRelative);
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
        float kx = oldX * 2f - oldKx;
        float ky = oldY * 2f - oldKy;
        path.quadTo(kx, ky, x + offx, y + offy);
        hist.setLastPoint(x + offx, y + offy);
        hist.setLastKnot(kx, ky);
    }

    @Override
    public int getNumKnotsAdded() {
        return 4;
    }
}
