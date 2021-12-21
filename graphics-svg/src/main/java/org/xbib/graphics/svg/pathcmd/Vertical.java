package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class Vertical extends PathCommand {

    private final float y;

    public Vertical(boolean isRelative, float y) {
        super(isRelative);
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = hist.getLastPoint().x;
        float offy = isRelative() ? hist.getLastPoint().y : 0f;
        path.lineTo(offx, y + offy);
        hist.setLastPoint(offx, y + offy);
        hist.setLastKnot(offx, y + offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 2;
    }

    @Override
    public String toString() {
        return "V " + y;
    }
}
