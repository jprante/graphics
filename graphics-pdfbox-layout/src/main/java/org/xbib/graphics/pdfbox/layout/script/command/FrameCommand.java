package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.color.ColorFactory;
import org.xbib.graphics.pdfbox.layout.elements.Frame;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.awt.Color;
import java.io.IOException;

public class FrameCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        Frame frame = new Frame();
        //frame.setPadding(settings.getAsFloat("padding", 0f));
        if (settings.containsSetting("backgroundcolor")) {
            Color backgroundColor = ColorFactory.web(settings.get("backgroundcolor", "black"));
            frame.setBackgroundColor(backgroundColor);
        }
        if (settings.containsSetting("bordercolor")) {
            Color borderColor = ColorFactory.web(settings.get("bordercolor", "black"));
            frame.setBorderColor(borderColor);
        }
        state.elements.peek().add(frame);
    }
}
