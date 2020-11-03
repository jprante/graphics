package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.Composite;

public class SetCompositeCommand extends StateCommand<Composite> {
    public SetCompositeCommand(Composite composite) {
        super(composite);
    }
}

