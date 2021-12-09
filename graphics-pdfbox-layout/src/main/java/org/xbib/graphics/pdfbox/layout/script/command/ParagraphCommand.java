package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.util.Locale;

import static org.xbib.graphics.pdfbox.layout.util.PdfUtil.mmToPt;

public class ParagraphCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        Paragraph paragraph = new Paragraph();
        if (settings.containsSetting("x") && settings.containsSetting("y")) {
            paragraph.setAbsolutePosition(new Position(mmToPt(settings.getAsFloat("x", 0f)), mmToPt(settings.getAsFloat("y", 0f))));
        }
        if (settings.containsSetting("width")) {
            paragraph.setMaxWidth(settings.getAsFloat("width", 0f));
        }
        if (settings.containsSetting("alignment")) {
            paragraph.setAlignment(Alignment.valueOf(settings.get("alignment", "left").toUpperCase(Locale.ROOT)));
        }
        state.elements.push(paragraph);
        engine.executeElements(settings);
        state.elements.pop();
        Alignment alignment = Alignment.valueOf(settings.get("layout.alignment", "left").toUpperCase(Locale.ROOT));
        String margin = settings.get("layout.margin", "0 0 0 0");
        String[] margins = margin.split(" ");
        float marginleft = Float.parseFloat(margins[0]);
        float marginright = Float.parseFloat(margins[1]);
        float margintop = Float.parseFloat(margins[2]);
        float marginbottom = Float.parseFloat(margins[3]);
        boolean resetY = settings.getAsBoolean("layout.resety", false);
        VerticalLayoutHint verticalLayoutHint = new VerticalLayoutHint(alignment, marginleft, marginright, margintop, marginbottom, resetY);
        state.elements.peek().add(paragraph, verticalLayoutHint);
    }
}
