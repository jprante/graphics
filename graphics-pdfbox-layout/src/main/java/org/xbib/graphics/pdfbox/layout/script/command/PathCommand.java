package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.color.ColorFactory;
import org.xbib.graphics.pdfbox.layout.elements.PathElement;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.shape.Path;
import org.xbib.graphics.pdfbox.layout.shape.Stroke;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PathCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) {
        String value = settings.get("value");
        if (value == null) {
            return;
        }
        List<Position> list = new ArrayList<>();
        String[] s = value.split(" ");
        Position position = null;
        if (s.length > 0) {
            if (settings.getAsBoolean("absolute", false)) {
                position = new Position(Float.parseFloat(s[0]), Float.parseFloat(s[1]));
                list.add(position);
            } else {
                Position p = new Position(Float.parseFloat(s[0]), Float.parseFloat(s[1]));
                list.add(p);
            }
            for (int i = 2; i < s.length; i += 2) {
                Position p = new Position(Float.parseFloat(s[i]), Float.parseFloat(s[i + 1]));
                list.add(p);
            }
        }
        Path path = new Path(list);
        Stroke.StrokeBuilder strokeBuilder = Stroke.builder()
                .capStyle(Stroke.CapStyle.valueOf(settings.get("capstyie", "Cap")))
                .joinStyle(Stroke.JoinStyle.valueOf(settings.get("joinstyle", "Miter")))
                .lineWidth(settings.getAsFloat("linewidth", 1f));
        if (settings.containsSetting("dash")) {
                strokeBuilder.dashPattern(new Stroke.DashPattern(settings.getAsFloat("dash", 1f)));
        }
        Color color = ColorFactory.web(settings.get("color", "black"));
        state.document.add(new PathElement(path, strokeBuilder.build(), color, position));
    }
}
