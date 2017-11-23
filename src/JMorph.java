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
    private JPanel master, settings, settingsScreen, morphButtons, ctrlPtBar, frameEntry, setPictures, fpsEntry; //contains slider bar and buttons
    private JButton morph, leftImg, rightImg, preview, reset;
    private JSlider ctrlPts;
    private JMenuBar menu; //contain exit, restart, settings, etc.?
    private GridBagLayout layout;
    private JPanel screen, rightGridScrn, leftGridScrn;
    private JTextArea inputDuration, inputFPS;
    private JLabel durationDesc, ptsDesc, fpsDesc;
    private GridBagConstraints leftGridConst, rightGridConst;
    public final static double MAX_DIMENSION = 400; //current max allowable size for the grid

    private GridPairController gridControl;

    public JMorph(){
        super("Morpher");
        cont = getContentPane();
        master = new JPanel(); //holds everything
        screen = new JPanel(); //will hold the grids
        rightGridScrn = new JPanel();
        leftGridScrn = new JPanel();
        settingsScreen = new JPanel(); // holds settings
        leftGridConst = new GridBagConstraints();
        rightGridConst = new GridBagConstraints();

        setupMenuBar();
        setupUI();

        gridControl = new GridPairController(leftGrid, rightGrid);

        pack();
        Dimension frameSize = master.getPreferredSize();
        setSize(frameSize.width + 50, frameSize.height + 100);
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
        master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));

        layout = new GridBagLayout(); //gridBagLayout is flexible
        rightGridScrn.setLayout(layout); //use to organize the main screen
        leftGridScrn.setLayout(layout); //use to organize the main screen

        leftGrid = new Grid(10, 10);
        rightGrid = new Grid(10, 10);

        leftGridConst.gridx = 0;
        leftGridConst.gridy = 0;
        leftGridConst.ipadx = leftGrid.getWidth(); //sets the "size" of the grid or how much space it can take up--need to customize based on number of ctrl pts
        leftGridConst.ipady = leftGrid.getHeight();
        leftGridConst.fill = GridBagConstraints.BOTH;
        leftGridScrn.add(leftGrid, leftGridConst);

        rightGridScrn.setLayout(layout);
        rightGridConst.gridx = 2;
        rightGridConst.gridy = 0;
        rightGridConst.ipadx = rightGrid.getWidth();
        rightGridConst.ipady = rightGrid.getHeight();
        rightGridConst.fill = GridBagConstraints.BOTH;
        rightGridScrn.add(rightGrid, rightGridConst);

        screen.add(leftGridScrn);
        screen.add(rightGridScrn);

        master.add(screen);
        setupSettingsPanel();

        cont.add(master, BorderLayout.CENTER);
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

    private void setupSettingsPanel()
    {
        settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));

        morphButtons = new JPanel();
        morphButtons.setLayout(new BoxLayout(morphButtons, BoxLayout.X_AXIS));
        morph = new JButton("Start Morph");
        morph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check input duration to see how many frames to render
                leftGrid.resetGrid(); //TODO: implement this method
                rightGrid.resetGrid();
            }
        });

        preview = new JButton("Preview Morph");
        preview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*Start morph preview*/
                int fps = 30;
                if(isInteger(inputFPS.getText()))
                    fps = Integer.parseInt(inputFPS.getText());

                int duration = 2;
                if(isInteger(inputDuration.getText()))
                    duration = Integer.parseInt(inputDuration.getText());

                TransitionFrame morphFrame = new TransitionFrame(leftGrid, rightGrid, duration, fps);
            }
        });
        morphButtons.add(morph);
        morphButtons.add(preview);

        frameEntry = new JPanel();
        frameEntry.setLayout(new BoxLayout(frameEntry, BoxLayout.X_AXIS));
        durationDesc = new JLabel("Enter the number of seconds:");
        inputDuration = new JTextArea("2");
        inputDuration.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        frameEntry.add(durationDesc);
        frameEntry.add(inputDuration);

        fpsEntry = new JPanel();
        fpsEntry.setLayout(new BoxLayout(fpsEntry, BoxLayout.X_AXIS));
        fpsDesc = new JLabel("Enter the number of frames per second: ");
        inputFPS = new JTextArea("30");
        inputFPS.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        fpsEntry.add(fpsDesc);
        fpsEntry.add(inputFPS);

        ctrlPtBar = new JPanel();
        ctrlPtBar.setLayout(new BoxLayout(ctrlPtBar, BoxLayout.X_AXIS));
        ptsDesc = new JLabel("Select control point resolution: ");
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
        ctrlPtBar.add(ptsDesc);
        ctrlPtBar.add(ctrlPts);

        setPictures = new JPanel();
        setPictures.setLayout(new BoxLayout(setPictures, BoxLayout.X_AXIS));
        leftImg = new JButton("Set left image"); //set image in left grid
        leftImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File path = getPath();
                if(path != null)
                {
                    setImage(leftGrid, path.getAbsolutePath());
                }
            }
        });

        rightImg = new JButton("Set right image"); //set image in right grid
        rightImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*Popup file selector, get data from it*/
                File path = getPath();
                if(path != null) {
                    setImage(rightGrid, path.getAbsolutePath());
                }
            }
        });
        setPictures.add(leftImg);
        setPictures.add(rightImg);

        reset = new JButton("Reset Morph");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //popup confirming reset choice
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the control points?");
                if(confirm == JOptionPane.YES_OPTION)
                {
                    gridControl.resetGrids();
                }
            }
        });

        settings.add(setPictures);
        settings.add(morphButtons);
        settings.add(ctrlPtBar);
        settings.add(frameEntry);
        settings.add(fpsEntry);
        settings.add(reset);

        settingsScreen.add(settings);
        Dimension prefSize = new Dimension();
        prefSize.width = settings.getPreferredSize().width;
        prefSize.height = 200;
        settingsScreen.setPreferredSize(prefSize);
        master.add(settingsScreen);
    }

    /*Popup file selector, get data from it*/
    private File getPath()
    {
        JFileChooser jfc = new JFileChooser();
        jfc.showDialog(null, "Select a file");
        jfc.setVisible(true);
        return jfc.getSelectedFile();
    }

    //Set the image in a particular grid
    public void setImage(Grid g, String path)
    {
        Image img = new ImageIcon(path).getImage();
        Dimension scaled = scaleImg(img, g);
        img = img.getScaledInstance((int)scaled.getWidth(), (int)scaled.getHeight(), Image.SCALE_SMOOTH);

        MediaTracker tracker = new MediaTracker(new Component() {});
        tracker.addImage(img, 0);

        try { tracker.waitForID(0); }
        catch(InterruptedException e){ System.out.println("exception");}

        BufferedImage buf = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = buf.createGraphics();
        bg.drawImage(img, g.getX(), g.getY(), null);
        g.setImg(buf);
        bg.dispose();
    }

    //scale but maintain aspect ratio
    private Dimension scaleImg(Image i, Grid g)
    {
        Dimension d;
        double widthScale = MAX_DIMENSION / (double)i.getWidth(null);
        double heightScale = MAX_DIMENSION / (double)i.getHeight(null);
        double scale = Math.min(widthScale, heightScale);
        d = new Dimension((int)(i.getWidth(null) * scale), (int)(i.getHeight(null) * scale));
        return d;
    }

    public static void main(String args[]){
        JMorph morph = new JMorph();
        morph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
