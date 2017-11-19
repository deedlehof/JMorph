import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransitionFrame extends JFrame{

    private Grid grid1, grid2;
    private Grid transitionGrid;
    private final int buttonHeight = 50;

    private int seconds = 2;
    private int framesPerSecond = 30;

    public TransitionFrame(Grid _grid1, Grid _grid2){
        this.grid1 = _grid1;
        this.grid2 = _grid2;

        transitionGrid = new Grid(grid1);
        transitionGrid.setGridImmovable(true);

        JButton doTransitionBtn = new JButton("Morph");
        doTransitionBtn.setSize(transitionGrid.getWidth(), buttonHeight);

        doTransitionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transitionGrid.morphGrid(grid2.getPntList(), seconds, framesPerSecond);
            }
        });

        add(transitionGrid, BorderLayout.CENTER);
        add(doTransitionBtn, BorderLayout.SOUTH);

        setSize(transitionGrid.getWidth() + 10, transitionGrid.getHeight() + buttonHeight + 25);
        setVisible(true);
    }
}
