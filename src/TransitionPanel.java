import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;


public class TransitionPanel extends JPanel {

    private int gridWidth, gridHeight;

    private CtrlPoint[][] pntList;
    private CtrlPoint[][] destPntList;
    private CtrlTriangle[][] triangleList;
    private CtrlTriangle[][] destTriangleList;

    private BufferedImage currImage = null;
    private BufferedImage destImage = null;
    private BufferedImage morphedImage = null; //holds the morphed image so neither current or dest is overwritten

    private Grid originalGrid = null;
    private Grid destGrid = null;

    private Object ALIASING = RenderingHints.VALUE_ANTIALIAS_ON;
    private Object INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BICUBIC;

    private boolean showMorphed; //tells paintcomponent whether to show the morphed image or not

    /*
    * Transition happens between 2 grids
    * */
    public TransitionPanel(Grid grid, Grid dest){
        this.originalGrid = grid;
        this.destGrid = dest;
        this.gridWidth = grid.getGridWidth();
        this.gridHeight = grid.getGridHeight();
        this.pntList = grid.getCopyPntList();
        this.triangleList = generateTriangles(pntList);
        this.destPntList = dest.getCopyPntList(); //get dest info
        this.destTriangleList = generateTriangles(destPntList);
        this.currImage = grid.getImg();
        this.destImage = dest.getImg();
        this.morphedImage = new BufferedImage(currImage.getWidth(), currImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        setSize(new Dimension(grid.getWidth(), grid.getHeight()));
    }

    public void resetPanel(){
        if(originalGrid == null) return;

        this.pntList = originalGrid.getCopyPntList();
        this.triangleList = generateTriangles(pntList);
        this.currImage = originalGrid.getImg();

        setSize(new Dimension(originalGrid.getWidth(), originalGrid.getHeight()));
    }

    //takes in grid2 in this method
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

        BufferedImage tempEnd = new BufferedImage(destImage.getWidth(), destImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        warpTriangles(destTriangleList, triangleList, destImage, tempEnd);

        int sleepTime = 1000/framesPerSecond;
        int loopCount = seconds * framesPerSecond;
        for(int i = 0; i < loopCount; i++){
            double percent = (i + 1)/(double)loopCount;
            //calculate the position of the points based on percentage done
            for(int y = 0; y < gridHeight; y++) {
                for (int x = 0; x < gridWidth; x++) {
                    int xDiff = (int) ((1 - percent) * xDifference[x][y]);
                    int yDiff = (int) ((1 - percent) * yDifference[x][y]);
                    pntList[x][y].x = xOriginal[x][y] + xDiff;
                    pntList[x][y].y = yOriginal[x][y] + yDiff;
                }
            }



            //warp image
            currImage = warpColors(originalGrid.getImg(), tempEnd, percent);
            warpTriangles(triangleList, destTriangleList, currImage, morphedImage); //use dest triangles, but apply changes to the copied image

            showMorphed = true; //tell paintcomponent to show the morphed image

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
        /*
        *  TODO: Fix exception thrown when size and aspect ratio of images don't match exactly
        *
        * */
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

    /*
    * Warping is weird
    * src image works in reverse to dest image morphing; source and dest triangles need to be swapped for src image
    * */
    private void warpTriangles(CtrlTriangle[][] srcTriangles, CtrlTriangle[][] destTriangles, BufferedImage curImg, BufferedImage destImg){
        for(int y = 0; y < gridHeight-1; y++){
            for(int x = 0; x < (gridWidth-1)*2; x++){
                warpTriangle(srcTriangles[x][y], destTriangles[x][y], curImg, destImg);
            }
        }
    }

    private void warpTriangle(CtrlTriangle S, CtrlTriangle D, BufferedImage src, BufferedImage dest){
        /*
        * Get our current 3x3 matrix
        * run through Gauss
        * solve
        * apply Affine Transform
        * */

        double[][] mat = new double[3][3]; //3x3 matrix
        for(int i = 0; i < 3; i++)
        {
            mat[i][0] = S.getX(i);
             //System.out.println("P" + i + "(" + S.getX(i) + ", " + S.getY(i) + ")" );
            mat[i][1] = S.getY(i);
            mat[i][2] = 1.0;
        }

        int l[] = new int[3];
        Gauss(3, mat, l);

        double[] b = new double[3];
        for(int i = 0; i < 3; i++)
        {
            b[i] = D.getX(i);
        }

        double[] x = new double[3];
        solve(3, mat, l, b, x);

        double[] by = new double[3];
        for(int i = 0; i < 3; i++)
        {
            by[i] = D.getY(i);
        }

        double[] y = new double[3];
        solve(3, mat, l, by, y);

        //System.out.println("Affine:\t" + x[0] + ", " + x[1] + ", " + x[2] ); //debug
        //System.out.println("\t" + y[0] + ", " + y[1] + ", " + y[2] );

        //Apply Affine Transform to transform using the info we have (x and y arrays out of Gauss and solver)
        AffineTransform af = new AffineTransform(x[0], y[0], x[1], y[1], x[2], y[2]);

        GeneralPath destPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        destPath.moveTo((float)D.getX(0), (float)D.getY(0));
        destPath.lineTo((float)D.getX(1), (float)D.getY(1));
        destPath.lineTo((float)D.getX(2), (float)D.getY(2));
        destPath.lineTo((float)D.getX(0), (float)D.getY(0));

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

    /*
    *  Apply Gaussian Elimination with scaled partial pivoting
    *  int n: dimensions of matrix (n x n)
    *  double[][] mat: contains the values of the n x n matrix
    *  int[] l: use to determine order of elimination of coefficients
    * */
    private static void Gauss(int n, double[][] mat, int[] l)
    {
        double[] s = new double[n]; //scaling factor
        int i, j = 0, k; //iterators
        double r, rmax, smax, xmult;

        for(i = 0; i < n; i++)
        {
            l[i] = i; //initialize values
            smax = 0; //reset
            for(j = 0; j < n; j++)
            {
                smax = Math.max(smax, Math.abs(mat[i][j])); //get maximum scale factor along a row of the matrix
            }
            s[i] = smax; //update scaling factor at i with the new maximum scale factor
        }

        i = n - 1;
        for(k = 0; k < (n-1); k++)
        {
            j--;
            rmax = 0;
            for(i = k; i < n; i++) //move forward through the matrix each main loop iteration
            {
                r = Math.abs(mat[l[i]][k] / s[l[i]]);
                if(r > rmax)
                {
                    rmax = r;
                    j = i;
                }
            }
            //swap
            int temp = l[j];
            l[j] = l[k];
            l[k] = temp;

            for(i = k + 1; i < n; i++)
            {
                xmult = mat[l[i]][k] / mat[l[k]][k];
                mat[l[i]][k] = xmult;
                for(j = k + 1; j < n; j++)
                {
                    mat[l[i]][j] = mat[l[i]][j] - xmult * mat[l[k]][j];
                }
            }
        }
    }

    /*
    * Solve for matrix of coefficients
    * int n, double[][] mat, and int[] l were used in Gaussian first above
    * double[] b: the product of mat and x
    * double[] x: the 1x3 matrix of coefficients to solve for
    * */
    private static void solve(int n, double[][] mat, int[] l, double[] b, double[] x)
    {
        int i, k;
        double sum;
        for(k = 0; k < (n - 1); k++)
        {
            for(i = k + 1; i < n; i++)
            {
                b[l[i]] -= mat[l[i]][k] * b[l[k]];
            }
        }
        x[n-1] = b[l[n-1]] / mat[l[n-1]][n-1];

        for(i = n-2; i >= 0; i--)
        {
            sum = b[l[i]];
            for(int j = i + 1; j < n; j++)
            {
                sum = sum - mat[l[i]][j] * x[j];
            }
            x[i] = sum / mat[l[i]][i];
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(showMorphed)
        {
            if (morphedImage != null) {
                Graphics2D bg = (Graphics2D) g;
                bg.drawImage(morphedImage, getX(), getY(), null);
            }
        }
        else {
            if (currImage != null) {
                Graphics2D bg = (Graphics2D) g;
                bg.drawImage(currImage, getX(), getY(), null);
            }
        }
    }
}
