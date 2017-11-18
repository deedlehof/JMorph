import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GridPairController {

    private Grid grid1, grid2;
    private CtrlPoint activePnt1, activePnt2;

    public GridPairController(Grid _grid1, Grid _grid2){
        this.grid1 = _grid1;
        this.grid2 = _grid2;

        grid1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                System.out.println("MOUSE PRESSED ON GRID 1");
                activePnt1 = grid1.getDragPoint();
                System.out.println(activePnt1.getGridX());
                grid2.changeActivePoint(activePnt1.getGridX(), activePnt1.getGridY(), true);
            }

            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                grid2.changeActivePoint(activePnt1.getGridX(), activePnt1.getGridY(), false);
            }
        });

        grid2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                activePnt2 = grid2.getDragPoint();
                grid1.changeActivePoint(activePnt2.getGridX(), activePnt2.getGridY(), true);
            }

            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                grid1.changeActivePoint(activePnt2.getGridX(), activePnt2.getGridY(), false);
            }
        });
    }

    public void setGridResolution(int width, int height){
        grid1.setGridResolution(width, height);
        grid2.setGridResolution(width, height);
    }

    public void setPntRadius(int radius){
        grid1.setPntRadius(radius);
        grid2.setPntRadius(radius);
    }
}
