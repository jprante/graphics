package org.xbib.graphics.io.vector.filters;

import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.commands.StateCommand;

public class StateChangeGroupingFilter extends GroupingFilter {

    public StateChangeGroupingFilter(Iterable<Command<?>> stream) {
        super(stream);
    }

    @Override
    protected boolean isGrouped(Command<?> command) {
        return command instanceof StateCommand;
    }
}

