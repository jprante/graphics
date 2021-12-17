package org.xbib.graphics.pdfbox.layout.element.scripting.command;

import org.xbib.graphics.pdfbox.layout.color.ColorFactory;
import org.xbib.graphics.pdfbox.layout.element.Document;
import org.xbib.graphics.pdfbox.layout.font.Fonts;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.graphics.pdfbox.layout.element.scripting.State;
import org.xbib.graphics.pdfbox.layout.table.BorderStyle;
import org.xbib.graphics.pdfbox.layout.table.BorderStyleInterface;
import org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment;
import org.xbib.graphics.pdfbox.layout.table.Row;
import org.xbib.graphics.pdfbox.layout.table.VerticalAlignment;
import org.xbib.settings.Settings;

import java.awt.Color;
import java.io.IOException;
import java.util.Locale;

public class RowCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        Row.Builder row = Row.builder();
        row.padding(settings.getAsFloat("padding", 0f));
        row.fontSize(settings.getAsFloat("fontsize", 11.0f));
        Document document = state.getDocument();
        row.font(Fonts.valueOf(settings.get("font", "helvetica").toUpperCase(Locale.ROOT)).getFont(document));
        Color color = ColorFactory.web(settings.get("color", "black"));
        row.textColor(color);
        if (settings.containsSetting("backgroundcolor")) {
            Color backgroundColor = ColorFactory.web(settings.get("backgroundcolor", "black"));
            row.backgroundColor(backgroundColor);
        }
        if (settings.containsSetting("bordercolor")) {
            Color borderColor = ColorFactory.web(settings.get("bordercolor", "black"));
            row.borderColor(borderColor);
        }
        row.borderWidth(settings.getAsFloat("borderwidth", 0f));
        BorderStyleInterface styleInterface = BorderStyle.valueOf(settings.get("borderstyle", "solid").toUpperCase(Locale.ROOT));
        row.borderStyle(styleInterface);
        if (settings.containsSetting("horizontalalignment")) {
            row.horizontalAlignment(HorizontalAlignment.valueOf(settings.get("horizontalalignment", "left").toUpperCase(Locale.ROOT)));
        }
        if (settings.containsSetting("verticalalignment")) {
            row.verticalAlignment(VerticalAlignment.valueOf(settings.get("verticalalignment", "left").toUpperCase(Locale.ROOT)));
        }
        state.elements.push(row);
        engine.executeElements(settings);
        state.elements.pop();
        state.elements.peek().add(row);
    }
}
