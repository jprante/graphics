package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

abstract public class PathCommand {

    public boolean isRelative = false;

    public PathCommand() {
    }

    public PathCommand(boolean isRelative) {
        this.isRelative = isRelative;
    }

    abstract public void appendPath(GeneralPath path, BuildHistory hist);

    abstract public int getNumKnotsAdded();
}
