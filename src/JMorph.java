import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class JMorph extends JFrame {
    private Container cont;
    private Grid leftGrid, rightGrid;
    private JPanel master, settings, settingsScreen, morphButtons, ctrlPtBar, setPictures; //contains slider bar and buttons
    private JButton morph, leftImg, rightImg, preview;
    private JComboBox ctrlPts;
    private JMenuBar menu; //contain exit, restart, settings, etc.?
    private GridBagLayout layout;
    private JPanel screen, rightGridScrn, leftGridScrn;
    private JLabel ptsDesc;
    private GridBagConstraints leftGridConst, rightGridConst;
    public final static int MAX_DIMENSION = 600; //current max allowable size for the grid

    private String saveFileName = null;

    private GridPairController gridControl;
    private OptionFrame options;

    public JMorph(){
        super("Morpher");

        cont = getContentPane();
        master = new JPanel(); //holds everything
        screen = new JPanel(); //holds grid panels
        rightGridScrn = new JPanel();
        leftGridScrn = new JPanel();
        settingsScreen = new JPanel(); // holds settings
        leftGridConst = new GridBagConstraints();
        rightGridConst = new GridBagConstraints();

        setupMenuBar();
        setupUI();

        gridControl = new GridPairController(leftGrid, rightGrid);

        ActionListener applySettingsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applySettings(options);
            }
        };
        options = new OptionFrame(applySettingsListener);

        pack();
        gridControl.resetGrids();
        Dimension frameSize = master.getPreferredSize();
        setSize(frameSize.width + 50, frameSize.height + 100);
        setVisible(true);
    }

    private void applySettings(OptionFrame settings){
        int gWidth = settings.getGridWidth();
        int gHeight = settings.getGridHeight();
        gridControl.setGridResolution(gWidth, gHeight);
        gridControl.drawTriangles(settings.drawTriangles());
    }

    private void resetGrids(){
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the control points?");
        if(confirm == JOptionPane.YES_OPTION) {
            gridControl.resetGrids();
        }
    }

    private void setupMenuBar()
    {
        menu = new JMenuBar();
        JMenu file = new JMenu("File");

        JMenuItem saveMenu = new JMenuItem("Save");
        saveMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(saveFileName == null){
                    File path = getApprovedPath(new String[]{"grid"});
                    if (path != null){
                        String absolutePath = path.getAbsolutePath();
                        saveFileName = correctFileExtension(absolutePath, "grid");
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid file. Grid did not save.");
                        return;
                    }
                }

                saveGrids(saveFileName);

            }
        });
        file.add(saveMenu);

        JMenuItem saveAsMenu = new JMenuItem("Save As");
        saveAsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File path = getApprovedPath(new String[]{"grid"});
                if (path != null){
                    String absolutePath = path.getAbsolutePath();
                    saveFileName = correctFileExtension(absolutePath, "grid");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid file!");
                    return;
                }
                saveGrids(saveFileName);
            }
        });
        file.add(saveAsMenu);

        JMenuItem loadMenu = new JMenuItem("Load");
        loadMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File path = getApprovedPath(new String[]{"grid"});
                if (path != null){
                    saveFileName = path.getAbsolutePath();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid file!");
                    return;
                }
                loadGrids(saveFileName);
            }
        });
        file.add(loadMenu);

        JMenuItem resetMenu = new JMenuItem("Reset Grids");
        resetMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGrids();
            }
        });
        file.add(resetMenu);

        JMenuItem settingMenu = new JMenuItem("Settings");
        settingMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                options.setVisible(true);
            }
        });
        file.add(settingMenu);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(exit);

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

        //leftGrid.setBorder(new BevelBorder(BevelBorder.LOWERED));

        leftGridConst.gridx = 0;
        leftGridConst.gridy = 0;
        leftGridConst.ipadx = MAX_DIMENSION;
        leftGridConst.ipady = MAX_DIMENSION;
        leftGridConst.fill = GridBagConstraints.BOTH;
        leftGridScrn.add(leftGrid, leftGridConst);

        rightGridScrn.setLayout(layout);
        rightGridConst.gridx = 2;
        rightGridConst.gridy = 0;
        rightGridConst.ipadx = MAX_DIMENSION;
        rightGridConst.ipady = MAX_DIMENSION;
        rightGridConst.fill = GridBagConstraints.BOTH;
        rightGridScrn.add(rightGrid, rightGridConst);

        screen.add(leftGridScrn);
        screen.add(rightGridScrn);

        master.add(screen);
        setupSettingsPanel();

        cont.add(master, BorderLayout.CENTER);
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
                leftGrid.resetGrid();
                rightGrid.resetGrid();
            }
        });

        preview = new JButton("Preview Morph");
        preview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*Start morph preview*/
                int duration = options.getSeconds();
                int fps = options.getFPS();

                TransitionFrame morphFrame = new TransitionFrame(leftGrid, rightGrid, duration, fps);
            }
        });
        morphButtons.add(morph);
        morphButtons.add(preview);

        ctrlPtBar = new JPanel();
        ctrlPtBar.setLayout(new BoxLayout(ctrlPtBar, BoxLayout.X_AXIS));
        ptsDesc = new JLabel("Grid Resolution: ");
        ctrlPts = new JComboBox(new String[]{"5X5", "10X10", "20X20"});
        ctrlPts.setSelectedIndex(1);
        ctrlPts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String)ctrlPts.getSelectedItem();
                switch (s){
                    case "5X5":
                        options.setGridResolution(5, 5);
                        gridControl.setGridResolution(5, 5);
                        break;
                    case "10X10":
                        options.setGridResolution(10, 10);
                        gridControl.setGridResolution(10, 10);
                        break;
                    case "20X20":
                        options.setGridResolution(20, 20);
                        gridControl.setGridResolution(20, 20);
                        break;
                }
            }
        });

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
                    BufferedImage leftImg = getBufferedImage(leftGrid, path.getAbsolutePath());
                    gridControl.setImage1(leftImg);
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
                    BufferedImage rightImg = getBufferedImage(rightGrid, path.getAbsolutePath());
                    gridControl.setImage2(rightImg);
                }
            }
        });
        setPictures.add(leftImg);
        setPictures.add(rightImg);

        JPanel editImage = new JPanel();
        editImage.setLayout(new BoxLayout(editImage, BoxLayout.X_AXIS));
        JButton editLeft = new JButton("Edit Left");
        editLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(leftGrid.getImg() == null){
                    JOptionPane.showMessageDialog(null, "No image to edit.");
                    return;
                }
                gridControl.editImage1();
            }
        });
        JButton editRight = new JButton("Edit Right");
        editRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(rightGrid.getImg() == null){
                    JOptionPane.showMessageDialog(null, "No image to edit.");
                    return;
                }
                gridControl.editImage2();
            }
        });

        editImage.add(editLeft);
        editImage.add(editRight);


        settings.add(setPictures);
        settings.add(morphButtons);
        settings.add(ctrlPtBar);
        settings.add(editImage);

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
        //jfc.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "Pictures"));
        jfc.showDialog(null, "Select a file");
        jfc.setVisible(true);
        return jfc.getSelectedFile();
    }

    private File getApprovedPath(String[] extensions){
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new FileNameExtensionFilter(extensions[0], extensions));
        jfc.showDialog(null, "Select");
        jfc.setVisible(true);

        return jfc.getSelectedFile();
    }
    
    private String correctFileExtension(String filePath, String extension){
        if(filePath.contains(".")) { //the user added their own extension
            String[] absPathParts = filePath.split("\\.");

            filePath = "";
            for(int i = 0; i < absPathParts.length-1; i++)
                filePath += absPathParts[i];
            filePath += ("." + extension);
        } else { // no extension so add it
            filePath += ("." + extension);
        }
        return filePath;
    }

    //Set the image in a particular grid
    public BufferedImage getBufferedImage(Grid g, String path)
    {
        Image img = new ImageIcon(path).getImage();
        Dimension scaled = scaleImg(img);
        img = img.getScaledInstance((int)scaled.getWidth(), (int)scaled.getHeight(), Image.SCALE_SMOOTH);

        MediaTracker tracker = new MediaTracker(new Component() {});
        tracker.addImage(img, 0);

        try { tracker.waitForID(0); }
        catch(InterruptedException e){ System.out.println("exception");}

        BufferedImage buf = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = buf.createGraphics();
        bg.drawImage(img, g.getX(), g.getY(), null);
        bg.dispose();
        return buf;
    }

    //scale but maintain aspect ratio
    private Dimension scaleImg(Image i)
    {
        Dimension d;
        double widthScale = MAX_DIMENSION / (double)i.getWidth(null);
        double heightScale = MAX_DIMENSION / (double)i.getHeight(null);
        double scale = Math.min(widthScale, heightScale);
        d = new Dimension((int)(i.getWidth(null) * scale), (int)(i.getHeight(null) * scale));
        return d;
    }

    private void saveGrids(String filename){

        Grid[] saveGrids = {leftGrid, rightGrid};
        try{
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(saveGrids);
            fileOut.close();
            out.close();
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void loadGrids(String filename){

        try{
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Grid[] savedGrids = (Grid[])in.readObject();

            //update the options menu for grid resolution
            options.setGridResolution(savedGrids[0].getGridWidth()-2, savedGrids[0].getGridHeight()-2);

            gridControl.setImage1(savedGrids[0].getImg());
            gridControl.setImage2(savedGrids[1].getImg());

            leftGrid.copyGrid(savedGrids[0]);
            rightGrid.copyGrid(savedGrids[1]);

            fileIn.close();
            in.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String args[]){
        JMorph morph = new JMorph();
        morph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
