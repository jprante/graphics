package org.xbib.graphics.io.visual;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class TestBrowser extends JFrame {

    private final List<AbstractTest> testCases;

    private final ImageComparisonPanel imageComparisonPanel;

    private AbstractTest testCase;

    public TestBrowser() throws IOException {
        super("Test browser");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1024, 768);
        testCases = new ArrayList<>();
        testCases.add(new ColorTest());
        testCases.add(new StrokeTest());
        testCases.add(new ShapesTest());
        testCases.add(new FontTest());
        testCases.add(new CharacterTest());
        testCases.add(new EmptyFileTest());
        testCases.add(new ImageTest());
        testCases.add(new ClippingTest());
        testCases.add(new PaintTest());
        testCases.add(new SwingExportTest());
        testCases.add(new TransformTest());
        final JList<?> testList = new JList<>(testCases.toArray());
        testList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        testList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String testName = value.getClass().getSimpleName();
                return super.getListCellRendererComponent(list, testName, index, isSelected, cellHasFocus);
            }
        });
        testList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = testList.getSelectedIndex();
                if (index < 0) {
                    return;
                }
                AbstractTest test = testCases.get(index);
                testCase = test;
                setTestCase(test);
            }
        });
        getContentPane().add(testList, BorderLayout.WEST);

        JPanel configurableImageComparisonPanel = new JPanel(new BorderLayout());
        getContentPane().add(configurableImageComparisonPanel, BorderLayout.CENTER);

        ImageFormat startingImageFormat = ImageFormat.EPS;
        JComboBox<?> imageFormatSelector = new JComboBox<>(ImageFormat.values());
        configurableImageComparisonPanel.add(imageFormatSelector, BorderLayout.NORTH);
        imageFormatSelector.setSelectedItem(startingImageFormat);
        imageFormatSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                ImageFormat format = (ImageFormat) itemEvent.getItem();
                imageComparisonPanel.setImageFormat(format);
                AbstractTest test = getTestCase();
                if (test != null) {
                    setTestCase(test);
                }
            }
        });

        imageComparisonPanel = new ImageComparisonPanel(startingImageFormat);
        configurableImageComparisonPanel.add(imageComparisonPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) throws Exception {
        new TestBrowser().setVisible(true);
    }

    public AbstractTest getTestCase() {
        return testCase;
    }

    public void setTestCase(AbstractTest test) {
        BufferedImage reference = test.getReference();
        imageComparisonPanel.setLeftComponent(new ImageDisplayPanel(reference, null));
        ImageDisplayPanel imageDisplayPanel;
        switch (imageComparisonPanel.getImageFormat()) {
            case EPS:
                imageDisplayPanel = new ImageDisplayPanel(null, test.getEPS());
                imageComparisonPanel.setRightComponent(imageDisplayPanel);
                break;
            case PDF:
                imageDisplayPanel = new ImageDisplayPanel(null, test.getPDF());
                imageComparisonPanel.setRightComponent(imageDisplayPanel);
                break;
            case SVG:
                imageDisplayPanel = new ImageDisplayPanel(null, test.getSVG());
                imageComparisonPanel.setRightComponent(imageDisplayPanel);
                break;
            default:
                throw new IllegalArgumentException("Unknown image format: " + imageComparisonPanel.getImageFormat());
        }
    }

    private enum ImageFormat {
        EPS("EPS"),
        PDF("PDF"),
        SVG("SVG");

        private final String name;

        ImageFormat(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static class ImageComparisonPanel extends Box {

        private final Box leftPanel;

        private final Box rightPanel;

        private ImageFormat imageFormat;

        private JComponent leftComponent;

        private JComponent rightComponent;

        public ImageComparisonPanel(ImageFormat imageFormat) {
            super(BoxLayout.PAGE_AXIS);

            this.imageFormat = imageFormat;

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setResizeWeight(0.5);
            add(splitPane);

            leftPanel = new Box(BoxLayout.PAGE_AXIS);
            leftPanel.add(new JLabel("Graphics2D"));
            splitPane.setTopComponent(leftPanel);

            rightPanel = new Box(BoxLayout.PAGE_AXIS);
            rightPanel.add(new JLabel(imageFormat.getName()));
            splitPane.setBottomComponent(rightPanel);
        }

        public void setLeftComponent(JComponent leftComponent) {
            if (this.leftComponent != null) {
                leftPanel.remove(this.leftComponent);
            }
            this.leftComponent = leftComponent;
            leftPanel.add(leftComponent);
            leftPanel.revalidate();
            leftPanel.repaint();
        }

        public void setRightComponent(JComponent rightComponent) {
            if (this.rightComponent != null) {
                rightPanel.remove(this.rightComponent);
            }
            this.rightComponent = rightComponent;
            rightPanel.add(rightComponent);
            rightPanel.revalidate();
            rightPanel.repaint();
        }

        public ImageFormat getImageFormat() {
            return imageFormat;
        }

        public void setImageFormat(ImageFormat imageFormat) {
            this.imageFormat = imageFormat;
            JLabel imageFormatLabel = (JLabel) rightPanel.getComponent(0);
            imageFormatLabel.setText(imageFormat.getName());
            imageFormatLabel.repaint();
        }
    }

    private static class ImageDisplayPanel extends JPanel {
        private final InputStream imageData;

        public ImageDisplayPanel(BufferedImage renderedImage, InputStream imageData) {
            super(new BorderLayout());
            this.imageData = imageData;
            if (renderedImage != null) {
                JLabel imageLabel = new JLabel(new ImageIcon(renderedImage));
                add(imageLabel, BorderLayout.CENTER);
            }
            JButton saveToFileButton = new JButton("Save as...");
            if (imageData == null) {
                saveToFileButton.setEnabled(false);
            }
            saveToFileButton.addActionListener(e -> {
                JFileChooser saveFileDialog = new JFileChooser();
                saveFileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
                saveFileDialog.setMultiSelectionEnabled(false);
                int userChoice = saveFileDialog.showSaveDialog(ImageDisplayPanel.this);
                if (userChoice != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File dest = saveFileDialog.getSelectedFile();
                FileOutputStream destStream = null;
                try {
                    destStream = new FileOutputStream(dest);
                    int imageDataChunk;
                    while ((imageDataChunk = ImageDisplayPanel.this.imageData.read()) != -1) {
                        destStream.write(imageDataChunk);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    if (destStream != null) {
                        try {
                            destStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            add(saveToFileButton, BorderLayout.SOUTH);
        }
    }
}
