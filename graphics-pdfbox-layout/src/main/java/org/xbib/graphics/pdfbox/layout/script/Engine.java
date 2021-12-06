package org.xbib.graphics.pdfbox.layout.script;

import org.xbib.graphics.pdfbox.layout.script.command.Command;
import org.xbib.settings.Settings;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Engine implements Closeable {

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
        execute(List.of(prefix), state, settings);
    }

    public void execute(List<String> prefixes, State state, Settings settings) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        for (String prefix : prefixes) {
            Settings subSettings = settings.getByPrefix(prefix);
            for (String string : subSettings.getAsStructuredMap().keySet()) {
                map.put(prefix + string, prefix);
            }
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                Settings thisSettings = settings.getAsSettings(entry.getKey());
                String type = thisSettings.get("type");
                if (type == null) {
                    type = entry.getValue();
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

    @Override
    public void close() throws IOException {
    }

    public State getState() {
        return state;
    }
}
