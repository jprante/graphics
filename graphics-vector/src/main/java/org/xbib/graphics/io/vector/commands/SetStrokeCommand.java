package org.xbib.graphics.io.vector.commands;

import java.awt.Stroke;

public class SetStrokeCommand extends StateCommand<Stroke> {

    public SetStrokeCommand(Stroke stroke) {
        super(stroke);
    }

    @Override
    public String getKey() {
        return "setStroke";
    }
}
