package org.xbib.graphics.pdfbox.layout.test.script;

import org.junit.jupiter.api.Test;
import org.xbib.settings.Settings;

import java.util.logging.Logger;

public class ElementsTest {

    @Test
    public void script() throws Exception {
        Settings settings = Settings.settingsBuilder()
                .loadFromResource("json", getClass().getResourceAsStream("elements.json"))
                .build();
        Logger.getAnonymousLogger().info(settings.getAsMap().toString());
        Logger.getAnonymousLogger().info(String.valueOf(settings.containsSetting("elements")));
        Logger.getAnonymousLogger().info(String.valueOf(settings.containsSetting("elements.0")));
        Logger.getAnonymousLogger().info(String.valueOf(settings.containsSetting("elements.1")));
        Logger.getAnonymousLogger().info(String.valueOf(settings.containsSetting("elements.2")));
        Logger.getAnonymousLogger().info(settings.getAsSettings("elements").getAsMap().toString());
        Logger.getAnonymousLogger().info(settings.getAsSettings("elements.0").getAsMap().toString());
        Logger.getAnonymousLogger().info(settings.getAsSettings("elements.1").getAsMap().toString());
        Logger.getAnonymousLogger().info(settings.getAsSettings("elements.2").getAsMap().toString());
    }
}
