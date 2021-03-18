package org.xbib.graphics.graph.jmathplot.panel;

import org.xbib.graphics.graph.jmathplot.io.ClipBoardPrintable;
import org.xbib.graphics.graph.jmathplot.io.FilePrintable;
import org.xbib.graphics.graph.jmathplot.io.StringPrintable;
import org.xbib.graphics.graph.jmathplot.frame.DataToolBar;
import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * BSD License
 *
 * @author Yann RICHET
 */
public abstract class DataPanel extends JPanel implements ComponentListener, FilePrintable, ClipBoardPrintable, StringPrintable {

    protected DataToolBar toolBar;
    protected JScrollPane scrollPane;
    public static int[] dimension = new int[]{400, 400};

    public DataPanel() {
        setLayout(new BorderLayout());
        initToolBar();
        init();
    }

    protected void initToolBar() {
        toolBar = new DataToolBar(this);
        add(toolBar, BorderLayout.NORTH);
        toolBar.setFloatable(false);
    }

    protected void initSize() {
        if (scrollPane != null) {
            scrollPane.setSize(this.getSize());
        }
        // scrollPane.setPreferredSize(this.getSize());
    }

    protected void init() {
        // initSize();
        addComponentListener(this);
    }

    public void update() {
        // this.remove(scrollPane);
        toWindow();
        repaint();
    }

    protected abstract void toWindow();

    public abstract void toClipBoard();

    public abstract void toASCIIFile(File file);

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        /*
         * dimension = new int[] { (int) (this.getSize().getWidth()), (int)
         * (this.getSize().getHeight()) };
         */
        initSize();
    }

    public void componentShown(ComponentEvent e) {
    }
}