package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.TableElement;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class TableCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        state.rows.clear();
        TableElement tableElement = new TableElement();
        if (settings.containsSetting("columnwidths")) {
            String columnWidths = settings.get("columnwidths");
            for (String columnWidth : columnWidths.split(" ")) {
                tableElement.addColumnOfWidth(Float.parseFloat(columnWidth));
            }
        }
        state.documents.peek().add(tableElement);
        state.tables.push(tableElement);
        engine.executeElements(settings);
    }
}