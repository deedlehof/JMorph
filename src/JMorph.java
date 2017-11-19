import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JMorph extends JFrame {
    private Container cont;
    private Grid leftGrid, rightGrid;
    private JPanel settings; //contains slider bar and buttons
    private JButton morph;
    private JSlider ctrlPts;
    private JMenuBar menu; //contain exit, restart, settings, etc.?
    private GridBagLayout layout;
    private JPanel screen;

    private GridPairController gridControl;

    public JMorph(){
        super("Morpher");
        cont = getContentPane();
        screen = new JPanel(); //will hold the components (grids, button panel, etc)
        setupMenuBar();
        setupUI();

        gridControl = new GridPairController(leftGrid, rightGrid);

        pack();
        setSize(screen.getPreferredSize());
        setVisible(true);
    }

    private void setupMenuBar()
    {
        menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(exit);
        /*
        * Other options:
        * save morph
        * load
        * ...?
        * */
        menu.add(file);

        this.setJMenuBar(menu);
    }

    private void setupUI()
    {
        layout = new GridBagLayout(); //gridBagLayout is flexible
        screen.setLayout(layout); //use to organize the main screen

        leftGrid = new Grid(10, 10);
        rightGrid = new Grid(10, 10);

        GridBagConstraints leftGridConst = new GridBagConstraints();
        leftGridConst.gridx = 0;
        leftGridConst.gridy = 0;
        leftGridConst.ipadx = leftGrid.getWidth(); //sets the "size" of the grid or how much space it can take up--need to customize based on number of ctrl pts
        leftGridConst.ipady = leftGrid.getHeight();
        leftGridConst.fill = GridBagConstraints.BOTH;
        leftGrid.setBackground(Color.BLUE);
        screen.add(leftGrid, leftGridConst);

        GridBagConstraints rightGridConst = new GridBagConstraints();
        rightGridConst.gridx = 2;
        rightGridConst.gridy = 0;
        rightGridConst.ipadx = leftGrid.getWidth();
        rightGridConst.ipady = leftGrid.getHeight();
        rightGridConst.fill = GridBagConstraints.BOTH;
        //rightGrid.setBackground(Color.GREEN);
        screen.add(rightGrid, rightGridConst);

        setupSettingsPanel();

        cont.add(screen, BorderLayout.CENTER);
    }

    private void setupSettingsPanel()
    {
        settings = new JPanel();

        morph = new JButton();
        morph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransitionFrame morphFrame = new TransitionFrame(leftGrid, rightGrid);
            }
        });

        ctrlPts = new JSlider(3, 25);
        ctrlPts.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gridControl.setGridResolution(ctrlPts.getValue(), ctrlPts.getValue());
            }
        });

        settings.add(morph);
        settings.add(ctrlPts);

        GridBagConstraints setConst = new GridBagConstraints();
        setConst.gridx = 1;
        setConst.gridy = 0;
        setConst.ipadx = 100;
        setConst.ipady = 500;
        setConst.fill = GridBagConstraints.VERTICAL;
        screen.add(settings, setConst);
    }

    public static void main(String args[]){
        JMorph morph = new JMorph();
        morph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
