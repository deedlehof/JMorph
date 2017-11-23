import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.Serializable;

//the entire class can be serialized for the two graphs
public class Grid extends JLabel implements Serializable{

    private int width, height;
    private boolean isDragging = false;
    private CtrlPoint dragPoint = null;

    private final Color baseColor = Color.black;
    private final Color activeColor = Color.red;

    private CtrlPoint[][] pntList;
    private CtrlTriangle[] triangleList;

    private boolean drawTriangles = true;
    private int radius = 5;

    private BufferedImage origImg;
    boolean hasImg = false;

    public Grid(int _width, int _height){
        this.width = _width + 2; //add two for edge points
        this.height = _height + 2;

        this.setSize(400, 400);

        generatePoints();
        generateTriangles();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                if(dragPoint != null) {
                    dragPoint.setStatus(false);
                }

                Point clickPnt = e.getPoint();
                for(int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if(pntList[x][y].contains(clickPnt)){
                            isDragging = true;
                            dragPoint = pntList[x][y];
                            dragPoint.setStatus(true);
                            repaint();
                            return;
                        }
                    }
                }
            }

            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                isDragging = false;
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if(isDragging){
                    if(dragPoint != null && dragPoint.isMoveable()){
                        Point currPnt = e.getPoint();
                        if(currPnt.x < 0) { currPnt.x = 0; }
                        if(currPnt.y < 0) { currPnt.y = 0; }
                        if(currPnt.x > getWidth()) { currPnt.x = getWidth(); }
                        if(currPnt.y > getHeight()) { currPnt.y = getHeight(); }
                        dragPoint.setLocation(currPnt);
                        repaint();
                    }

                }
            }
        });


    }

    public Grid(Grid cpyGrid){
        this(cpyGrid.width-2, cpyGrid.height-2);
        this.pntList = cpyGrid.getCopyPntList();
    }

    private void generatePoints(){
        pntList = new CtrlPoint[width][height];

        int sepWidth = getWidth()/(width-1);  //gaps are 1 less than points
        int sepHeight = getHeight()/(height-1);

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                if(x == 0 || y ==0 || x == width-1 || y == height-1) {
                    pntList[x][y] = new CtrlPoint(sepWidth * x, sepHeight * y, x, y, false);
                } else {
                    pntList[x][y] = new CtrlPoint(sepWidth * x, sepHeight * y, x, y, true);
                }
            }
        }
        repaint();
    }

    public void resetGrid()
    {
        //reset the points to their unaltered state
    }

    public CtrlPoint[][] getPntList(){
        return pntList;
    }

    public CtrlPoint[][] getCopyPntList(){
        CtrlPoint[][] cpyPnts = new CtrlPoint[width][height];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                CtrlPoint currPnt = pntList[x][y];
                cpyPnts[x][y] = new CtrlPoint(currPnt.x, currPnt.y, x, y, currPnt.isMoveable());
            }
        }

        return cpyPnts;
    }

    private void generateTriangles(){
        triangleList = new CtrlTriangle[((width-1)*(height-1))*2];

        int placeCount = 0;
        for(int y  = 0; y < height-1; y++){
            for(int x = 0; x < width-1; x++){
                triangleList[placeCount] = new CtrlTriangle(
                        pntList[x][y],
                        pntList[x+1][y],
                        pntList[x+1][y+1]
                );
                placeCount++;

                triangleList[placeCount] = new CtrlTriangle(
                        pntList[x][y],
                        pntList[x][y+1],
                        pntList[x+1][y+1]
                );
                placeCount++;
            }
        }
    }

    public CtrlTriangle[] getTriangleList(){
        return triangleList;
    }

    public CtrlPoint getDragPoint() {
        return dragPoint;
    }

    public void changeActivePoint(int gridX, int gridY, boolean pntStatus){
        if(dragPoint != null){
            dragPoint.setStatus(false);
            dragPoint = null;
            isDragging = false;
        }
        pntList[gridX][gridY].setStatus(pntStatus);
        if(pntStatus)
            repaint();
    }

    public void setGridImmovable(boolean move){
        for(int y = 1; y < height-1; y++){
            for(int x = 1; x < width-1; x++){
                pntList[x][y].setMoveable(!move);
            }
        }
    }

    public void setGridResolution(int _width, int _height){
        this.width = _width;
        this.height = _height;

        generatePoints();
        generateTriangles();
        repaint();
    }

    public int getGridWidth(){ return width; }

    public int getGridHeight() { return height; }

    public void setPntRadius(int _radius){
        this.radius = _radius;
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                pntList[x][y].setRadius(radius);
            }
        }
        repaint();
    }

    public void morphGrid(Grid destGrid, int seconds, int framesPerSecond){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                applyMorph(destGrid, seconds, framesPerSecond);
            }
        });
        t.start();
    }

    //super basic morph
    //just wanted to do a proof of concept.
    //not sure if this is how we should do it in the finished product
    //uses PointInitial + percent*(PointFinal - PointInitial)
    private void applyMorph(Grid destGrid, int seconds, int framesPerSecond){

        CtrlPoint[][] points = destGrid.getCopyPntList();
        /*
        double imgWidthDiff, imgHeightDiff;
        int originalWidth, originalHeight;

        imgWidthDiff = this.getWidth() - destGrid.getWidth();
        imgHeightDiff = this.getHeight() - destGrid.getHeight();
        originalWidth = this.getWidth();
        originalHeight = this.getHeight();
        */


        double xDifference[][] = new double[width][height];
        double yDifference[][] = new double[width][height];
        int xOriginal[][] = new int[width][height];
        int yOriginal[][] = new int[width][height];

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                xDifference[x][y] = points[x][y].x - pntList[x][y].x;
                yDifference[x][y] = points[x][y].y - pntList[x][y].y;
                xOriginal[x][y] = pntList[x][y].x;
                yOriginal[x][y] = pntList[x][y].y;
            }
        }

        int sleepTime = 1000/framesPerSecond;
        int loopCount = seconds*framesPerSecond;
        for(int i = 0; i < loopCount; i++){
            double percent = (i+1)/(double)loopCount;
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    int xDiff = (int)(percent*xDifference[x][y]);
                    int yDiff = (int)(percent*yDifference[x][y]);
                    pntList[x][y].x = xOriginal[x][y] + xDiff;
                    pntList[x][y].y = yOriginal[x][y] + yDiff;
                }
            }
            /*
            int imgWidth = (int) (originalWidth + percent*imgWidthDiff);
            int imgHeight = (int) (originalHeight + percent*imgHeightDiff);
            this.setSize(imgWidth, imgHeight);
            */


            this.repaint();

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void setImg(BufferedImage i)
    {
        origImg = i;
        hasImg = true;
        //change width the point generator must traverse
        this.setSize(i.getWidth(), i.getHeight());
        generatePoints();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(hasImg) {
            Graphics2D bg = (Graphics2D) g;
            bg.drawImage(origImg, getX(), getY(), null);
        }

        if(drawTriangles){
            for(int y = 0; y < height-1; y++){
                for(int x = 0; x < width-1; x++){
                    g.drawLine(pntList[x][y].x, pntList[x][y].y, pntList[x+1][y].x, pntList[x+1][y].y);
                    g.drawLine(pntList[x][y].x, pntList[x][y].y, pntList[x+1][y+1].x, pntList[x+1][y+1].y);
                    g.drawLine(pntList[x][y].x, pntList[x][y].y, pntList[x][y+1].x, pntList[x][y+1].y);
                }
                g.drawLine(pntList[width-1][y].x, pntList[width-1][y].y, pntList[width-1][y+1].x, pntList[width-1][y+1].y);
            }

            for(int x = 0; x < width-1; x++){
                g.drawLine(pntList[x][height-1].x, pntList[x][height-1].y, pntList[x+1][height-1].x, pntList[x+1][height-1].y);
            }
        }

        for(int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                CtrlPoint currPnt = pntList[x][y];
                if(currPnt.getStatus()){
                    g.setColor(activeColor);
                } else {
                    g.setColor(baseColor);
                }
                g.fillOval((int)currPnt.getX() - radius, (int)currPnt.getY() - radius, radius*2, radius*2);
            }
        }
    }
}
