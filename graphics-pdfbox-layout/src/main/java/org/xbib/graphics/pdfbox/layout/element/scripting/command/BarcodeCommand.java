package org.xbib.graphics.pdfbox.layout.element.scripting.command;

import org.xbib.graphics.barcode.HumanReadableLocation;
import org.xbib.graphics.barcode.Symbol;
import org.xbib.graphics.barcode.Symbols;
import org.xbib.graphics.pdfbox.layout.element.BarcodeElement;
import org.xbib.graphics.pdfbox.layout.element.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.graphics.pdfbox.layout.element.scripting.State;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.util.Locale;

import static org.xbib.graphics.pdfbox.layout.util.PdfUtil.mmToPt;

public class BarcodeCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        BarcodeElement element;
        try {
            Symbol symbol = Symbols.valueOf(settings.get("symbol")).getSymbol();
            symbol.setContent(settings.get("value"));
            symbol.setBarHeight(settings.getAsInt("barheight", 150));
            symbol.setHumanReadableLocation(HumanReadableLocation.valueOf(settings.get("readablelocation", "bottom").toUpperCase(Locale.ROOT)));
            element = new BarcodeElement(symbol);
        } catch (Exception e) {
            throw new IOException(e);
        }
        if (settings.containsSetting("x") && settings.containsSetting("y")) {
            element.setAbsolutePosition(new Position(mmToPt(settings.getAsFloat("x", 0f)), mmToPt(settings.getAsFloat("y", 0f))));
        }
        if (settings.containsSetting("width")) {
            element.setWidth(settings.getAsFloat("width", element.getWidth()));
        }
        if (settings.containsSetting("height")) {
            element.setHeight(settings.getAsFloat("height", element.getHeight()));
        }
        if (settings.containsSetting("scalex")) {
            element.setScaleX(settings.getAsFloat("scalex", element.getScaleX()));
        }
        if (settings.containsSetting("scaley")) {
            element.setScaleY(settings.getAsFloat("scaley", element.getScaleY()));
        }
        Alignment alignment = Alignment.valueOf(settings.get("alignment", "left").toUpperCase(Locale.ROOT));
        String margin = settings.get("margin", "0 0 0 0");
        String[] margins = margin.split(" ");
        float marginleft = Float.parseFloat(margins[0]);
        float marginright = Float.parseFloat(margins[1]);
        float margintop = Float.parseFloat(margins[2]);
        float marginbottom = Float.parseFloat(margins[3]);
        VerticalLayoutHint verticalLayoutHint = new VerticalLayoutHint(alignment, marginleft, marginright, margintop, marginbottom, true);
        state.elements.peek().add(element, verticalLayoutHint);
    }
}
