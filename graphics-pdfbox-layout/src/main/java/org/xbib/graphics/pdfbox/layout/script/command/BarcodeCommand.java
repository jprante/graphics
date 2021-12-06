package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.barcode.HumanReadableLocation;
import org.xbib.graphics.barcode.Symbol;
import org.xbib.graphics.barcode.Symbols;
import org.xbib.graphics.pdfbox.layout.elements.BarcodeElement;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;

public class BarcodeCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        BarcodeElement element;
        try {
            Symbol symbol = Symbols.valueOf(settings.get("symbol")).getSymbol();
            symbol.setContent(settings.get("value"));
            symbol.setBarHeight(settings.getAsInt("barheight", 150));
            symbol.setHumanReadableLocation(HumanReadableLocation.valueOf(settings.get("readablelocation", "BOTTOM")));
            element = new BarcodeElement(symbol);
        } catch (Exception e) {
            throw new IOException(e);
        }
        if (settings.containsSetting("x") && settings.containsSetting("y")) {
            element.setAbsolutePosition(new Position(settings.getAsFloat("x", 0f), settings.getAsFloat("y", 0f)));
        }
        if (settings.containsSetting("width")) {
            element.setWidth(settings.getAsFloat("width", element.getWidth()));
        }
        if (settings.containsSetting("height")) {
            element.setWidth(settings.getAsFloat("height", element.getHeight()));
        }
        if (settings.containsSetting("scale")) {
            element.setScale(settings.getAsFloat("scale", element.getScale()));
        }
        state.document.add(element);
    }
}
