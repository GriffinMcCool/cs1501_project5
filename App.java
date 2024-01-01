/**
 * A driver for CS1501 Project 5
 * @author	Dr. Farnan
 */

package cs1501_p5;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        Pixel[][] pixelMatrix = null;
        try {
            // Load bitmap image
            BufferedImage image = ImageIO.read(new File("build/resources/main/image.bmp"));

            // Create pixel matrix
            pixelMatrix = convertBitmapToPixelMatrix(image);

            // Save pixel matrix to file
            savePixelMatrixToFile("build/resources/main/pixel_matrix.txt", pixelMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("HI");
        
        DistanceMetric_Inter dm;
        ColorMapGenerator_Inter generator;
        ColorQuantizer cq;

        Pixel p1 = new Pixel(1, 255, 1);
        Pixel p2 = new Pixel(255, 1, 255);
        dm = new CosineDistanceMetric();

        // use BigDecimal for rounding returned result
        double result = dm.colorDistance(p1, p2);
        BigDecimal bd = new BigDecimal(Double.toString(result));
        bd = bd.setScale(5, RoundingMode.HALF_UP);

        result = dm.colorDistance(p2, p1);
        bd = new BigDecimal(Double.toString(result));
        bd = bd.setScale(5, RoundingMode.HALF_UP);

        Pixel[][] stripedArr = null;
        BucketingMapGenerator generator2 = new BucketingMapGenerator();

        // check 1 color
        Pixel[] result2 = generator2.generateColorPalette(stripedArr, 7);
        for (int i = 0; i < 7; i++) System.out.println("Pixel: (" + result2[i].getRed() + "," + result2[i].getGreen() + "," + result2[i].getBlue() + ")");

        System.out.println("------------------------------------");
        ClusteringMapGenerator generator3 = new ClusteringMapGenerator(new CosineDistanceMetric());
        stripedArr = genStripedArr();
        result2 = generator3.generateColorPalette(pixelMatrix, 10);
        for (int i = 0; i < 10; i++) System.out.println("Pixel: (" + result2[i].getRed() + "," + result2[i].getGreen() + "," + result2[i].getBlue() + ")");
        System.out.println("------------------------------------");

        System.out.println("Testing cq");
        cq = new ColorQuantizer("build/resources/main/image.bmp", generator3);
        //cq = new ColorQuantizer("src/test/resources/test.bmp", generator3);
        //cq = new ColorQuantizer(stripedArr, generator3);
        cq.quantizeToBMP("src/main/java/cs1501_p5/testimage.bmp", 56);

        p1 = new Pixel(51, 52, 53);
        p2 = new Pixel(100, 100, 100);
        CosineDistanceMetric cdm = new CosineDistanceMetric();
        System.out.println(cdm.colorDistance(p1,p2));

        SquaredEuclideanMetric sdm = new SquaredEuclideanMetric();
        ClusteringMapGenerator cm = new ClusteringMapGenerator(sdm);
        Pixel[] arr = cm.generateColorPalette(genStripedArr(),5);
        for(int i = 0; i < arr.length; i++){
            System.out.println(arr[i]);
        }

    }

    // public static void savePixelMatrixToBitmap(String filePath, Pixel[][] pixelMatrix) {
    //     int width = pixelMatrix.length;
    //     int height = pixelMatrix[0].length;
    //     BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    //     Pixel pixel;
    //     for (int y = 0; y < height; y++) {
    //         for (int x = 0; x < width; x++) {
    //             pixel = pixelMatrix[x][y];
    //             Color color = new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue());
    //             image.setRGB(x, y, color.getRGB());
    //         }
    //     }
    //     try {
    //         File file = new File(filePath);
    //         ImageIO.write(image, "bmp", file);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    public static Pixel[][] convertBitmapToPixelMatrix(BufferedImage image) {
        Pixel[][] pixelMatrix = new Pixel[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                pixelMatrix[x][y] = new Pixel(red, green, blue);
            }
        }

        return pixelMatrix;
    }

    public static Pixel[][] genStripedArr() {
        return new Pixel[][]{
                {new Pixel(5, 5, 5), new Pixel(5, 5, 5), new Pixel(5, 5, 5)},
                {new Pixel(50, 50, 50), new Pixel(50, 50, 50), new Pixel(50, 50, 50)},
                {new Pixel(100, 100, 100), new Pixel(100, 100, 100), new Pixel(100, 100, 100)},
                {new Pixel(150, 150, 150), new Pixel(150, 150, 150), new Pixel(150, 150, 150)},
                {new Pixel(200, 200, 200), new Pixel(200, 200, 200), new Pixel(200, 200, 200)},
                {new Pixel(250, 250, 250), new Pixel(250, 250, 250), new Pixel(250, 250, 250)}
        };
    }

    public static void savePixelMatrixToFile(String filePath, Pixel[][] matrix) {

        try {
            // Open file for writing
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            // Write matrix to file
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    writer.write(matrix[i][j] + String.valueOf('\t'));
                }
                writer.newLine();
            }

            // Close file
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
