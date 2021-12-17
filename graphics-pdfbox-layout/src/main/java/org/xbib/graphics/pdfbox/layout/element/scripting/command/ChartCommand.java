package org.xbib.graphics.pdfbox.layout.element.scripting.command;

import org.xbib.graphics.pdfbox.layout.element.ChartElement;
import org.xbib.graphics.pdfbox.layout.element.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.graphics.pdfbox.layout.element.scripting.State;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static org.xbib.graphics.pdfbox.layout.util.PdfUtil.mmToPt;

public class ChartCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        ChartElement element = new ChartElement();
        if (settings.containsSetting("xdata")) {
            String[] strings = settings.getAsArray("xdata");
            element.setXData(Arrays.stream(strings).mapToDouble(Double::parseDouble).toArray());
        }
        if (settings.containsSetting("ydata")) {
            String[] strings = settings.getAsArray("ydata");
            element.setYData(Arrays.stream(strings).mapToDouble(Double::parseDouble).toArray());
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
