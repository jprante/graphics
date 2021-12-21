package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class MoveTo extends PathCommand {

    private final float x;

    private final float y;

    public MoveTo(boolean isRelative, float x, float y) {
        super(isRelative);
        this.x = x;
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = isRelative() ? hist.getLastPoint().x : 0f;
        float offy = isRelative() ? hist.getLastPoint().y : 0f;
        path.moveTo(x + offx, y + offy);
        hist.setStartPoint(x + offx, y + offy);
        hist.setLastPoint(x + offx, y + offy);
        hist.setLastKnot(x + offx, y + offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 2;
    }

    @Override
    public String toString() {
        return "M " + x + " " + y;
    }
}
