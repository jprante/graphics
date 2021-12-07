package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.color.ColorFactory;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.Fonts;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.table.BorderStyle;
import org.xbib.graphics.pdfbox.layout.table.BorderStyleInterface;
import org.xbib.graphics.pdfbox.layout.table.TextCell;
import org.xbib.settings.Settings;

import java.awt.Color;
import java.io.IOException;
import java.util.Locale;

public class CellCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        TextCell.Builder cell = TextCell.builder();
        cell.text(settings.get("value"));
        cell.fontSize(settings.getAsFloat("size", 11.0f));
        Font font = Fonts.valueOf(settings.get("font", "helvetica").toUpperCase(Locale.ROOT)).getFont(state.documents.peek());
        cell.font(font);
        cell.padding(settings.getAsFloat("padding", 0f));
        Color color = ColorFactory.web(settings.get("color", "black"));
        cell.textColor(color);
        if (settings.containsSetting("backgroundcolor")) {
            Color backgroundColor = ColorFactory.web(settings.get("backgroundcolor", "black"));
            cell.backgroundColor(backgroundColor);
        }
        if (settings.containsSetting("bordercolor")) {
            Color borderColor = ColorFactory.web(settings.get("bordercolor", "black"));
            cell.borderColor(borderColor);
        }
        cell.borderWidth(settings.getAsFloat("borderwidth", 0f));
        BorderStyleInterface styleInterface = BorderStyle.valueOf(settings.get("borderstyle", "solid").toUpperCase(Locale.ROOT));
        cell.borderStyle(styleInterface);
        cell.colSpan(settings.getAsInt("colspan", 1));
        cell.rowSpan(settings.getAsInt("rowspan", 1));
        state.rows.peek().add(cell.build());
    }
}
