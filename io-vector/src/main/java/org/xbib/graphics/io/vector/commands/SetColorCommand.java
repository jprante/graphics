package org.xbib.graphics.io.vector.commands;

import java.awt.Color;

public class SetColorCommand extends StateCommand<Color> {

    public SetColorCommand(Color color) {
        super(color);
    }

    @Override
    public String getKey() {
        return "setColor";
    }
}
