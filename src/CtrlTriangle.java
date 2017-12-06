import java.awt.*;

public class CtrlTriangle extends Polygon{

    public final CtrlPoint c1, c2, c3;
    public final CtrlPoint[] points;

    public CtrlTriangle(CtrlPoint _c1, CtrlPoint _c2, CtrlPoint _c3){
        this.c1 = _c1;
        this.c2 = _c2;
        this.c3 = _c3;
        points = new CtrlPoint[]{c1, c2, c3};
    }

    public double getX(int index){
        return points[index].getX();
    }

    public double getY(int index){
        return points[index].getY();
    }

    public boolean contains(Point pnt) {
        Polygon triangle = new Polygon();
        triangle.addPoint(c1.x, c1.y);
        triangle.addPoint(c2.x, c2.y);
        triangle.addPoint(c3.x, c3.y);
        return triangle.contains(pnt);
    }

    public Polygon getShape(){
        Polygon triangle = new Polygon();
        triangle.addPoint(c1.x, c1.y);
        triangle.addPoint(c2.x, c2.y);
        triangle.addPoint(c3.x, c3.y);
        return triangle;
    }
}
