package org.xbib.graphics.pdfbox.layout.test.script;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.settings.Settings;

import java.io.FileOutputStream;

public class ScriptTest {

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
}
