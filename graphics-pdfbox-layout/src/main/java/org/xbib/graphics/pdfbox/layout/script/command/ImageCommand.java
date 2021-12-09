package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.ImageElement;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.util.Locale;

import static org.xbib.graphics.pdfbox.layout.util.PdfUtil.mmToPt;

public class ImageCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        ImageElement imageElement = new ImageElement(settings.get("value"));
        if (settings.containsSetting("x") && settings.containsSetting("y")) {
            imageElement.setAbsolutePosition(new Position(mmToPt(settings.getAsFloat("x", 0f)), mmToPt(settings.getAsFloat("y", 0f))));
        }
        if (settings.containsSetting("width")) {
            imageElement.setWidth(settings.getAsFloat("width", imageElement.getWidth()));
        }
        if (settings.containsSetting("height")) {
            imageElement.setHeight(settings.getAsFloat("height", imageElement.getHeight()));
        }
        if (settings.containsSetting("scale")) {
            imageElement.setScale(settings.getAsFloat("scale", imageElement.getScale()));
        }
        Alignment alignment = Alignment.valueOf(settings.get("alignment", "left").toUpperCase(Locale.ROOT));
        String margin = settings.get("margin", "0 0 0 0");
        String[] margins = margin.split(" ");
        float marginleft = Float.parseFloat(margins[0]);
        float marginright = Float.parseFloat(margins[1]);
        float margintop = Float.parseFloat(margins[2]);
        float marginbottom = Float.parseFloat(margins[3]);
        VerticalLayoutHint verticalLayoutHint = new VerticalLayoutHint(alignment, marginleft, marginright, margintop, marginbottom, true);
        state.elements.peek().add(imageElement, verticalLayoutHint);
    }
}
