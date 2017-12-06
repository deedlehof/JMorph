import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;


/*
The warping part of the morph doesn't work at all.  I'm unsure how to get it to work.  The images do fade though.
 */

public class TransitionPanel extends JPanel {

    private int gridWidth, gridHeight;

    private CtrlPoint[][] pntList;
    private CtrlTriangle[][] triangleList;

    private BufferedImage currImage = null;
    private BufferedImage destImage = null;

    private Grid originalGrid = null;

    private Object ALIASING = RenderingHints.VALUE_ANTIALIAS_ON;
    private Object INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BICUBIC;

    public TransitionPanel(Grid grid){
        this.originalGrid = grid;
        this.gridWidth = grid.getGridWidth();
        this.gridHeight = grid.getGridHeight();
        this.pntList = grid.getCopyPntList();
        this.triangleList = generateTriangles(pntList);
        this.currImage = grid.getImg();

        setSize(new Dimension(grid.getWidth(), grid.getHeight()));
    }

    public void resetPanel(){
        if(originalGrid == null) return;

        this.pntList = originalGrid.getCopyPntList();
        this.triangleList = generateTriangles(pntList);
        this.currImage = originalGrid.getImg();

        setSize(new Dimension(originalGrid.getWidth(), originalGrid.getHeight()));
    }

    public void morph(Grid grid, int seconds, int frames){
        if(currImage == null || grid.getImg() == null) {
            JOptionPane.showMessageDialog(this, "You must have an image in both the grids to do a warp.");
            return;
        }

        resetPanel();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                applyMorph(grid, seconds, frames);
            }
        });
        t.start();
    }

    
    private void applyMorph(Grid destGrid, int seconds, int framesPerSecond){

        CtrlPoint[][] destPoints = destGrid.getCopyPntList();

        double xDifference[][] = new double[gridWidth][gridHeight];
        double yDifference[][] = new double[gridWidth][gridHeight];
        int xOriginal[][] = new int[gridWidth][gridHeight];
        int yOriginal[][] = new int[gridWidth][gridHeight];

        //calculate the change in the triangles of both of the images
        for(int y = 0; y < gridHeight; y++){
            for(int x = 0; x < gridWidth; x++){
                xDifference[x][y] = destPoints[x][y].x - pntList[x][y].x;
                yDifference[x][y] = destPoints[x][y].y - pntList[x][y].y;
                xOriginal[x][y] = pntList[x][y].x;
                yOriginal[x][y] = pntList[x][y].y;
            }
        }

        int sleepTime = 1000/framesPerSecond;
        int loopCount = seconds*framesPerSecond;
        for(int i = 0; i < loopCount; i++){
            double percent = (i+1)/(double)loopCount;
            //calculate the position of the points based on percentage done
            for(int y = 0; y < gridHeight; y++){
                for(int x = 0; x < gridWidth; x++){
                    int xDiff = (int)(percent*xDifference[x][y]);
                    int yDiff = (int)(percent*yDifference[x][y]);
                    pntList[x][y].x = xOriginal[x][y] + xDiff;
                    pntList[x][y].y = yOriginal[x][y] + yDiff;
                }
            }

            //warp image
            currImage = warpColors(originalGrid.getImg(), destGrid.getImg(), percent);
            warpTriangles(triangleList, currImage); //warping doesn't work at all

            this.repaint();

            try {
                Thread.sleep(sleepTime); //TODO return to sleeptime
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }


    public BufferedImage warpColors(BufferedImage src, BufferedImage dest, double percent){
        BufferedImage outImage = new BufferedImage(src.getWidth(null), src.getHeight(null), BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < src.getHeight(); y++){
            for(int x = 0; x < src.getWidth(); x++){
                Color startColor = new Color(src.getRGB(x, y));
                Color endColor = new Color(dest.getRGB(x, y));
                int red, green, blue, alpha;
                red = (int)(startColor.getRed() + percent*(endColor.getRed() - startColor.getRed()));
                if (red > 255) red = 255;
                green = (int)(startColor.getGreen() + percent*(endColor.getGreen() - startColor.getGreen()));
                if (green > 255) green = 255;
                blue = (int)(startColor.getBlue() + percent*(endColor.getBlue() - startColor.getBlue()));
                if (blue > 255) blue = 255;
                alpha = (int)(startColor.getAlpha() + percent*(endColor.getAlpha() - startColor.getAlpha()));
                if (alpha > 255) alpha = 255;

                outImage.setRGB(x, y, new Color(red, green, blue, alpha).getRGB());
            }
        }

        return outImage;
    }
    
    private void warpTriangles(CtrlTriangle[][] destTriangles, BufferedImage destImage){
        for(int y = 0; y < gridHeight-1; y++){
            for(int x = 0; x < (gridWidth-1)*2; x++){
                warpTriangle(triangleList[x][y], destTriangles[x][y], currImage, destImage);
            }
        }
    }

    //based on slide set 7.  Isn't correct
    private void warpTriangle(CtrlTriangle S, CtrlTriangle D, BufferedImage src, BufferedImage dest){
        AffineTransform af = new AffineTransform(S.getX(0), S.getY(0), S.getX(1), S.getY(1), S.getX(2), S.getY(2));

        GeneralPath destPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        destPath.moveTo(D.getX(0), D.getY(0));
        destPath.lineTo(D.getX(1), D.getY(1));
        destPath.lineTo(D.getX(2), D.getY(2));
        destPath.lineTo(D.getX(0), D.getY(0));

        Graphics2D g2 = dest.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, ALIASING);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, INTERPOLATION);
        g2.clip(destPath);
        g2.setTransform(af);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
    }

    private CtrlTriangle[][] generateTriangles(CtrlPoint[][] points){
        triangleList = new CtrlTriangle[(gridWidth-1)*2][gridHeight-1];

        for(int y  = 0; y < gridHeight-1; y++){
            for(int x = 0; x < gridWidth-1; x++){
                triangleList[x*2][y] = new CtrlTriangle(
                        points[x][y],
                        points[x][y+1],
                        points[x+1][y+1]
                );

                triangleList[x*2+1][y] = new CtrlTriangle(
                        points[x][y],
                        points[x+1][y],
                        points[x+1][y+1]
                );
            }
        }
        return triangleList;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currImage != null) {
            Graphics2D bg = (Graphics2D) g;
            bg.drawImage(currImage, getX(), getY(), null);
        }
    }
}
