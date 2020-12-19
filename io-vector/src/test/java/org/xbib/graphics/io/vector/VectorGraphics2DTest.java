package org.xbib.graphics.io.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.xbib.graphics.io.vector.util.ImageUtil.hasAlpha;
import static org.xbib.graphics.io.vector.util.ImageUtil.toBufferedImage;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.io.vector.commands.CreateCommand;
import org.xbib.graphics.io.vector.commands.DisposeCommand;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.util.Iterator;

public class VectorGraphics2DTest {

    @Test
    public void testEmptyVectorGraphics2DStartsWithCreateCommand() {
        VectorGraphics2D g = new VectorGraphics2D();
        Iterable<Command<?>> commands = g.getCommands();
        Iterator<Command<?>> commandIterator = commands.iterator();
        assertTrue(commandIterator.hasNext());
        Command<?> firstCommand = commandIterator.next();
        assertTrue(firstCommand instanceof CreateCommand);
        assertEquals(g, ((CreateCommand) firstCommand).getValue());
    }

    @Test
    public void testCreateEmitsCreateCommand() {
        VectorGraphics2D g = new VectorGraphics2D();
        VectorGraphics2D g2 = (VectorGraphics2D) g.create();
        assertNotNull(g2);
        CreateCommand g2CreateCommand = null;
        for (Command<?> g2Command : g2.getCommands()) {
            if (g2Command instanceof CreateCommand) {
                g2CreateCommand = (CreateCommand) g2Command;
            }
        }
        assertNotNull(g2CreateCommand);
        assertEquals(g2, g2CreateCommand.getValue());
    }

    @Test
    public void testDisposeCommandEmitted() {
        VectorGraphics2D g = new VectorGraphics2D();
        g.setColor(Color.RED);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLUE);
        g2.dispose();
        Iterable<Command<?>> commands = g.getCommands();
        Command<?> lastCommand = null;
        for (Command<?> command : commands) {
            lastCommand = command;
        }
        assertTrue(lastCommand instanceof DisposeCommand);
        assertEquals(Color.BLUE, ((DisposeCommand) lastCommand).getValue().getColor());
    }

    @Test
    public void testToBufferedImage() {
        Image[] images = {
                new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB),
                new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB),
                Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(
                        new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB).getSource(),
                        new RGBImageFilter() {
                            @Override
                            public int filterRGB(int x, int y, int rgb) {
                                return rgb & 0xff;
                            }
                        }
                ))
        };
        for (Image image : images) {
            BufferedImage bimage = toBufferedImage(image);
            assertNotNull(bimage);
            assertEquals(BufferedImage.class, bimage.getClass());
            assertEquals(image.getWidth(null), bimage.getWidth());
            assertEquals(image.getHeight(null), bimage.getHeight());
        }
    }

    @Test
    public void testHasAlpha() {
        Image image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
        assertTrue(hasAlpha(image));
        image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
        assertFalse(hasAlpha(image));
    }
}
