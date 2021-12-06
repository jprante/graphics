package org.xbib.graphics.pdfbox.layout.script;

import org.xbib.graphics.pdfbox.layout.script.command.Command;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.util.Set;

public class Engine {
    private final String packageName;

    private final ClassLoader classLoader;

    private final State state;

    public Engine() {
        packageName = getClass().getPackageName();
        classLoader = getClass().getClassLoader();
        this.state = new State();
    }

    public void execute(Settings settings) throws IOException {
        execute("document", state, settings);
    }

    public void execute(String prefix, State state, Settings settings) throws IOException {
        Settings subSettings = settings.getByPrefix(prefix);
        Set<String> set = subSettings.getAsStructuredMap().keySet();
        for (String string : set) {
            try {
                Settings thisSettings = settings.getAsSettings(prefix + string);
                String type = thisSettings.get("type");
                if (type == null) {
                    type = prefix;
                }
                String className = packageName + ".command." + type.substring(0, 1).toUpperCase() + type.substring(1) + "Command";
                Class<?> cl = classLoader.loadClass(className);
                Command command = (Command) cl.getConstructor().newInstance();
                command.execute(this, state, thisSettings);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    public State getState() {
        return state;
    }
}
