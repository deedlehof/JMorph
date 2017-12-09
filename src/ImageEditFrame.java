import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ImageEditFrame extends JFrame{

    private ImageEditor editor;
    private String[] imageOptionsList = {
            "None",
            "Sharpen",
            "Blur",
            "Edge Detection"
    };
    private JComboBox imageOptions;
    private JButton resetBtn, confirmBtn, cancelBtn;
    private JLabel brightLabel, filterLabel;
    private JSlider brightnessSlide;
    private int maxBrightness = 100;

    public ImageEditFrame(BufferedImage _editImage, ActionListener confirmChange){
        editor = new ImageEditor(_editImage);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcs = new GridBagConstraints();

        filterLabel = new JLabel("Filter Options: ");

        imageOptions = new JComboBox(imageOptionsList);
        imageOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chosenOption = imageOptions.getSelectedItem().toString();
                switch (chosenOption) {
                    case "None":
                        editor.resetImage();
                        break;
                    case "Sharpen":
                        editor.sharpenImage();
                        break;
                    case "Blur":
                        editor.blurImage();
                        break;
                    case "Edge Detection":
                        editor.edgeDetect();
                        break;
                }
                brightnessSlide.setValue(maxBrightness / 2);
            }
        });

        brightLabel = new JLabel("Brightness: ");

        brightnessSlide = new JSlider(0, maxBrightness, maxBrightness/2);
        brightnessSlide.setMinorTickSpacing(5);
        brightnessSlide.setMajorTickSpacing(10);
        brightnessSlide.setPaintTicks(true);
        brightnessSlide.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int brightVal = brightnessSlide.getValue();
                editor.changeBrightness(brightVal/(maxBrightness/2.0));
            }
        });

        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.resetImage();
                brightnessSlide.setValue(maxBrightness/2);
                imageOptions.setSelectedIndex(0);
            }
        });

        confirmBtn = new JButton("Save");
        confirmBtn.addActionListener(confirmChange);
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        gbcs.fill = GridBagConstraints.BOTH;
        gbcs.weightx = 1;
        gbcs.weighty = 0;

        gbcs.gridx = 0;
        gbcs.gridy = 0;
        gbcs.gridwidth = 1;
        buttonPanel.add(filterLabel, gbcs);
        gbcs.gridx = 1;
        gbcs.gridwidth = 2;
        buttonPanel.add(imageOptions, gbcs);

        gbcs.gridx = 0;
        gbcs.gridy = 1;
        gbcs.gridwidth = 1;
        buttonPanel.add(brightLabel, gbcs);
        gbcs.gridx = 1;
        gbcs.gridwidth = 2;
        buttonPanel.add(brightnessSlide, gbcs);

        gbcs.gridx = 0;
        gbcs.gridy = 2;
        gbcs.gridwidth = 1;
        buttonPanel.add(cancelBtn, gbcs);
        gbcs.gridx = 1;
        buttonPanel.add(confirmBtn, gbcs);
        gbcs.gridx = 2;
        buttonPanel.add(resetBtn, gbcs);

        add(editor, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setVisible(false);
    }

    public BufferedImage getImage(){
        return editor.getFilteredImage();
    }

    public void setImage(BufferedImage img){
        editor.setImage(img);
        pack();
        setSize(new Dimension(img.getWidth(), getHeight()));
    }
}
