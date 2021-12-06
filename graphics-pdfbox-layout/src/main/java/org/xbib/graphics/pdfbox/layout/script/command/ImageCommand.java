package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.ImageElement;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;

public class ImageCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        ImageElement element = new ImageElement(settings.get("value"));
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
        state.document.add(element, new VerticalLayoutHint(Alignment.LEFT, 10, 10, 10, 10, true));
    }
}
