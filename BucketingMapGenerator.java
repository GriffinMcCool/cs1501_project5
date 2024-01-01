//Author Griffin McCool
package cs1501_p5;
import java.util.*;

public class BucketingMapGenerator implements ColorMapGenerator_Inter{

    /**
	 * Produces an initial palette. This initial palette will be
	 * the centers of the buckets and the starting centroids of clusters.
	 * 
     * @param pixelArr the 2D Pixel array that represents the bitmap image
     * @param numColors the number of desired colors in the palette
     * @return A Pixel array containing numColors elements
	 */
	public Pixel[] generateColorPalette(Pixel[][] pixelArr, int numColors){
        Pixel[] palette = new Pixel[numColors];
        if (numColors == 0) return palette;
        double possibleColors = 16777216;
        double resultDouble, min, max = 0, numColorsDouble = (double)numColors;
        int result, red, green, blue;
        for (int i = 1; i <= numColors; i++){
            min = max;
            max = i * (possibleColors/numColors);
            resultDouble = (max+min)/2;
            result = (int)resultDouble;
            red = (result >> 16) & 0xFF;
            green = (result >> 8) & 0xFF;
            blue = result & 0xFF;
            palette[i-1] = new Pixel(red, green, blue);
        }
        return palette;
    }

    /**
     * Computes the reduced color map. For bucketing, this will map each color
	 * to the center of its bucket, for clustering, this maps examples to final
	 * centroids.
     *
     * @param pixelArr the pixels array that represents the bitmap image
	 * @param initialColorPalette an array of Pixels generated by generateColorPalette
     * @return A Map that maps each distinct color in pixelArr to a final color 
     */
    public Map<Pixel,Pixel> generateColorMap(Pixel[][] pixelArr, Pixel[] initialColorPalette){
        if (initialColorPalette.length == 0) return null;
        Map colorMap = new HashMap();
        int rgb;
        double possibleColors = 16777216, numBuckets = initialColorPalette.length;
        double bucketRange = possibleColors/numBuckets;
        int index;
        for (int i = 0; i < pixelArr.length; i ++){

            for (int j = 0; j < pixelArr[0].length; j++){
                Pixel p = pixelArr[i][j];
                rgb = (p.getRed() << 16) | (p.getGreen() << 8) | (p.getBlue());
                index = (int)(rgb/bucketRange);
                Pixel newp = initialColorPalette[index];
                colorMap.put(p, newp);
            }

        }
        return colorMap;
    }
    
}