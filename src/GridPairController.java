import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GridPairController {

    private Grid grid1, grid2;
    private CtrlPoint activePnt1, activePnt2;
    private ImageEditFrame imageEditor1, imageEditor2;

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

        ActionListener confirmGrid1Edit = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid1.setImg(imageEditor1.getImage());
            }
        };

        ActionListener confirmGrid2Edit = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid2.setImg(imageEditor2.getImage());
            }
        };

        this.imageEditor1 = new ImageEditFrame(grid1.getImg(), confirmGrid1Edit);
        this.imageEditor2 = new ImageEditFrame(grid2.getImg(), confirmGrid2Edit);
    }

    public void setGridResolution(int width, int height){
        grid1.setGridResolution(width, height);
        grid2.setGridResolution(width, height);
    }

    public void setPntRadius(int radius){
        grid1.setPntRadius(radius);
        grid2.setPntRadius(radius);
    }

    public void drawTriangles(boolean draw){
        grid1.drawTriangles(draw);
        grid2.drawTriangles(draw);
    }

    public void resetGrids(){
        grid1.resetGrid();
        grid2.resetGrid();
    }

    public void editImage1(){
        imageEditor1.setVisible(true);
    }

    public void editImage2(){
        imageEditor2.setVisible(true);
    }

    public void setImage1(BufferedImage img){
        imageEditor1.setImage(img);
        grid1.setImg(img);
    }

    public void setImage2(BufferedImage img){
        imageEditor2.setImage(img);
        grid2.setImg(img);
    }
}
