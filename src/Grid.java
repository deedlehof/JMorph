import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Grid extends JPanel {

    private int width, height; //static? will the two grids ever be different sizes?
    private boolean isDragging = false;
    private CtrlPoint dragPoint = null;

    private final Color baseColor = Color.black;
    private final Color activeColor = Color.red;

    private CtrlPoint[][] pntList;
    private CtrlTriangle[] triangleList;

    private boolean drawTriangles = true;
    private int radius = 5;

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
                        dragPoint.setLocation(e.getPoint());
                        repaint();
                    }

                }
            }
        });


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

    public CtrlPoint getDragPoint() {
        return dragPoint;
    }

    public void changeActivePoint(int gridX, int gridY, boolean pntStatus){
        pntList[gridX][gridY].setStatus(pntStatus);
        if(pntStatus)
            repaint();
    }

    public void setGridResolution(int _width, int _height){
        this.width = _width;
        this.height = _height;

        generatePoints();
        generateTriangles();
        repaint();
    }

    public void setPntRadius(int _radius){
        this.radius = _radius;
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                pntList[x][y].setRadius(radius);
            }
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CtrlPoint currPnt = pntList[x][y];
                if(currPnt.getStatus()){
                    g.setColor(activeColor);
                } else {
                    g.setColor(baseColor);
                }
                if(currPnt.isMoveable()) {
                    g.fillOval((int)currPnt.getX() - radius, (int)currPnt.getY() - radius, radius*2, radius*2);
                }
            }
        }

        //TODO draw based on points not triangles to avoid redrawing
        if(drawTriangles){
            for(int i = 0; i < triangleList.length; i++){
                CtrlTriangle currTriangle = triangleList[i];
                g.drawLine(currTriangle.c1.x, currTriangle.c1.y,
                        currTriangle.c2.x, currTriangle.c2.y);
                g.drawLine(currTriangle.c2.x, currTriangle.c2.y,
                        currTriangle.c3.x, currTriangle.c3.y);
                g.drawLine(currTriangle.c3.x, currTriangle.c3.y,
                        currTriangle.c1.x, currTriangle.c1.y);
            }
        }
    }
}
