package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

abstract public class PathCommand {

    private final boolean isRelative;

    public PathCommand() {
        this(true);
    }

    public PathCommand(boolean isRelative) {
        this.isRelative = isRelative;
    }

    public boolean isRelative() {
        return isRelative;
    }

    abstract public void appendPath(GeneralPath path, BuildHistory hist);

    abstract public int getNumKnotsAdded();
}
