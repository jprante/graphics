package org.xbib.graphics.io.vector.commands;

import java.util.Locale;

public class SetHintCommand extends StateCommand<Object> {

    private final Object key;

    public SetHintCommand(Object hintKey, Object hintValue) {
        super(hintValue);
        key = hintKey;
    }

    public Object getHintKey() {
        return key;
    }

    @Override
    public String getKey() {
        return "setHint";
    }

    @Override
    public String toString() {
        return String.format((Locale) null,
                "%s[key=%s, value=%s]", getKey(), getHintKey(), getValue());
    }
}
