package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.Element;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.TextElement;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.Fonts;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.util.Locale;

import static org.xbib.graphics.pdfbox.layout.util.PdfUtil.mmToPt;

public class TextCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) {
        String value = settings.get("value");
        float size = settings.getAsFloat("fontsize", 11.0f);
        Document document = state.getDocument();
        Font font = Fonts.valueOf(settings.get("font", "helvetica").toUpperCase(Locale.ROOT)).getFont(document);
        Element element = state.elements.peek();
        if (element instanceof Paragraph) {
            element.add(new TextElement(value, font, size));
        } else if (element instanceof Document) {
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
            paragraph.add(new TextElement(value, font, size));
            Alignment alignment = Alignment.valueOf(settings.get("layout.alignment", "left").toUpperCase(Locale.ROOT));
            String margin = settings.get("layout.margin", "0 0 0 0");
            String[] margins = margin.split(" ");
            float marginleft = Float.parseFloat(margins[0]);
            float marginright = Float.parseFloat(margins[1]);
            float margintop = Float.parseFloat(margins[2]);
            float marginbottom = Float.parseFloat(margins[3]);
            boolean resetY = settings.getAsBoolean("layout.resety", false);
            VerticalLayoutHint verticalLayoutHint = new VerticalLayoutHint(alignment, marginleft, marginright, margintop, marginbottom, resetY);
            element.add(paragraph, verticalLayoutHint);
        }
    }
}
