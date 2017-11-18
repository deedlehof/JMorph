import java.awt.*;
import java.awt.geom.Ellipse2D;

/*
    Should points keep track of their neighbors?
* */

public class CtrlPoint extends Point{

    private boolean moveable;
    private int gridX, gridY;
    private boolean status = false;
    private int radius = 5;
    private Shape shape;

    public CtrlPoint(int _x, int _y, int _gridX, int _gridY, boolean _moveable){
        super(_x, _y);
        this.gridX = _gridX;
        this.gridY = _gridY;
        this.moveable = _moveable;

        shape = new Ellipse2D.Float(_x - radius, _y - radius, radius*2, radius*2);
    }

    public boolean isMoveable(){ return moveable; }
    public void setMoveable(boolean _move){ this.moveable = _move; }

    public void setStatus(boolean state) { this.status = state; }
    public boolean getStatus(){ return status; }

    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }

    public void setRadius(int _radius){
        this.radius = _radius;
        Point currPoint = super.getLocation();
        shape = new Ellipse2D.Float(currPoint.x, currPoint.y, radius*2, radius*2);
    }

    public void setLocation(Point p){
        super.setLocation(p.x, p.y);
        shape = new Ellipse2D.Float(p.x - radius, p.y - radius, radius*2, radius*2);
    }

    public boolean contains(Point pnt) {
        return shape.contains(pnt);
    }

}
