import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransitionFrame extends JFrame{

    private Grid grid1, grid2;
    private int guiHeight = 50;
    private final int addedPadding = 15;

    private int seconds = 2;
    private int framesPerSecond = 30;

    public TransitionFrame(Grid _grid1, Grid _grid2, int duration, int fps){
        this.grid1 = _grid1;
        this.grid2 = _grid2;
        this.seconds = duration;
        this.framesPerSecond = fps;

        TransitionPanel transition = new TransitionPanel(grid1, grid2);

        JButton doTransitionBtn = new JButton("Morph");
        doTransitionBtn.setSize(transition.getWidth(), doTransitionBtn.getHeight());

        doTransitionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transition.morph(grid2, seconds, framesPerSecond);
            }
        });

        add(transition, BorderLayout.CENTER);
        add(doTransitionBtn, BorderLayout.SOUTH);

        setSize(transition.getWidth() + addedPadding, transition.getHeight() + guiHeight + 25);
        setVisible(true);
    }
}
