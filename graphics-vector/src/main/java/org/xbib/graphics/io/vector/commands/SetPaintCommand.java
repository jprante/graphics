package org.xbib.graphics.io.vector.commands;

import java.awt.Paint;

public class SetPaintCommand extends StateCommand<Paint> {

    public SetPaintCommand(Paint paint) {
        super(paint);
    }

    @Override
    public String getKey() {
        return "setPaint";
    }
}

