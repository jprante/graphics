package org.xbib.graphics.pdfbox.layout.element.render;

public enum Layouts {
    VERTICAL,
    COLUMN;

    public Layout getLayout() {
        switch (name()) {
            case "VERTICAL": return new VerticalLayout();
            case "COLUMN": return new ColumnLayout();
        }
        throw new IllegalArgumentException();
    }
}
