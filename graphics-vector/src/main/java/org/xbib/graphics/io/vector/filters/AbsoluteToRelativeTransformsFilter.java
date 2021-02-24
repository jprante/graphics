package org.xbib.graphics.io.vector.filters;

import org.xbib.graphics.io.vector.commands.AffineTransformCommand;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.commands.CreateCommand;
import org.xbib.graphics.io.vector.commands.DisposeCommand;
import org.xbib.graphics.io.vector.commands.SetTransformCommand;
import org.xbib.graphics.io.vector.commands.TransformCommand;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class AbsoluteToRelativeTransformsFilter extends Filter {

    private final Deque<AffineTransform> transforms;

    public AbsoluteToRelativeTransformsFilter(Iterable<Command<?>> stream) {
        super(stream);
        transforms = new LinkedList<>();
    }

    @Override
    public Command<?> next() {
        Command<?> nextCommand = super.next();
        if (nextCommand instanceof AffineTransformCommand) {
            AffineTransformCommand affineTransformCommand = (AffineTransformCommand) nextCommand;
            getCurrentTransform().concatenate(affineTransformCommand.getValue());
        } else if (nextCommand instanceof CreateCommand) {
            AffineTransform newTransform = transforms.isEmpty() ? new AffineTransform() : new AffineTransform(getCurrentTransform());
            transforms.push(newTransform);
        } else if (nextCommand instanceof DisposeCommand) {
            transforms.pop();
        }
        return nextCommand;
    }

    @Override
    protected List<Command<?>> filter(Command<?> command) {
        if (command instanceof SetTransformCommand) {
            SetTransformCommand setTransformCommand = (SetTransformCommand) command;
            AffineTransform absoluteTransform = setTransformCommand.getValue();
            AffineTransform relativeTransform = new AffineTransform();
            try {
                AffineTransform invertedOldTransformation = getCurrentTransform().createInverse();
                relativeTransform.concatenate(invertedOldTransformation);
            } catch (NoninvertibleTransformException e) {
                // ignore
            }
            relativeTransform.concatenate(absoluteTransform);
            TransformCommand transformCommand = new TransformCommand(relativeTransform);
            return Collections.singletonList(transformCommand);
        }
        return Collections.singletonList(command);
    }

    private AffineTransform getCurrentTransform() {
        return transforms.peek();
    }
}

