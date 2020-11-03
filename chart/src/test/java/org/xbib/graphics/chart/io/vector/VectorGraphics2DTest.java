package org.xbib.graphics.chart.io.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.chart.io.vector.intermediate.commands.Command;
import org.xbib.graphics.chart.io.vector.intermediate.commands.CreateCommand;
import org.xbib.graphics.chart.io.vector.intermediate.commands.DisposeCommand;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

public class VectorGraphics2DTest {

    @Test
    public void testEmptyVectorGraphics2DStartsWithCreateCommand() {
        VectorGraphics2D g = new VectorGraphics2D();
        Iterable<Command<?>> commands = g.getCommands();
        Iterator<Command<?>> commandIterator = commands.iterator();
        assertTrue(commandIterator.hasNext());
        Command<?> firstCommand = commandIterator.next();
        assertTrue(firstCommand instanceof CreateCommand);
        assertEquals(g, ((CreateCommand) firstCommand).getValue());
    }

    @Test
    public void testCreateEmitsCreateCommand() {
        VectorGraphics2D g = new VectorGraphics2D();
        Iterable<Command<?>> gCommands = g.getCommands();
        Iterator<Command<?>> gCommandIterator = gCommands.iterator();
        CreateCommand gCreateCommand = (CreateCommand) gCommandIterator.next();

        VectorGraphics2D g2 = (VectorGraphics2D) g.create();
        CreateCommand g2CreateCommand = null;
        for (Command<?> g2Command : g2.getCommands()) {
            if (g2Command instanceof CreateCommand) {
                g2CreateCommand = (CreateCommand) g2Command;
            }
        }
        assertNotNull(g2CreateCommand);
        assertNotEquals(gCreateCommand, g2CreateCommand);
        assertEquals(g2, g2CreateCommand.getValue());
    }

    @Test
    public void testDisposeCommandEmitted() {
        VectorGraphics2D g = new VectorGraphics2D();
        g.setColor(Color.RED);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLUE);
        g2.dispose();
        Iterable<Command<?>> commands = g.getCommands();
        Command<?> lastCommand = null;
        for (Command<?> command : commands) {
            lastCommand = command;
        }
        assertTrue(lastCommand instanceof DisposeCommand);
        assertEquals(Color.BLUE, ((DisposeCommand) lastCommand).getValue().getColor());
    }
}
