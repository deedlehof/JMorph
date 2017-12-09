import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class ImageEditor extends JLabel {

    private BufferedImage originalImage;
    private BufferedImage filteredImage;

    private int currentFilter = 0;

    private static final int LOWPASS = 1;
    private static final int SHARPEN = 2;
    private static final int EDGE = 3;

    private final float[] LOWPASS3x3 =
            {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};
    private final float[] SHARPEN3x3 =
            {0.f, -1.f, 0.f, -1.f, 5.f, -1.f, 0.f, -1.f, 0.f};
    private final float[] EDGE3x3 =
            {0.f, -1.f, 0.f, -1.f, 4.0f, -1.f, 0.f, -1.f, 0.f};

    public ImageEditor(BufferedImage _originalImage){
        this.originalImage = _originalImage;
        this.filteredImage = deepCopy(_originalImage);
    }

    public void setImage(BufferedImage img) {
        if (img == null) return;
        originalImage = img;
        filteredImage = deepCopy(img);
        setPreferredSize(new Dimension(originalImage.getWidth(), originalImage.getHeight()));
        this.repaint();
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        if (bi == null) return bi;

        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null)
                .getSubimage(0, 0, bi.getWidth(), bi.getHeight());
    }

    public void blurImage(){
        currentFilter = LOWPASS;

        Kernel kernel = new Kernel(3, 3, LOWPASS3x3);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage newbim = deepCopy(originalImage);

        cop.filter(newbim, filteredImage);
        this.repaint();
    }

    public void sharpenImage(){
        currentFilter = SHARPEN;

        Kernel kernel = new Kernel(3, 3, SHARPEN3x3);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage newbim = deepCopy(originalImage);

        cop.filter(newbim, filteredImage);
        this.repaint();
    }

    public void edgeDetect(){
        currentFilter = EDGE;

        Kernel kernel = new Kernel(3, 3, EDGE3x3);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage newbim = deepCopy(originalImage);

        cop.filter(newbim, filteredImage);
        this.repaint();
    }

    public void changeBrightness(double factor){
        if (factor < 0) { factor = 0; }
        if (factor > 2) { factor = 2; }

        applyFilter(currentFilter);

        for(int y = 0; y < filteredImage.getHeight(); y++){
            for(int x = 0; x < filteredImage.getWidth(); x++){
                Color startColor = new Color(filteredImage.getRGB(x, y));
                int red, green, blue;
                red = (int)(startColor.getRed() * factor);
                if (red > 255) red = 255;
                green = (int)(startColor.getGreen() * factor);
                if (green > 255) green = 255;
                blue = (int)(startColor.getBlue() * factor);
                if (blue > 255) blue = 255;
                filteredImage.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
        repaint();
    }

    private void applyFilter(int filterOption){
        switch (filterOption){
            case 0:
                filteredImage = deepCopy(originalImage);
                break;
            case LOWPASS:
                blurImage();
                break;
            case SHARPEN:
                sharpenImage();
                break;
            case EDGE:
                edgeDetect();
                break;
        }
    }

    public void resetImage(){
        filteredImage = deepCopy(originalImage);
        repaint();
    }

    public BufferedImage getFilteredImage() {
        return filteredImage;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(filteredImage, 0, 0, this);
    }
}
