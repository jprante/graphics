package org.xbib.graphics.pdfbox.layout.element.scripting;

import org.xbib.graphics.pdfbox.layout.element.scripting.command.Command;
import org.xbib.settings.Settings;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

public class Engine implements Closeable {

    private static final Logger logger = Logger.getLogger(Engine.class.getName());

    private final String packageName;

    private final ClassLoader classLoader;

    private final State state;

    public Engine() {
        packageName = getClass().getPackageName();
        classLoader = getClass().getClassLoader();
        this.state = new State();
    }

    public void execute(Settings settings) throws IOException {
        executeSettings(settings);
    }

    public void executeElements(Settings settings) throws IOException {
        execute(settings.getAsSettings("elements"));
        for (int i = 0; i < 256; i++) {
            if (!executeElement(i, settings)) {
                break;
            }
        }
    }

    private boolean executeElement(int i, Settings settings) throws IOException {
        String key = "elements." + i;
        if (settings.containsSetting(key)) {
            executeSettings(settings.getAsSettings(key));
            return true;
        }
        return false;
    }

    private void executeSettings(Settings settings) throws IOException {
        try {
            String type = settings.get("type");
            if (type == null) {
                return;
            }
            String className = packageName + ".command." + type.substring(0, 1).toUpperCase() + type.substring(1) + "Command";
            Class<?> cl = classLoader.loadClass(className);
            Command command = (Command) cl.getConstructor().newInstance();
            logger.finer("executing element " + type + " settings = " + settings.getAsMap());
            command.execute(this, state, settings);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
    }

    public State getState() {
        return state;
    }
}
