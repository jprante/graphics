package org.xbib.graphics.pdfbox.layout.element.scripting;

import org.xbib.graphics.pdfbox.layout.element.Document;
import org.xbib.graphics.pdfbox.layout.element.Element;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class State {

    public Stack<Element> elements = new Stack<>();

    public Document getDocument() {
        List<Document> list = getDocuments();
        int size = list.size();
        return size > 0 ? list.get(size - 1) : null;
    }

    public List<Document> getDocuments() {
        return elements.stream()
                .filter(e -> e instanceof Document)
                .map(e -> (Document) e)
                .collect(Collectors.toList());
    }
}
