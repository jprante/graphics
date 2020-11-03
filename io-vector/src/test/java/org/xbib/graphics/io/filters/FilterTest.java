package org.xbib.graphics.io.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.commands.DrawShapeCommand;
import org.xbib.graphics.io.vector.commands.SetColorCommand;
import org.xbib.graphics.io.vector.commands.SetStrokeCommand;
import org.xbib.graphics.io.vector.commands.SetTransformCommand;
import org.xbib.graphics.io.vector.filters.Filter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class FilterTest {

    @Test
    public void filterNone() {
        List<Command<?>> stream = new LinkedList<Command<?>>();
        stream.add(new SetColorCommand(Color.BLACK));
        stream.add(new SetStrokeCommand(new BasicStroke(1f)));
        stream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
        stream.add(new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
        stream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));

        Iterator<Command<?>> unfiltered = stream.iterator();

        Filter filtered = new Filter(stream) {
            @Override
            protected List<Command<?>> filter(Command<?> command) {
                return Collections.singletonList(command);
            }
        };

        while (filtered.hasNext() || unfiltered.hasNext()) {
            Command<?> expected = unfiltered.next();
            Command<?> result = filtered.next();
            assertEquals(expected, result);
        }
    }

    @Test
    public void filterAll() {
        List<Command<?>> stream = new LinkedList<Command<?>>();
        stream.add(new SetColorCommand(Color.BLACK));
        stream.add(new SetStrokeCommand(new BasicStroke(1f)));
        stream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
        stream.add(new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
        stream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));

        Iterator<Command<?>> unfiltered = stream.iterator();

        Filter filtered = new Filter(stream) {
            @Override
            protected List<Command<?>> filter(Command<?> command) {
                return null;
            }
        };
        assertTrue(unfiltered.hasNext());
        assertFalse(filtered.hasNext());
    }

    @Test
    public void duplicate() {
        List<Command<?>> stream = new LinkedList<Command<?>>();
        stream.add(new SetColorCommand(Color.BLACK));
        stream.add(new SetStrokeCommand(new BasicStroke(1f)));
        stream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
        stream.add(new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
        stream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));

        Iterator<Command<?>> unfiltered = stream.iterator();

        Filter filtered = new Filter(stream) {
            @Override
            protected List<Command<?>> filter(Command<?> command) {
                return Arrays.asList(command, command);
            }
        };

        while (filtered.hasNext() || unfiltered.hasNext()) {
            Command<?> expected = unfiltered.next();
            Command<?> result1 = filtered.next();
            Command<?> result2 = filtered.next();
            assertEquals(expected, result1);
            assertEquals(expected, result2);
        }
    }
}

