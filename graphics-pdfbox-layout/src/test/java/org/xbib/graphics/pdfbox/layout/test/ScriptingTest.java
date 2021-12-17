package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.element.Document;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.settings.Settings;

import java.io.FileOutputStream;

public class ScriptingTest {

    @Test
    public void script() throws Exception {
        Settings settings = Settings.settingsBuilder()
                .loadFromResource("json", getClass().getResourceAsStream("elements.json"))
                .build();
        Engine engine = new Engine();
        engine.execute(settings);
        int i = 0;
        for (Document document : engine.getState().getDocuments()) {
            document.render().save(new FileOutputStream("build/elements" + (i++) + ".pdf")).close();
        }
        engine.close();
    }

    @Test
    public void deckblatt() throws Exception {
        Settings settings = Settings.settingsBuilder()
                .loadFromResource("json", getClass().getResourceAsStream("deckblatt.json"))
                .build();
        Engine engine = new Engine();
        engine.execute(settings);
        int i = 0;
        for (Document document : engine.getState().getDocuments()) {
            document.render().save(new FileOutputStream("build/deckblatt" + (i++) + ".pdf")).close();
        }
        engine.close();
    }
}
