package org.xbib.graphics.io.vector.commands;

import java.awt.Composite;

public class SetCompositeCommand extends StateCommand<Composite> {
    public SetCompositeCommand(Composite composite) {
        super(composite);
    }
}

