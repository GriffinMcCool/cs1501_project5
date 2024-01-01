//Author Griffin McCool
package cs1501_p5;

import java.util.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.awt.Color;

public class ColorQuantizer implements ColorQuantizer_Inter{
    private Map<Pixel,Pixel> cMap;
    private Pixel[] palette;
    private Pixel[][] pixels;
    private ColorMapGenerator_Inter gen;

    //First constructor
    public ColorQuantizer(Pixel[][] pixelArr, ColorMapGenerator_Inter c){
        pixels = pixelArr;
        gen = c;
    }

    //Second constructor
    public ColorQuantizer(String filename, ColorMapGenerator_Inter c){
        try {
            // Load bitmap image
            BufferedImage image = ImageIO.read(new File(filename));

            // Create pixel matrix
            Pixel[][] pixelArr = convertBitmapToPixelMatrix(image);
            pixels = pixelArr;
            gen = c;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method for constructor (code borrowed from App.java)
     * 
     * @param image image to be converted to a pixel array
     * 
     * @return pixel array representation of image
     */
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

    /**
     * Performs color quantization.
     *
	 * @param numColors number of colors to use for color quantization
     * @return A two dimensional array where each index represents the pixel from the original bitmap
     *         image and contains Pixel representing its color after quantization
     */
    public Pixel[][] quantizeTo2DArray(int numColors){
        Pixel[][] quantizedPixels = new Pixel[pixels.length][pixels[0].length];
        if (numColors == 0) return quantizedPixels;
        palette = gen.generateColorPalette(pixels, numColors);
        cMap = gen.generateColorMap(pixels, palette);

        for (int i = 0; i < pixels.length; i ++){

            for (int j = 0; j < pixels[0].length; j++){
                quantizedPixels[i][j] = cMap.get(pixels[i][j]);
            }

        }
        return quantizedPixels;
    }

    /**
     * Performs color quantization (but saves to a file!). Should perform quantization like
	 * quantizeToArray, but instead of returning a 2D Pixel array, returns nothing and writes
	 * the resulting image to a file.
	 *
	 * @param numColors number of colors to use for color quantization
	 * @param fileName File to write resulting image to
     */
	public void quantizeToBMP(String fileName, int numColors){
        // if numColors is 0, just return as there will be no image
        if (numColors == 0) return;
        // A lot of this code here is from TA Marcelo via the discord
        Pixel[][] quantizedPixels = quantizeTo2DArray(numColors);
        int width = quantizedPixels.length;
        int height = quantizedPixels[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Pixel pixel;
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                pixel = quantizedPixels[j][i];
                Color color = new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue());
                image.setRGB(j, i, color.getRGB());
            }
        }
        try {
            File file = new File(fileName);
            ImageIO.write(image, "bmp", file);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}