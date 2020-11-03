package org.xbib.graphics.chart.io.vector.intermediate.filters;

import org.xbib.graphics.chart.io.vector.intermediate.commands.Command;
import org.xbib.graphics.chart.io.vector.intermediate.commands.StateCommand;


public class StateChangeGroupingFilter extends GroupingFilter {

    public StateChangeGroupingFilter(Iterable<Command<?>> stream) {
        super(stream);
    }

    @Override
    protected boolean isGrouped(Command<?> command) {
        return command instanceof StateCommand;
    }
}

