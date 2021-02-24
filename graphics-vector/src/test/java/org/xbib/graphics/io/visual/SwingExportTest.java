package org.xbib.graphics.io.visual;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;

public class SwingExportTest extends AbstractTest {

    public SwingExportTest() throws IOException {
    }

    @Override
    public void draw(Graphics2D g) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JButton("Hello Swing!"), BorderLayout.CENTER);
        frame.getContentPane().add(new JSlider(), BorderLayout.NORTH);
        frame.setSize(200, 250);
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        frame.setVisible(true);
        frame.printAll(g);
        frame.setVisible(false);
        frame.dispose();
    }
}
