package org.xbib.graphics.pdfbox.layout.table;

public class Column {

    private Table table;

    private Column next;

    private float width;

    Column(final float width) {
        if (width < 0) {
            throw new IllegalArgumentException("Column width must be non-negative");
        }
        this.width = width;
    }

    boolean hasNext() {
        return next != null;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setNext(Column next) {
        this.next = next;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public Table getTable() {
        return table;
    }

    public float getWidth() {
        return width;
    }

    public Column getNext() {
        return next;
    }
}
