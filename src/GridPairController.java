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
                activePnt1 = grid1.getDragPoint();
                if(activePnt1 != null)
                    grid2.changeActivePoint(activePnt1.getGridX(), activePnt1.getGridY(), true);
            }

            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                if(activePnt1 != null)
                    grid2.changeActivePoint(activePnt1.getGridX(), activePnt1.getGridY(), false);
            }
        });

        grid2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                activePnt2 = grid2.getDragPoint();
                if(activePnt2 != null)
                    grid1.changeActivePoint(activePnt2.getGridX(), activePnt2.getGridY(), true);
            }

            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                if(activePnt2 != null)
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

    public void resetGrids(){
        grid1.resetGrid();
        grid2.resetGrid();
    }
}
