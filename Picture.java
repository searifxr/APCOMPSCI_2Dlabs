import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class Picture {
    private Pixel[][] pixels;

    public Picture(String picture) {
        File file = new File( picture);
        BufferedImage image;
        if (!file.exists()) throw new RuntimeException("No picture at the location " + file.getPath() + "!");
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        pixels = new Pixel[image.getHeight()][image.getWidth()];
        for (int y = 0; y < pixels.length; y++) {
            for (int x = 0; x < pixels[y].length; x++) {
                int rgb = image.getRGB(x, y);
                pixels[y][x] = new Pixel((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
            }
        }
    }

    public Picture(int red, int green, int blue, int height, int width) {
        pixels = new Pixel[height][width];
        for (int y = 0; y < pixels.length; y++) {
            for (int x = 0; x < pixels[y].length; x++) {
                pixels[y][x] = new Pixel(red, green, blue);
            }
        }
    }

    public Picture(int height, int width) {
        this(Color.WHITE, height, width);
    }

    public Picture(Color color, int height, int width) {
        this(color.getRed(), color.getGreen(), color.getBlue(), height, width);
    }

    public Picture(Pixel[][] pixels) {
        if (pixels.length == 0 || pixels[0].length == 0) throw new RuntimeException("Can't have an empty image!");
        int width = pixels[0].length;
        for (int i = 0; i < pixels.length; i++) if (pixels[i].length != width)
            throw new RuntimeException("Pictures must be rectangles. pixels[0].length != pixels[" + i + "].length!");
        this.pixels = new Pixel[pixels.length][width];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                this.pixels[i][j] = new Pixel(pixels[i][j].getColor());
            }
        }
    }

    public Picture(Picture picture) {
        this(picture.pixels);
    }

    public int getWidth() {
        return pixels[0].length;
    }

    public int getHeight() {
        return pixels.length;
    }

    public Pixel getPixel(int x, int y) {
        if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0) throw new RuntimeException("No pixel at (" + x + ", " + y + ")");
        return pixels[y][x];
    }

    public void setPixel(int x, int y, Pixel pixel) {
        if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0) throw new RuntimeException("No pixel at (" + x + ", " + y + ")");
        if (pixel == null) throw new NullPointerException("Pixel is null");
        pixels[y][x] = pixel;
    }

    public PictureViewer view() {
        return new PictureViewer(this);
    }

    public void save() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedImage image = new BufferedImage(this.pixels[0].length, this.pixels.length, BufferedImage.TYPE_INT_RGB);
        for (int r = 0; r < this.pixels.length; r++)
            for (int c = 0; c < this.pixels[0].length; c++)
                image.setRGB(c, r, this.pixels[r][c].getColor().getRGB());
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        chooser.setDialogTitle("Select picture save location / file name");
        File file = null;
        int choice = chooser.showSaveDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION)
            file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".jpg") && !file.getName().endsWith(".JPG") && !file.getName().endsWith(".jpeg") && !file.getName().endsWith(".JPEG"))
            file = new File(file.getAbsolutePath() + ".jpg");
        try {
            ImageIO.write(image, "jpg", file);
            System.out.println("File created at " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Can't write to location: " + file.toString());
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("Invalid directory choice");
        }
    }

    public Pixel[][] getPixels() {
        return pixels;
    }

    public void zeroBlue() {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                p.setBlue(0);
            }
        }
    }

    public void keepOnlyBlue() {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                p.setRed(0);
                p.setGreen(0);
            }
        }
    }

    public void negate() {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                p.setRed(255 - p.getRed());
                p.setGreen(255 - p.getGreen());
                p.setBlue(255 - p.getBlue());
            }
        }
    }

    public void solarize(int threshold) {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                if (p.getRed() > threshold) p.setRed(255 - p.getRed());
                if (p.getGreen() > threshold) p.setGreen(255 - p.getGreen());
                if (p.getBlue() > threshold) p.setBlue(255 - p.getBlue());
            }
        }
    }

    public void grayscale() {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                int gray = (int) (0.299 * p.getRed() + 0.587 * p.getGreen() + 0.114 * p.getBlue());
                p.setRed(gray);
                p.setGreen(gray);
                p.setBlue(gray);
            }
        }
    }

    public void tint(double red, double blue, double green) {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                p.setRed((int) Math.min(255, p.getRed() * red));
                p.setGreen((int) Math.min(255, p.getGreen() * green));
                p.setBlue((int) Math.min(255, p.getBlue() * blue));
            }
        }
    }

    public void posterize(int span) {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                p.setRed(p.getRed() / span * span);
                p.setGreen(p.getGreen() / span * span);
                p.setBlue(p.getBlue() / span * span);
            }
        }
    }

    public void mirrorVertical() {
        Pixel leftPixel = null;
        Pixel rightPixel = null;
        int width = pixels[0].length;
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < width / 2; c++) {
                leftPixel = pixels[r][c];
                rightPixel = pixels[r][(width - 1) - c];
                rightPixel.setColor(leftPixel.getColor());
            }
        }
    }

    public void mirrorRightToLeft() {
        Pixel leftPixel = null;
        Pixel rightPixel = null;
        int width = pixels[0].length;
        for (int r = 0; r < pixels.length; r++) {
            for (int c = width - 1; c >= width / 2; c--) {
                leftPixel = pixels[r][c];
                rightPixel = pixels[r][(width - 1) - c];
                leftPixel.setColor(rightPixel.getColor());
            }
        }
    }

    public void mirrorHorizontal() {
        Pixel topPixel = null;
        Pixel bottomPixel = null;
        int height = pixels.length;
        int width = pixels[0].length;
        for (int r = 0; r < height / 2; r++) {
            for (int c = 0; c < width; c++) {
                topPixel = pixels[r][c];
                bottomPixel = pixels[height - 1 - r][c];
                bottomPixel.setColor(topPixel.getColor());
            }
        }
    }

    public void verticalFlip() {
        Pixel topPixel = null;
        Pixel bottomPixel = null;
        int height = pixels.length;
        int width = pixels[0].length;
        for (int r = 0; r < height / 2; r++) {
            for (int c = 0; c < width; c++) {
                topPixel = pixels[r][c];
                bottomPixel = pixels[height - 1 - r][c];
                pixels[r][c] = bottomPixel;
                pixels[height - 1 - r][c] = topPixel;
            }
        }
    }

    public void fixRoof() {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                if (p.getRed() > 200 && p.getGreen() > 200 && p.getBlue() > 200)
                    p.setRed(255);
            }
        }
    }

    public void edgeDetection(int dist) {
        Pixel[][] newPixels = new Pixel[pixels.length][pixels[0].length];
        for (int r = 1; r < pixels.length - 1; r++) {
            for (int c = 1; c < pixels[0].length - 1; c++) {
                int avgColorDiff = Math.abs(pixels[r][c].getRed() - pixels[r + 1][c].getRed())
                        + Math.abs(pixels[r][c].getGreen() - pixels[r + 1][c].getGreen())
                        + Math.abs(pixels[r][c].getBlue() - pixels[r + 1][c].getBlue());
                if (avgColorDiff > dist)
                    newPixels[r][c] = new Pixel(0, 0, 0); // black color for edges
                else
                    newPixels[r][c] = new Pixel(255, 255, 255); // white color for non-edges
            }
        }
        pixels = newPixels;
    }

    public void chromakey(Picture other, Color color, int dist) {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                if (Math.abs(p.getRed() - color.getRed()) < dist &&
                        Math.abs(p.getGreen() - color.getGreen()) < dist &&
                        Math.abs(p.getBlue() - color.getBlue()) < dist) {
                    Pixel otherPixel = other.getPixel(c, r);
                    pixels[r][c] = otherPixel;
                }
            }
        }
    }

    public void encode(Picture msg) {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                Pixel msgPixel = msg.getPixel(c, r);
                int newRed = (p.getRed() & 0xFE) | (msgPixel.getRed() >> 7);
                int newGreen = (p.getGreen() & 0xFE) | (msgPixel.getGreen() >> 7);
                int newBlue = (p.getBlue() & 0xFE) | (msgPixel.getBlue() >> 7);
                p.setRed(newRed);
                p.setGreen(newGreen);
                p.setBlue(newBlue);
            }
        }
    }

    public Picture decode() {
        Picture decoded = new Picture(pixels);
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                int red = p.getRed() & 0x01;
                int green = p.getGreen() & 0x01;
                int blue = p.getBlue() & 0x01;
                decoded.getPixel(c, r).setRed(red * 255);
                decoded.getPixel(c, r).setGreen(green * 255);
                decoded.getPixel(c, r).setBlue(blue * 255);
            }
        }
        return decoded;
    }
    public void motionBlur(int blurLength, boolean isHorizontal) {
        Pixel[][] newPixels = new Pixel[pixels.length][pixels[0].length];
        
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                int redSum = 0, greenSum = 0, blueSum = 0;
                int count = 0;
                
                if (isHorizontal) {
                    for (int i = -blurLength / 2; i <= blurLength / 2; i++) {
                        int x = c + i;
                        if (x >= 0 && x < pixels[0].length) {
                            Pixel p = pixels[r][x];
                            redSum += p.getRed();
                            greenSum += p.getGreen();
                            blueSum += p.getBlue();
                            count++;
                        }
                    }
                } else {
                    for (int i = -blurLength / 2; i <= blurLength / 2; i++) {
                        int y = r + i;
                        if (y >= 0 && y < pixels.length) {
                            Pixel p = pixels[y][c];
                            redSum += p.getRed();
                            greenSum += p.getGreen();
                            blueSum += p.getBlue();
                            count++;
                        }
                    }
                }
    
                int red = redSum / count;
                int green = greenSum / count;
                int blue = blueSum / count;
    
                newPixels[r][c] = new Pixel(red, green, blue);
            }
        }
    
        pixels = newPixels;
    }
    public void applyVignette() {
        int width = pixels[0].length;
        int height = pixels.length;
        
        // Calculate the center of the image
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Set a max vignette strength (you can adjust this as needed)
        double maxVignetteStrength = 0.8;
        
        // Apply the vignette effect
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Calculate the distance from the center
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                
                // Normalize the distance (scale it so it falls between 0 and 1)
                double maxDistance = Math.sqrt(Math.pow(centerX, 2) + Math.pow(centerY, 2));
                double distanceFactor = distance / maxDistance;
                
                // Apply vignette effect (decrease brightness based on distance)
                double vignetteStrength = Math.max(0, 1 - distanceFactor * maxVignetteStrength);
                
                // Get the pixel and adjust its color values
                Pixel p = pixels[y][x];
                int red = (int) Math.min(255, p.getRed() * vignetteStrength);
                int green = (int) Math.min(255, p.getGreen() * vignetteStrength);
                int blue = (int) Math.min(255, p.getBlue() * vignetteStrength);
                
                // Set the adjusted color values back to the pixel
                p.setRed(red);
                p.setGreen(green);
                p.setBlue(blue);
            }
        }
    }
    public void compress(int compressionLevel) {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Pixel p = pixels[r][c];
                int red = p.getRed();
                int green = p.getGreen();
                int blue = p.getBlue();
                
                // Average color value to convert to grayscale
                int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                
                // Adjust the grayscale value based on the compression level
                int compressedGray = (gray / compressionLevel) * compressionLevel;
                compressedGray = Math.min(255, compressedGray); // Ensures value stays within valid color range
                
                p.setRed(compressedGray);
                p.setGreen(compressedGray);
                p.setBlue(compressedGray);
            }
        }
    }
    
    
    
}
