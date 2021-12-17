package org.xbib.graphics.svg;

public class MarkerPos {

    private int type;

    double x;

    double y;

    double dx;

    double dy;

    public MarkerPos(int type, double x, double y, double dx, double dy) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
