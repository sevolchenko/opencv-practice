package ru.vsu.cs.volchenko.frame_main;

import ru.vsu.cs.volchenko.image_processing.ImageProcessorCanny;
import ru.vsu.cs.volchenko.image_processing.ImageProcessorContext;
import ru.vsu.cs.volchenko.utils.ImageUtils;
import ru.vsu.cs.volchenko.utils.SwingUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FrameMain extends JFrame {

    private JScrollPane panelMain;
    private JSlider sliderThresholdMin;
    private JSlider sliderThresholdMax;
    private JLabel labelImg;
    private JButton buttonUpload;
    private JButton buttonPrev;
    private JButton buttonNext;
    private JPanel panelButtons;
    private JLabel labelDescription;
    private JButton buttonSave;
    private JButton buttonExecute;
    private JPanel panelSettings;
    private JTextField textFieldSizeX;
    private JTextField textFieldSizeY;
    private JTextField textFieldSigmaX;
    private JTextField textFieldSigmaY;
    private JCheckBox checkBoxL2Gradient;
    private JComboBox comboBoxApertureSize;
    private JTextField textFieldValueOfThresholdMin;
    private JTextField textFieldValueOfThresholdMax;
    private JComboBox comboBoxMethod;
    private JLabel labelObjectSrc;
    private JPanel panelObjects;
    private JLabel labelObjectsYellow;
    private JLabel labelObjectsBlue;
    private JLabel labelObjectsRed;
    private JLabel labelObjectsGreen;
    private JLabel labelColor;
    private JButton buttonPrevObject;
    private JButton buttonNextObject;
    private JButton buttonSaveObject;
    private JButton buttonHideShowSettings;
    private JSlider sliderColorThreshold;
    private JTextField textFieldValueOfColorThreshold;

    private final JFileChooser fileChooserOpen;
    private final JFileChooser fileChooserSave;

    BufferedImage sourceImage = null;

    ImageProcessorCanny imageProcessor;
    Integer currentImageIndex = null;
    Integer currentObjectIndex = null;
    ImageProcessorContext processorContext = null;

    public FrameMain() {

        this.setTitle("ImageProcessor");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fileChooserOpen = new JFileChooser();
        fileChooserSave = new JFileChooser();
        fileChooserOpen.setCurrentDirectory(new File("./images"));
        fileChooserSave.setCurrentDirectory(new File("."));
        FileFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp", "ico");
        fileChooserOpen.addChoosableFileFilter(filter);
        fileChooserSave.addChoosableFileFilter(filter);

        fileChooserSave.setAcceptAllFileFilterUsed(false);
        fileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserSave.setApproveButtonText("Save");

        comboBoxMethod.setSelectedIndex(3);
        panelSettings.setVisible(false);

        imageProcessor = new ImageProcessorCanny();

        updateView();
        this.pack();
        this.setLocationRelativeTo(null);

        buttonUpload.addActionListener(e -> {
            try {
                if (fileChooserOpen.showOpenDialog(panelMain) == JFileChooser.APPROVE_OPTION) {
                    sourceImage = ImageUtils.readBI(fileChooserOpen.getSelectedFile().getPath());
                    currentImageIndex = null;
                    currentObjectIndex = null;
                    processorContext = null;
                    panelSettings.setVisible(true);

                    updateView();
                    this.pack();
                    this.setLocationRelativeTo(null);
                }
            } catch (IOException ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });

        buttonSave.addActionListener(e -> {
            try {
                if (fileChooserSave.showSaveDialog(panelMain) == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooserSave.getSelectedFile().getPath();
                    ImageIO.write(getCurrentImage(), "jpg", new File(path));
                }
            } catch (IOException ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });

        buttonSaveObject.addActionListener(e -> {
            try {
                if (fileChooserSave.showSaveDialog(panelMain) == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooserSave.getSelectedFile().getPath();
                    ImageIO.write(processorContext.listOfColors.get(currentObjectIndex).source, "jpg", new File(path));
                }
            } catch (IOException ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });

        buttonHideShowSettings.addActionListener(e -> {
            panelSettings.setVisible(!panelSettings.isVisible());

            updateView();
        });

        buttonNext.addActionListener(e -> {
            if (processorContext != null) {
                if (currentImageIndex < processorContext.listOfImages.size() - 1) {
                    currentImageIndex++;
                }
                updateView();
            }
        });

        buttonPrev.addActionListener(e -> {
            if (processorContext != null) {
                if (currentImageIndex > -1) {
                    currentImageIndex--;
                }
                updateView();
            }
        });

        buttonNextObject.addActionListener(e -> {
            if (processorContext != null) {
                if (currentObjectIndex < processorContext.listOfColors.size() - 1) {
                    currentObjectIndex++;
                }
                updateView();
            }
        });

        buttonPrevObject.addActionListener(e -> {
            if (processorContext != null) {
                if (currentObjectIndex > 0) {
                    currentObjectIndex--;
                }
                updateView();
            }
        });

        buttonExecute.addActionListener(e -> {
            try {

                int kSizeW = Integer.parseInt(textFieldSizeX.getText());
                int kSizeH = Integer.parseInt(textFieldSizeY.getText());

                int sigmaX = Integer.parseInt(textFieldSigmaX.getText());
                int sigmaY = Integer.parseInt(textFieldSigmaY.getText());

                int thresholdMin = sliderThresholdMin.getValue();
                int thresholdMax = sliderThresholdMax.getValue();

                int apertureSize = Integer.parseInt((String) comboBoxApertureSize.getSelectedItem());
                boolean l2Gradient = checkBoxL2Gradient.isSelected();

                int method = comboBoxMethod.getSelectedIndex() + 1;

                int colorThreshold = sliderColorThreshold.getValue();

                processorContext = imageProcessor.process(sourceImage,
                        kSizeW, kSizeH, sigmaX, sigmaY,
                        thresholdMin, thresholdMax,
                        apertureSize, l2Gradient,
                        method, colorThreshold);

                Map<ImageUtils.ObjectColor, Integer> mapOfCounts = Arrays.stream(ImageUtils.ObjectColor.values())
                        .collect(Collectors.toMap(objectColor -> objectColor,
                                objectColor -> Long.valueOf(processorContext.listOfColors.stream()
                                .filter(imagesWithColor -> imagesWithColor.color == objectColor)
                                .count())
                                        .intValue()));

                String colorInformation = "Colors detected:" +
                        mapOfCounts.entrySet().stream()
                                .sorted((entry1, entry2) -> entry2.getValue() - entry1.getValue())
                                .map(entry -> "\n" + entry.getKey().toString() + ": " + entry.getValue())
                                .collect(Collectors.joining());


                SwingUtils.showInfoMessageBox("Execution time: " + processorContext.getPassedTime() + " millis" +
                        "\nContours detected: " + processorContext.listOfColors.size() +
                        "\n\n" + colorInformation + "\n", "Execution finished");

                if (currentImageIndex == null) {
                    currentImageIndex = -1;
                }

                currentObjectIndex = 0;

                updateView();
                this.setLocationRelativeTo(null);
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }

        });

        sliderThresholdMin.addChangeListener(e -> updateView());

        sliderThresholdMax.addChangeListener(e -> updateView());

        sliderColorThreshold.addChangeListener(e -> updateView());

        textFieldValueOfThresholdMin.addActionListener(e -> {
            sliderThresholdMin.setValue(Integer.parseInt(textFieldValueOfThresholdMin.getText()));
            updateView();
        });

        textFieldValueOfThresholdMax.addActionListener(e -> {
            sliderThresholdMax.setValue(Integer.parseInt(textFieldValueOfThresholdMax.getText()));
            updateView();
        });

        textFieldValueOfColorThreshold.addActionListener(e -> {
            sliderColorThreshold.setValue(Integer.parseInt(textFieldValueOfColorThreshold.getText()));
            updateView();
        });
    }

    private BufferedImage getCurrentImage() {
        if (currentImageIndex == null || currentImageIndex == -1) {
            return sourceImage;
        } else {
            return processorContext.listOfImages.get(currentImageIndex).img;
        }
    }

    private void updateView() {

        if (currentImageIndex != null) {
            panelButtons.setVisible(true);
            panelObjects.setVisible(true);
            if (currentImageIndex != -1) {
                labelDescription.setText(processorContext.listOfImages.get(currentImageIndex).description);
            } else {
                labelDescription.setText("Source image");
            }

            labelObjectSrc.setIcon(new ImageIcon(processorContext.listOfColors.get(currentObjectIndex).source));
            labelColor.setText("Object " + currentObjectIndex + ": " + processorContext.listOfColors.get(currentObjectIndex).color.toString());

            if (processorContext.listOfColors.get(currentObjectIndex).greenFilter != null) {
                labelObjectsGreen.setIcon(new ImageIcon(processorContext.listOfColors.get(currentObjectIndex).greenFilter));
                labelObjectsYellow.setIcon(new ImageIcon(processorContext.listOfColors.get(currentObjectIndex).yellowFilter));
                labelObjectsBlue.setIcon(new ImageIcon(processorContext.listOfColors.get(currentObjectIndex).blueFilter));
                labelObjectsRed.setIcon(new ImageIcon(processorContext.listOfColors.get(currentObjectIndex).redFilter));
            }
        } else {
            panelButtons.setVisible(false);
            panelObjects.setVisible(false);
        }

        boolean currentImageExist = getCurrentImage() != null;
        buttonExecute.setVisible(currentImageExist);
        buttonSave.setVisible(currentImageExist);

        if (currentImageExist) {
            labelImg.setIcon(new ImageIcon(getCurrentImage()));
        }

        textFieldValueOfThresholdMin.setText(String.valueOf(sliderThresholdMin.getValue()));
        textFieldValueOfThresholdMax.setText(String.valueOf(sliderThresholdMax.getValue()));
        textFieldValueOfColorThreshold.setText(String.valueOf(sliderColorThreshold.getValue()));

        if (panelSettings.isVisible()) {
            buttonHideShowSettings.setText("Hide settings");
        } else {
            buttonHideShowSettings.setText("Show settings");
        }
    }
}
