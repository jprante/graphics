package org.xbib.graphics.pdfbox.layout.element.scripting.command;

import org.xbib.graphics.pdfbox.layout.element.TableElement;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.graphics.pdfbox.layout.element.scripting.State;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;

import static org.xbib.graphics.pdfbox.layout.util.PdfUtil.mmToPt;

public class TableCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        TableElement tableElement = new TableElement();
        if (settings.containsSetting("columnwidths")) {
            String columnwidths = settings.get("columnwidths");
            String[] widths = columnwidths.split(" ");
            for (String width : widths) {
                tableElement.addColumnOfWidth(mmToPt(Float.parseFloat(width)));
            }
        }

        if (settings.containsSetting("x") && settings.containsSetting("y")) {
            tableElement.setAbsolutePosition(new Position(mmToPt(settings.getAsFloat("x", 0f)), mmToPt(settings.getAsFloat("y", 0f))));
        }
        state.elements.push(tableElement);
        engine.executeElements(settings);
        state.elements.pop();
        state.elements.peek().add(tableElement);
    }
}
