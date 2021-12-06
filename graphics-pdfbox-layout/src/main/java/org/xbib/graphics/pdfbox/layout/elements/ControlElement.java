package org.xbib.graphics.pdfbox.layout.elements;

/**
 * ControlElements do not have a drawable representation, but control page flow,
 * rendering etc.
 */
public class ControlElement implements Element {

    /**
     * Triggers a new page in a document.
     */
    public final static ControlElement NEWPAGE = new ControlElement("NEWPAGE");

    /**
     * Triggers flip to the next column.
     */
    public final static ControlElement NEWCOLUMN = new ControlElement("NEWCOLUMN");

    private final String name;

    public ControlElement(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ControlElement [NEWPAGE=" + NEWPAGE + ", name=" + name + "]";
    }

}
