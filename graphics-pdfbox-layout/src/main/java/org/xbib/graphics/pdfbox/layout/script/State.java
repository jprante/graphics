package org.xbib.graphics.pdfbox.layout.script;

import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;

import java.util.Collection;
import java.util.Stack;

public class State {

    public Stack<Document> documents = new Stack<>();

    public Stack<Paragraph> paragraphs = new Stack<>();

    public Collection<Document> getDocuments() {
        return documents;
    }
}
