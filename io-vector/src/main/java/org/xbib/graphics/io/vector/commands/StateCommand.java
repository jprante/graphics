package org.xbib.graphics.io.vector.commands;

import org.xbib.graphics.io.vector.Command;

public abstract class StateCommand<T> extends Command<T> {
    public StateCommand(T value) {
        super(value);
    }
}

