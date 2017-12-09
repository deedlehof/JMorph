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
            "Sharpen",
            "Blur",
            "Edge Detection"
    };
    private JComboBox imageOptions;
    private JButton applyBtn, resetBtn, applyBrightBtn, confirmBtn, cancelBtn;
    private JLabel brightLabel, filterLabel;
    private JSlider brightnessSlide;
    private int maxBrightness = 100;

    public ImageEditFrame(BufferedImage _editImage, ActionListener confirmChange){
        editor = new ImageEditor(_editImage);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3, 1, 1));


        filterLabel = new JLabel("Filter Options: ");

        imageOptions = new JComboBox(imageOptionsList);

        applyBtn = new JButton("Apply");
        applyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chosenOption = imageOptions.getSelectedItem().toString();
                if(chosenOption == "Sharpen"){
                    editor.sharpenImage();
                } else if(chosenOption == "Blur"){
                    editor.blurImage();
                } else if (chosenOption == "Edge Detection"){
                    editor.edgeDetect();
                }
                brightnessSlide.setValue(maxBrightness/2);
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
        applyBrightBtn = new JButton();

        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.resetImage();
                brightnessSlide.setValue(maxBrightness/2);
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

        buttonPanel.add(filterLabel);
        buttonPanel.add(imageOptions);
        buttonPanel.add(applyBtn);

        buttonPanel.add(brightLabel);
        buttonPanel.add(brightnessSlide);
        buttonPanel.add(applyBrightBtn);

        buttonPanel.add(cancelBtn);
        buttonPanel.add(confirmBtn);
        buttonPanel.add(resetBtn);

        add(editor, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(false);
    }

    public BufferedImage getImage(){
        return editor.getFilteredImage();
    }

    public void setImage(BufferedImage img){
        editor.setImage(img);
        pack();
    }
}
