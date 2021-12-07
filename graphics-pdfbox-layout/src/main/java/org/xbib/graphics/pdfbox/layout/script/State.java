package org.xbib.graphics.pdfbox.layout.script;

import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.TableElement;
import org.xbib.graphics.pdfbox.layout.table.Row;

import java.util.Collection;
import java.util.Stack;

public class State {

    public Stack<Document> documents = new Stack<>();

    public Stack<Paragraph> paragraphs = new Stack<>();

    public Stack<TableElement> tables = new Stack<>();

    public Stack<Row.Builder> rows = new Stack<>();

    public Collection<Document> getDocuments() {
        return documents;
    }
}
