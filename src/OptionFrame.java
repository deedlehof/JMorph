import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionFrame extends JFrame {

    private int gridWidth = 10, gridHeight = 10;
    private boolean drawTriangles = true;

    private int seconds = 1, frames = 30;

    private JLabel customGridResLabel, widthInLabel, heightInLabel, gridOptionsLabel, drawTriangleLabel;
    private JTextField widthInField, heightInField;
    private JCheckBox drawTrianglesBox;

    private JLabel morphLabel, secondsLabel, framesLabel;
    private JTextField secondsField, framesField;

    private JButton confirmBtn, cancelBtn;

    public OptionFrame(ActionListener confirmAction){
        super("Options");

        JTabbedPane optionPane = new JTabbedPane();

        JPanel gridOptions = new JPanel();
        optionPane.addTab("Grid", gridOptions);

        JPanel morphOptions = new JPanel();
        optionPane.addTab("Morph", morphOptions);

        JPanel confirmPanel = new JPanel();


        //Grid Options======================================================
        gridOptions.setLayout(new GridBagLayout());
        GridBagConstraints gridConstraints = new GridBagConstraints();

        customGridResLabel = new JLabel("Custom Frame Resolution: ");
        customGridResLabel.setFont(new Font("serif", Font.BOLD, 18));
        widthInLabel = new JLabel("Width: ");
        widthInField = new JTextField(Integer.toString(gridWidth));
        heightInLabel = new JLabel("Height: ");
        heightInField = new JTextField(Integer.toString(gridHeight));
        gridOptionsLabel = new JLabel("Grid Drawing: ");
        gridOptionsLabel.setFont(new Font("serif", Font.BOLD, 18));
        drawTriangleLabel = new JLabel("Draw Triangles: ");
        drawTrianglesBox = new JCheckBox();
        drawTrianglesBox.setSelected(drawTriangles);
        
        gridConstraints.fill = GridBagConstraints.BOTH;
        gridConstraints.weightx = 1;
        gridConstraints.weighty = .2;
        
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 0;
        gridConstraints.gridwidth = 3;
        gridOptions.add(customGridResLabel, gridConstraints);
        
        gridConstraints.gridy = 1;
        gridConstraints.gridwidth = 1;
        gridConstraints.weightx = .2;
        gridOptions.add(widthInLabel, gridConstraints);
        gridConstraints.gridx = 1;
        gridConstraints.gridwidth = 2;
        gridConstraints.weightx = 1;
        gridOptions.add(widthInField, gridConstraints);

        gridConstraints.gridx = 0;
        gridConstraints.gridy = 2;
        gridConstraints.gridwidth = 1;
        gridConstraints.weightx = .2;
        gridOptions.add(heightInLabel, gridConstraints);
        gridConstraints.gridx = 1;
        gridConstraints.gridwidth = 2;
        gridConstraints.weightx = 1;
        gridOptions.add(heightInField, gridConstraints);

        gridConstraints.gridx = 0;
        gridConstraints.gridy = 3;
        gridConstraints.gridwidth = 3;
        gridConstraints.weightx = 1;
        gridOptions.add(gridOptionsLabel, gridConstraints);

        gridConstraints.gridx = 0;
        gridConstraints.gridy = 4;
        gridConstraints.gridwidth = 1;
        gridConstraints.weightx = .2;
        gridOptions.add(drawTriangleLabel, gridConstraints);
        gridConstraints.gridx = 1;
        gridConstraints.gridwidth = 2;
        gridConstraints.weightx = 1;
        gridOptions.add(drawTrianglesBox, gridConstraints);

        gridOptions.setPreferredSize(new Dimension(500, 170));

        //Morph Options======================================================
        morphOptions.setLayout(new GridBagLayout());
        GridBagConstraints morphConstraints = new GridBagConstraints();

        morphLabel = new JLabel("Custom Morph Options: ");
        morphLabel.setFont(new Font("serif", Font.BOLD, 18));
        secondsLabel = new JLabel("Warp time (seconds): ");
        secondsField = new JTextField(Integer.toString(seconds));
        framesLabel = new JLabel("Frames per second: ");
        framesField = new JTextField(Integer.toString(frames));

        morphConstraints.fill = GridBagConstraints.BOTH;
        morphConstraints.weighty = .1;

        morphConstraints.gridx = 0;
        morphConstraints.gridy = 0;
        morphConstraints.weightx = 1;
        morphConstraints.gridwidth = 3;
        morphOptions.add(morphLabel, morphConstraints);

        morphConstraints.gridy = 1;
        morphConstraints.gridwidth = 1;
        morphConstraints.weightx = .2;
        morphOptions.add(secondsLabel, morphConstraints);
        morphConstraints.gridx = 1;
        morphConstraints.gridwidth = 2;
        morphConstraints.weightx = 1;
        morphOptions.add(secondsField, morphConstraints);

        morphConstraints.gridx = 0;
        morphConstraints.gridy = 2;
        morphConstraints.gridwidth = 1;
        morphConstraints.weightx = .2;
        morphOptions.add(framesLabel, morphConstraints);
        morphConstraints.gridx = 1;
        morphConstraints.gridwidth = 2;
        morphConstraints.weightx = 1;
        morphOptions.add(framesField, morphConstraints);

        morphOptions.setPreferredSize(new Dimension(500, 100));

        //Confirm Options======================================================

        confirmPanel.setLayout(new GridLayout(1, 2, 1, 1));

        cancelBtn = new JButton("Cancel");
        confirmBtn = new JButton("Confirm");

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        confirmBtn.addActionListener(confirmAction);
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        confirmPanel.add(cancelBtn);
        confirmPanel.add(confirmBtn);

        //======================================================
        add(optionPane, BorderLayout.CENTER);
        add(confirmPanel, BorderLayout.SOUTH);
        pack();
        setVisible(false);
    }

    private boolean isInteger(String str) {
        int size = str.length();

        for (int i = 0; i < size; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return size > 0;
    }

    public void setGridResolution(int width, int height){
        this.gridWidth = width;
        this.gridHeight = height;

        widthInField.setText(String.valueOf(width));
        heightInField.setText(String.valueOf(height));
    }

    public int getGridWidth(){
        if(isInteger(widthInField.getText()))
            gridWidth = Integer.parseInt(widthInField.getText());
        else
            widthInField.setText(Integer.toString(gridWidth));

        return gridWidth;
    }

    public int getGridHeight(){
        if(isInteger(heightInField.getText()))
            gridHeight = Integer.parseInt(heightInField.getText());
        else
            heightInField.setText(Integer.toString(gridHeight));

        return gridHeight;
    }

    public boolean drawTriangles(){
        return drawTrianglesBox.isSelected();
    }

    public int getSeconds(){
        if(isInteger(secondsField.getText()))
            seconds = Integer.parseInt(secondsField.getText());
        else
            secondsField.setText(Integer.toString(seconds));

        return seconds;
    }

    public int getFPS(){
        if(isInteger(framesField.getText()))
            frames = Integer.parseInt(framesField.getText());
        else
            framesField.setText(Integer.toString(frames));

        return frames;
    }
}
