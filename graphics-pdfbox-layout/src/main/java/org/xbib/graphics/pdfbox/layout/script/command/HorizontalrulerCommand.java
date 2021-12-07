package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.color.ColorFactory;
import org.xbib.graphics.pdfbox.layout.elements.HorizontalRuler;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.shape.Stroke;
import org.xbib.settings.Settings;

import java.awt.Color;
import java.io.IOException;
import java.util.Locale;

public class HorizontalrulerCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        Stroke.StrokeBuilder strokeBuilder = Stroke.builder()
                .capStyle(Stroke.CapStyle.valueOf(settings.get("capstyie", "cap").toUpperCase(Locale.ROOT)))
                .joinStyle(Stroke.JoinStyle.valueOf(settings.get("joinstyle", "miter").toUpperCase(Locale.ROOT)))
                .lineWidth(settings.getAsFloat("linewidth", 1f));
        if (settings.containsSetting("dash")) {
            strokeBuilder.dashPattern(new Stroke.DashPattern(settings.getAsFloat("dash", 1f)));
        }
        Color color = ColorFactory.web(settings.get("color", "black"));
        HorizontalRuler horizontalRuler = new HorizontalRuler(strokeBuilder.build(), color);
        state.elements.peek().add(horizontalRuler);
    }
}
