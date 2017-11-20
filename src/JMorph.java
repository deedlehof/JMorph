import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class JMorph extends JFrame {
    private Container cont;
    private Grid leftGrid, rightGrid;
    private JPanel settings; //contains slider bar and buttons
    private JButton morph, leftImg, rightImg, preview;
    private JSlider ctrlPts;
    private JMenuBar menu; //contain exit, restart, settings, etc.?
    private GridBagLayout layout;
    private JPanel screen;
    JTextArea inputDuration;
    JLabel durationDesc, ptsDesc;

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
        settings.setLayout(new GridLayout(6,2));

        morph = new JButton("Start Morph");
        morph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransitionFrame morphFrame = new TransitionFrame(leftGrid, rightGrid);
            }
        });

        preview = new JButton("Preview Morph");
        preview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*Start morph preview*/
            }
        });

        durationDesc = new JLabel("Enter the duration of the morph (seconds):");
        inputDuration = new JTextArea("0");
        inputDuration.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        ptsDesc = new JLabel("Select control point resolution:");
        ctrlPts = new JSlider(3, 25);
        ctrlPts.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gridControl.setGridResolution(ctrlPts.getValue(), ctrlPts.getValue());
            }
        });
        ctrlPts.setMajorTickSpacing(10);
        ctrlPts.setMinorTickSpacing(1);
        ctrlPts.setPaintTicks(true);

        leftImg = new JButton("Set left image"); //set image in left grid
        leftImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(".");
                jfc.showDialog(null, "Select a file");
                jfc.setVisible(true);
                File path = jfc.getSelectedFile();
                if(path != null)
                {
                    System.out.println("File selected: " + path.getPath());
                    setImage(leftGrid, path.getName());//temp
                }
                //setImage(leftGrid, "turkey.JPG");
                /*Popup file selector, get data from it*/
            }
        });

        rightImg = new JButton("Set right image"); //set image in right grid
        rightImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*Popup file selector, get data from it*/
                JFileChooser jfc = new JFileChooser(".");
                jfc.showDialog(null, "Select a file");
                jfc.setVisible(true);
                File path = jfc.getSelectedFile();
                if(path != null) {
                    System.out.println("File selected: " + path.getName());
                    setImage(rightGrid, path.getName());//temp
                }
                //setImage(rightGrid, "turkey.JPG");
            }
        });

        settings.add(morph);
        settings.add(preview);
        settings.add(durationDesc);
        settings.add(inputDuration);
        settings.add(ptsDesc);
        settings.add(ctrlPts);
        settings.add(leftImg);
        settings.add(rightImg);

        GridBagConstraints setConst = new GridBagConstraints();
        setConst.gridx = 0;
        setConst.gridy = 2;
        setConst.ipadx = 50;
        setConst.ipady = 0;
        setConst.fill = GridBagConstraints.CENTER;
        screen.add(settings, setConst);
    }

    //Set the image in a particular grid
    public void setImage(Grid g, String path)
    {
        Image img = new ImageIcon(this.getClass().getResource(path)).getImage();

        MediaTracker tracker = new MediaTracker(new Component() {});
        tracker.addImage(img, 0);

        try { tracker.waitForID(0); }
        catch(InterruptedException e){ System.out.println("exception");}

        System.out.println(img.getWidth(this) + " " + img.getHeight(this));
        BufferedImage buf = new BufferedImage(g.getWidth(), g.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = buf.createGraphics();
        bg.drawImage(img, g.getX(), g.getY(), null);
        g.setImg(buf);
    }

    public static void main(String args[]){
        JMorph morph = new JMorph();
        morph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
