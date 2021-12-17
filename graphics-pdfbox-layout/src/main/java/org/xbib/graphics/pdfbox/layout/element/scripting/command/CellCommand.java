package org.xbib.graphics.pdfbox.layout.element.scripting.command;

import org.xbib.graphics.pdfbox.layout.color.ColorFactory;
import org.xbib.graphics.pdfbox.layout.element.Document;
import org.xbib.graphics.pdfbox.layout.element.Paragraph;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.Fonts;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.graphics.pdfbox.layout.element.scripting.State;
import org.xbib.graphics.pdfbox.layout.table.BorderStyle;
import org.xbib.graphics.pdfbox.layout.table.BorderStyleInterface;
import org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment;
import org.xbib.graphics.pdfbox.layout.table.Markup;
import org.xbib.graphics.pdfbox.layout.table.ParagraphCell;
import org.xbib.graphics.pdfbox.layout.table.TextCell;
import org.xbib.graphics.pdfbox.layout.table.VerticalAlignment;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.awt.Color;
import java.io.IOException;
import java.util.Locale;

import static org.xbib.graphics.pdfbox.layout.util.PdfUtil.mmToPt;

public class CellCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        if (settings.containsSetting("value")) {
            TextCell.Builder cell = TextCell.builder();
            cell.padding(settings.getAsFloat("padding", 0f));
            cell.text(settings.get("value"));
            cell.fontSize(settings.getAsFloat("fontsize", 11.0f));
            Document document = state.getDocument();
            Font font = Fonts.valueOf(settings.get("font", "helvetica").toUpperCase(Locale.ROOT)).getFont(document);
            cell.font(font);
            Color color = ColorFactory.web(settings.get("color", "black"));
            cell.textColor(color);
            if (settings.containsSetting("backgroundcolor")) {
                Color backgroundColor = ColorFactory.web(settings.get("backgroundcolor", "black"));
                cell.backgroundColor(backgroundColor);
            }
            if (settings.containsSetting("bordercolor")) {
                Color borderColor = ColorFactory.web(settings.get("bordercolor", "black"));
                cell.borderColor(borderColor);
            }
            cell.borderWidth(settings.getAsFloat("borderwidth", 0f));
            BorderStyleInterface styleInterface = BorderStyle.valueOf(settings.get("borderstyle", "solid").toUpperCase(Locale.ROOT));
            cell.borderStyle(styleInterface);
            if (settings.containsSetting("horizontalalignment")) {
                cell.horizontalAlignment(HorizontalAlignment.valueOf(settings.get("horizontalalignment", "left").toUpperCase(Locale.ROOT)));
            }
            if (settings.containsSetting("verticalalignment")) {
                cell.verticalAlignment(VerticalAlignment.valueOf(settings.get("verticalalignment", "left").toUpperCase(Locale.ROOT)));
            }
            cell.colSpan(settings.getAsInt("colspan", 1));
            cell.rowSpan(settings.getAsInt("rowspan", 1));
            state.elements.peek().add(cell.build());
        } else if (settings.containsSetting("markup")) {
            ParagraphCell.Builder cell = ParagraphCell.builder();
            cell.colSpan(settings.getAsInt("colspan", 1));
            cell.rowSpan(settings.getAsInt("rowspan", 1));
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
            cell.paragraph(paragraph);
            String value = settings.get("markup");
            float size = settings.getAsFloat("fontsize", 11.0f);
            Document document = state.getDocument();
            Font font = Fonts.valueOf(settings.get("font", "helvetica").toUpperCase(Locale.ROOT)).getFont(document);
            cell.add(new Markup().setValue(value).setFont(font).setFontSize(size));
            state.elements.peek().add(cell.build());
        }
    }
}
