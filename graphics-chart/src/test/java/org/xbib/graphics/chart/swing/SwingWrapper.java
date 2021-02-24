package org.xbib.graphics.chart.swing;

import org.xbib.graphics.chart.io.BitmapFormat;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.Chart;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * A convenience class used to display a Chart in a Swing application.
 */
public class SwingWrapper {

    private String windowTitle = "Chart";

    private List<Chart<?, ?>> charts = new ArrayList<>();

    private int numRows;

    private int numColumns;

    public SwingWrapper(Chart<?, ?> chart) {
        this.charts.add(chart);
    }

    public SwingWrapper(List<Chart<?, ?>> charts) {
        this.charts = charts;
        this.numRows = (int) (Math.sqrt(charts.size()) + .5);
        this.numColumns = (int) ((double) charts.size() / this.numRows + 1);
    }

    public SwingWrapper(List<Chart<?, ?>> charts, int numRows, int numColumns) {
        this.charts = charts;
        this.numRows = numRows;
        this.numColumns = numColumns;
    }

    public JFrame displayChart(String windowTitle) {
        this.windowTitle = windowTitle;
        return displayChart();
    }

    public JFrame displayChart() {
        final JFrame frame = new JFrame(windowTitle);
        SwingUtilities.invokeLater(() -> {
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JPanel chartPanel = new ChartPanel<>(charts.get(0));
            frame.add(chartPanel);
            frame.pack();
            frame.setVisible(true);
        });

        return frame;
    }

    public JFrame displayChartMatrix(String windowTitle) {
        this.windowTitle = windowTitle;
        return displayChartMatrix();
    }

    private JFrame displayChartMatrix() {
        final JFrame frame = new JFrame(windowTitle);
        SwingUtilities.invokeLater(() -> {
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new GridLayout(numRows, numColumns));
            for (Chart<?, ?> chart : charts) {
                if (chart != null) {
                    JPanel chartPanel = new ChartPanel<>(chart);
                    frame.add(chartPanel);
                } else {
                    JPanel chartPanel = new JPanel();
                    frame.getContentPane().add(chartPanel);
                }
            }
            frame.pack();
            frame.setVisible(true);
        });
        return frame;
    }

    @SuppressWarnings("serial")
    private static class ChartPanel<T extends Chart<?, ?>> extends JPanel {

        private final T chart;

        private final Dimension preferredSize;

        private String saveAsString = "Save As...";

        public ChartPanel(T chart) {
            this.chart = chart;
            preferredSize = new Dimension(chart.getWidth(), chart.getHeight());
            // Right-click listener for saving chart
            this.addMouseListener(new PopUpMenuClickListener());
            // Control+S key listener for saving chart
            KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
            this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(ctrlS, "save");
            this.getActionMap().put("save", new SaveAction());
        }

        public void setSaveAsString(String saveAsString) {
            this.saveAsString = saveAsString;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            chart.paint(g2d, getWidth(), getHeight());
            g2d.dispose();
        }

        public T getChart() {
            return this.chart;
        }

        @Override
        public Dimension getPreferredSize() {
            return this.preferredSize;
        }

        void showSaveAsDialog() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new SuffixSaveFilter("jpg"));
            FileFilter pngFileFilter = new SuffixSaveFilter("png");
            fileChooser.addChoosableFileFilter(pngFileFilter);
            fileChooser.addChoosableFileFilter(new SuffixSaveFilter("bmp"));
            fileChooser.addChoosableFileFilter(new SuffixSaveFilter("gif"));
            fileChooser.addChoosableFileFilter(new SuffixSaveFilter("svg"));
            fileChooser.addChoosableFileFilter(new SuffixSaveFilter("eps"));
            fileChooser.addChoosableFileFilter(new SuffixSaveFilter("pdf"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(pngFileFilter);
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                if (fileChooser.getSelectedFile() != null) {
                    File theFileToSave = fileChooser.getSelectedFile();
                    try {
                        OutputStream outputStream = Files.newOutputStream(theFileToSave.toPath());
                        if (fileChooser.getFileFilter() == null) {
                            chart.saveBitmap(outputStream, BitmapFormat.PNG);
                        } else if (fileChooser.getFileFilter().getDescription().equals("*.jpg,*.JPG")) {
                            chart.saveJPGWithQuality(outputStream, 1.0f);
                        } else if (fileChooser.getFileFilter().getDescription().equals("*.png,*.PNG")) {
                            chart.saveBitmap(outputStream, BitmapFormat.PNG);
                        } else if (fileChooser.getFileFilter().getDescription().equals("*.bmp,*.BMP")) {
                            chart.saveBitmap(outputStream, BitmapFormat.BMP);
                        } else if (fileChooser.getFileFilter().getDescription().equals("*.gif,*.GIF")) {
                            chart.saveBitmap(outputStream, BitmapFormat.GIF);
                        } else if (fileChooser.getFileFilter().getDescription().equals("*.svg,*.SVG")) {
                            chart.write(outputStream, VectorGraphicsFormat.SVG);
                        } else if (fileChooser.getFileFilter().getDescription().equals("*.eps,*.EPS")) {
                            chart.write(outputStream, VectorGraphicsFormat.EPS);
                        } else if (fileChooser.getFileFilter().getDescription().equals("*.pdf,*.PDF")) {
                            chart.write(outputStream, VectorGraphicsFormat.PDF);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        class SaveAction extends AbstractAction {

            public SaveAction() {
                super("save");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                showSaveAsDialog();
            }
        }

        class SuffixSaveFilter extends FileFilter {

            private final String suffix;

            public SuffixSaveFilter(String suffix) {
                this.suffix = suffix;
            }

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String s = f.getName();
                return s.endsWith("." + suffix) || s.endsWith("." + suffix.toUpperCase());
            }

            @Override
            public String getDescription() {
                return "*." + suffix + ",*." + suffix.toUpperCase();
            }
        }

        class PopUpMenuClickListener extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    doPop(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    doPop(e);
                }
            }

            private void doPop(MouseEvent e) {
                ChartPanelPopupMenu menu = new ChartPanelPopupMenu();
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        class ChartPanelPopupMenu extends JPopupMenu {

            JMenuItem saveAsMenuItem;

            ChartPanelPopupMenu() {
                saveAsMenuItem = new JMenuItem(saveAsString);
                saveAsMenuItem.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        showSaveAsDialog();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                    }
                });
                add(saveAsMenuItem);
            }
        }
    }


}
