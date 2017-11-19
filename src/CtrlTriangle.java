public class CtrlTriangle {

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
}
