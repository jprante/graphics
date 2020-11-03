package org.xbib.graphics.io.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.commands.DrawShapeCommand;
import org.xbib.graphics.io.vector.commands.Group;
import org.xbib.graphics.io.vector.commands.SetColorCommand;
import org.xbib.graphics.io.vector.commands.SetStrokeCommand;
import org.xbib.graphics.io.vector.commands.SetTransformCommand;
import org.xbib.graphics.io.vector.commands.StateCommand;
import org.xbib.graphics.io.vector.filters.Filter;
import org.xbib.graphics.io.vector.filters.GroupingFilter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GroupingFilterTest {

    @Test
    public void filtered() {
        List<Command<?>> resultStream = new LinkedList<Command<?>>();
        resultStream.add(new SetColorCommand(Color.BLACK));
        resultStream.add(new SetStrokeCommand(new BasicStroke(1f)));
        resultStream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
        resultStream.add(new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
        resultStream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));
        List<Command<?>> expectedStream = new LinkedList<Command<?>>();
        Iterator<Command<?>> resultCloneIterator = resultStream.iterator();
        Group group1 = new Group();
        group1.add(resultCloneIterator.next());
        group1.add(resultCloneIterator.next());
        expectedStream.add(group1);
        expectedStream.add(resultCloneIterator.next());
        Group group2 = new Group();
        group2.add(resultCloneIterator.next());
        expectedStream.add(group2);
        expectedStream.add(resultCloneIterator.next());
        Iterator<Command<?>> expectedIterator = expectedStream.iterator();
        Filter resultIterator = new GroupingFilter(resultStream) {
            @Override
            protected boolean isGrouped(Command<?> command) {
                return command instanceof StateCommand;
            }
        };
        while (resultIterator.hasNext() || expectedIterator.hasNext()) {
            Command<?> result = resultIterator.next();
            Command<?> expected = expectedIterator.next();
            assertEquals(expected, result);
        }
    }
}
