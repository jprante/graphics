package org.xbib.graphics.io.vector.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.commands.CreateCommand;
import org.xbib.graphics.io.vector.commands.DisposeCommand;
import org.xbib.graphics.io.vector.commands.SetTransformCommand;
import org.xbib.graphics.io.vector.commands.TransformCommand;
import org.xbib.graphics.io.vector.commands.TranslateCommand;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbsoluteToRelativeTransformsFilterTest {

    @Test
    public void testAbsoluteAndRelativeTransformsIdentical() {
        AffineTransform absoluteTransform = new AffineTransform();
        absoluteTransform.rotate(42.0);
        absoluteTransform.translate(4.0, 2.0);
        List<Command<?>> commands = wrapCommands(
                new SetTransformCommand(absoluteTransform)
        );

        AbsoluteToRelativeTransformsFilter filter = new AbsoluteToRelativeTransformsFilter(commands);

        filter.next();
        AffineTransform relativeTransform = ((TransformCommand) filter.next()).getValue();
        assertThat(relativeTransform, is(absoluteTransform));
    }

    @Test
    public void testTranslateCorrect() {
        AffineTransform absoluteTransform = new AffineTransform();
        absoluteTransform.scale(2.0, 2.0);
        absoluteTransform.translate(4.2, 4.2); // (8.4, 8.4)
        List<Command<?>> commands = wrapCommands(
                new TranslateCommand(4.0, 2.0),
                new SetTransformCommand(absoluteTransform)
        );

        AbsoluteToRelativeTransformsFilter filter = new AbsoluteToRelativeTransformsFilter(commands);

        TransformCommand transformCommand = null;
        while (filter.hasNext()) {
            Command<?> filteredCommand = filter.next();
            if (filteredCommand instanceof TransformCommand) {
                transformCommand = (TransformCommand) filteredCommand;
            }
        }
        AffineTransform relativeTransform = transformCommand.getValue();
        assertThat(relativeTransform.getTranslateX(), is(4.4));
        assertThat(relativeTransform.getTranslateY(), is(6.4));
    }

    @Test
    public void testRelativeTransformAfterDispose() {
        AffineTransform absoluteTransform = new AffineTransform();
        absoluteTransform.rotate(42.0);
        absoluteTransform.translate(4.0, 2.0);
        List<Command<?>> commands = wrapCommands(
                new CreateCommand(null),
                new TransformCommand(absoluteTransform),
                new DisposeCommand(null),
                new SetTransformCommand(absoluteTransform)
        );

        AbsoluteToRelativeTransformsFilter filter = new AbsoluteToRelativeTransformsFilter(commands);
        TransformCommand lastTransformCommand = null;
        for (Command<?> filteredCommand : filter) {
            if (filteredCommand instanceof TransformCommand) {
                lastTransformCommand = (TransformCommand) filteredCommand;
            }
        }
        assertThat(lastTransformCommand.getValue(), is(absoluteTransform));
    }

    private List<Command<?>> wrapCommands(Command<?>... commands) {
        List<Command<?>> commandList = new ArrayList<Command<?>>(commands.length + 2);
        commandList.add(new CreateCommand(null));
        commandList.addAll(Arrays.asList(commands));
        commandList.add(new DisposeCommand(null));
        return commandList;
    }
}

