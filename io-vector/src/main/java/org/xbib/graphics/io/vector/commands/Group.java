package org.xbib.graphics.io.vector.commands;

import org.xbib.graphics.io.vector.Command;
import java.util.LinkedList;
import java.util.List;

public class Group extends Command<List<Command<?>>> {

    public Group() {
        super(new LinkedList<>());
    }

    public void add(Command<?> command) {
        List<Command<?>> group = getValue();
        group.add(command);
    }

    @Override
    public String getKey() {
        return "group";
    }
}
