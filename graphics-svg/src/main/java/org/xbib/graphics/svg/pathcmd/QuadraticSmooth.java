package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class QuadraticSmooth extends PathCommand {

    private final float x;

    private final float y;

    public QuadraticSmooth(boolean isRelative, float x, float y) {
        super(isRelative);
        this.x = x;
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = isRelative() ? hist.getLastPoint().x : 0f;
        float offy = isRelative() ? hist.getLastPoint().y : 0f;
        float oldKx = hist.getLastKnot().x;
        float oldKy = hist.getLastKnot().y;
        float oldX = hist.getLastPoint().x;
        float oldY = hist.getLastPoint().y;
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

    @Override
    public String toString() {
        return "T " + x + " " + y;
    }
}
