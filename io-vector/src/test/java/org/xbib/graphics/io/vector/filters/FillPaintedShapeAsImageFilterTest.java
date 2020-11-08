package org.xbib.graphics.io.vector.filters;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.commands.DrawImageCommand;
import org.xbib.graphics.io.vector.commands.FillShapeCommand;
import org.xbib.graphics.io.vector.commands.RotateCommand;
import org.xbib.graphics.io.vector.commands.SetPaintCommand;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FillPaintedShapeAsImageFilterTest {

    @Test
    public void testFillShapeReplacedWithDrawImage() {
        List<Command<?>> commands = new LinkedList<Command<?>>();
        commands.add(new SetPaintCommand(new GradientPaint(0.0f, 0.0f, Color.BLACK, 100.0f, 100.0f, Color.WHITE)));
        commands.add(new RotateCommand(10.0, 4.0, 2.0));
        commands.add(new FillShapeCommand(new Rectangle2D.Double(10.0, 10.0, 100.0, 100.0)));

        FillPaintedShapeAsImageFilter filter = new FillPaintedShapeAsImageFilter(commands);

        assertThat(filter, hasItem(any(DrawImageCommand.class)));
        assertThat(filter, not(hasItem(any(FillShapeCommand.class))));
    }

    @Test
    public void testFillShapeNotReplacedWithoutPaintCommand() {
        List<Command<?>> commands = new LinkedList<Command<?>>();
        commands.add(new RotateCommand(10.0, 4.0, 2.0));
        commands.add(new FillShapeCommand(new Rectangle2D.Double(10.0, 10.0, 100.0, 100.0)));

        FillPaintedShapeAsImageFilter filter = new FillPaintedShapeAsImageFilter(commands);

        Iterator<Command<?>> filterIterator = filter.iterator();
        for (Command<?> command : commands) {
            assertEquals(command, filterIterator.next());
        }
        assertFalse(filterIterator.hasNext());
    }
}

