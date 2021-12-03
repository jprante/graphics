package org.xbib.graphics.pdfbox.layout.script;

import org.xbib.graphics.pdfbox.layout.script.command.Command;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.util.Map;

public class Engine {

    private final State state;

    public Engine() {
        this.state = new State();
    }

    public void execute(String key, Settings settings) throws IOException {
        State state = new State();
        String packageName = getClass().getPackageName();
        ClassLoader classLoader = getClass().getClassLoader();
        for (Map.Entry<String, Settings> entry : settings.getGroups(key).entrySet()) {
            try {
                String string = entry.getKey();
                Class<?> cl = classLoader.loadClass(packageName + ".command." + string.substring(0, 1).toUpperCase() + string.substring(1) + "Command");
                Command command  = (Command) cl.getConstructor().newInstance();
                command.execute(this, state, entry.getValue());
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    public State getState() {
        return state;
    }
}
