import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

//the entire class can be serialized for the two graphs
public class Grid extends JPanel implements Serializable{

    private int width, height;

    private boolean isDragging = false;
    private CtrlPoint clickedPoint = null;
    private CtrlPoint[] highlightedPoints = null;
    private CtrlTriangle[][] dragTriangles = null;
    private CtrlTriangle[] errorTriangles = null;
    private boolean isHighlighting = false;
    private Point selectCorner1, selectCorner2;
    private Polygon highlightPoly = null;

    private final Color baseColor = Color.black;
    private final Color activeColor = Color.red;
    private final Color errorColor = new Color(255, 255, 150, 150);

    private CtrlPoint[][] pntList;
    private CtrlTriangle[][] triangleList;

    private boolean drawTriangles = true;
    private int radius = 5;

    private BufferedImage origImg = null;

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

                Point clickPnt = e.getPoint();
                selectCorner1 = e.getPoint();

                //reset if not a highlighted point else just move
                for(int y = 1; y < height-1; y++) {
                    for (int x = 1; x < width-1; x++) {
                        if(pntList[x][y].contains(clickPnt)){
                            isDragging = true;
                            clickedPoint = pntList[x][y];
                            if(!pntInList(highlightedPoints, pntList[x][y])){ //do only 1 drag point
                                resetDragPoints();
                                highlightedPoints = new CtrlPoint[1];
                                dragTriangles = new CtrlTriangle[1][];

                                highlightedPoints[0] = pntList[x][y];
                                dragTriangles[0] = getPntTriangleNeighbors(pntList[x][y]);
                                pntList[x][y].setStatus(true);
                                repaint();
                            }
                            return;
                        }
                    }
                }

                isHighlighting = true;

            }

            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                isDragging = false;

                //if highlighting then get all covered points
                if(isHighlighting) {
                    selectCorner2 = e.getPoint();
                    resetDragPoints();
                    genHighlightPoly();
                    highlightedPoints = getHighlightedPoints();

                    if(highlightedPoints == null) return;

                    dragTriangles = new CtrlTriangle[highlightedPoints.length][];
                    for(int i = 0; i < highlightedPoints.length; i++){
                        highlightedPoints[i].setStatus(true);
                        dragTriangles[i] = getPntTriangleNeighbors(highlightedPoints[i]);
                    }
                    repaint();
                    isHighlighting = false;
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                //check that every drag is valid and then if so move
                if(isDragging){

                    Point currPnt = e.getPoint();

                    double offSetX = currPnt.x - clickedPoint.x;
                    double offSetY = currPnt.y - clickedPoint.y;

                    Point[] destPoints = new Point[highlightedPoints.length];

                    for(int i = 0; i < highlightedPoints.length; i++) {

                        int checkX = (int)(highlightedPoints[i].x + offSetX);
                        int checkY = (int)(highlightedPoints[i].y + offSetY);

                        if (!(pointInTriangles(dragTriangles[i], new Point(checkX, checkY)))) {
                            errorTriangles = dragTriangles[i];
                            repaint();
                            return;
                        }

                        if (checkX < 0) {
                            return;
                        }
                        if (checkY < 0) {
                            return;
                        }
                        if (checkX > getWidth()) {
                            return;
                        }
                        if (checkY > getHeight()) {
                            return;
                        }

                        destPoints[i] = new Point(checkX, checkY);
                    }

                    if(errorTriangles != null)
                        errorTriangles = null;

                    for(int i = 0; i < highlightedPoints.length; i++){
                        highlightedPoints[i].setLocation(destPoints[i]);
                    }
                    repaint();

                }
                if(isHighlighting){
                    selectCorner2 = e.getPoint();
                    genHighlightPoly();
                    repaint();
                }
            }
        });


    }

    private void resetDragPoints(){
        //set all of the points back to black
        if(highlightedPoints == null) return;

        for(int i = 0; i < highlightedPoints.length; i++){
            highlightedPoints[i].setStatus(false);
        }

        highlightedPoints = null;

        repaint();
    }

    private boolean pntInList(CtrlPoint[] points, CtrlPoint findPnt){
        if(points == null || points.length == 0) return false;

        for(int i = 0; i < points.length; i++){
            if(points[i] == findPnt) return true;
        }

        return false;
    }

    private CtrlPoint[] getHighlightedPoints(){
        genHighlightPoly();

        ArrayList<CtrlPoint> highlightedPoints = new ArrayList<>();

        for(int y = 1; y < height-1; y++){
            for(int x = 1; x < width-1; x++){
                CtrlPoint currPnt = pntList[x][y];
                if(highlightPoly.contains(currPnt)){
                    highlightedPoints.add(currPnt);
                }
            }
        }

        return highlightedPoints.toArray(new CtrlPoint[highlightedPoints.size()]);
    }

    private void genHighlightPoly(){
        Point corner3 = new Point(selectCorner2.x, selectCorner1.y);
        Point corner4 = new Point(selectCorner1.x, selectCorner2.y);
        Point[] highlightCorners = {selectCorner1, corner3, selectCorner2, corner4};
        highlightPoly = polygonize(highlightCorners);
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
        generatePoints();
        generateTriangles();
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
        triangleList = new CtrlTriangle[(width-1)*2][height-1];

        for(int y  = 0; y < height-1; y++){
            for(int x = 0; x < width-1; x++){
                triangleList[x*2][y] = new CtrlTriangle(
                        pntList[x][y],
                        pntList[x][y+1],
                        pntList[x+1][y+1]
                );

                triangleList[x*2+1][y] = new CtrlTriangle(
                        pntList[x][y],
                        pntList[x+1][y],
                        pntList[x+1][y+1]
                );
            }
        }
    }

    private CtrlTriangle[] getPntTriangleNeighbors(CtrlPoint pnt){
        CtrlTriangle[] controlledTriangles = new CtrlTriangle[6];
        int triangleXIndex = pnt.getGridX()*2 - 1;
        int triangleYIndex = pnt.getGridY() - 1;

        controlledTriangles[0] = triangleList[triangleXIndex-1][triangleYIndex];
        controlledTriangles[1] = triangleList[triangleXIndex][triangleYIndex];
        controlledTriangles[2] = triangleList[triangleXIndex+1][triangleYIndex];
        controlledTriangles[3] = triangleList[triangleXIndex][triangleYIndex+1];
        controlledTriangles[4] = triangleList[triangleXIndex+1][triangleYIndex+1];
        controlledTriangles[5] = triangleList[triangleXIndex+2][triangleYIndex+1];

        return controlledTriangles;
    }

    private boolean pointInTriangles(CtrlTriangle[] triangles, Point point){
        for(int i = 0; i < triangles.length; i++){
            if(triangles[i].contains(point)) { return true; }
        }

        return false;
    }


    public CtrlPoint[] getDragPoints() {
        return highlightedPoints;
    }


    public void changeActivePoint(int gridX, int gridY, boolean pntStatus){
        if(highlightedPoints != null){
            for (CtrlPoint point: highlightedPoints) {
                point.setStatus(false);
            }
            highlightedPoints = null;
        }
        pntList[gridX][gridY].setStatus(pntStatus);
        if(pntStatus)
            repaint();
    }


    public void setGridResolution(int _width, int _height){
        if(width == _width+2 && height == _height+2) return;
        this.width = _width + 2;
        this.height = _height + 2;

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

    public void drawTriangles(boolean draw){
        this.drawTriangles = draw;
        repaint();
    }

    public void setImg(BufferedImage i)
    {
        origImg = i;
        //change width the point generator must traverse
        this.setSize(i.getWidth(), i.getHeight());
        resetGrid();
        this.repaint();
    }

    public BufferedImage getImg() {
        return origImg;
    }

    private Polygon polygonize(Point[] polyPoints){

        Polygon tempPoly = new Polygon();

        for(int  i=0; i < polyPoints.length; i++){
            tempPoly.addPoint(polyPoints[i].x, polyPoints[i].y);
        }

        return tempPoly;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(origImg != null) {
            Graphics2D bg = (Graphics2D) g;
            bg.drawImage(origImg, getX(), getY(), null);
        }

        /*
        //draws the triangles bounding the drag
        if(dragTriangles != null){
            g.setColor(Color.green);
            for (int p = 0; p < dragTriangles.length; p++){
                for(int t = 0; t < dragTriangles[p].length; t++){
                    g.fillPolygon(dragTriangles[p][t].getShape());
                }
            }
        }
        */

        if(errorTriangles != null){
            g.setColor(errorColor);
            for(int t = 0; t < errorTriangles.length; t++){
                g.fillPolygon(errorTriangles[t].getShape());
            }
        }

        if(isHighlighting && highlightPoly != null){
            g.setColor(Color.cyan);
            g.fillPolygon(highlightPoly);
            g.setColor(Color.blue);
            g.drawPolygon(highlightPoly);
        }

        g.setColor(Color.black);
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
                g.setColor(Color.white);
                g.drawOval((int)currPnt.getX() - radius, (int)currPnt.getY() - radius, radius*2, radius*2);
            }
        }
    }
}
