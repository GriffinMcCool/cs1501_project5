//Author Griffin McCool
package cs1501_p5;
import java.util.*;

public class ClusteringMapGenerator implements ColorMapGenerator_Inter{
    DistanceMetric_Inter dist;

    //Constructor
    public ClusteringMapGenerator(DistanceMetric_Inter dm){
        dist = dm;
    }

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
        boolean[][] marked = new boolean[pixelArr.length][pixelArr[0].length];
        double max = 0, distance, minDist = Double.MAX_VALUE;
        int maxX = 0, maxY = 0, rgb1, rgb2;
        Pixel maxPixel = null, cur;
        // initial color
        palette[0] = pixelArr[0][0];
        marked[0][0] = true;

        // find numColors pixels for the palette
        for (int i = 1; i < numColors; i++){
            // search thru every unmarked pixel and find it's closest centroid (pixel in palette)
            for (int x = 0; x < pixelArr.length; x++){
                for (int y = 0; y < pixelArr[0].length; y++){
                    // if the pixel hasn't been seen yet...
                    if (!marked[x][y]){
                        cur = pixelArr[x][y];
                        minDist = Double.MAX_VALUE;
                        int j = 0;
                        // loop through each pixel in palette to find closest centroid
                        while ((j < palette.length) && (palette[j] != null)){
                            distance = dist.colorDistance(palette[j], cur);
                            // if distance is less than the previously found closest centroid, update minDist
                            if (distance < minDist){
                                minDist = distance;
                            }
                            j++;
                        }
                        // if the distance between the current palette pixel and the pixel in the
                        // image is greater than the max, update max
                        if (minDist > max){
                            if (!seen(cur, palette)){
                                max = minDist;
                                maxPixel = cur;
                                maxX = x;
                                maxY = y;
                            }
                        }
                        // if the distances are the same, go by highest RGB value
                        if (minDist == max){
                            if (!seen(cur, palette)){
                                if (maxPixel == null){
                                    maxPixel = cur;
                                    maxX = x;
                                    maxY = y;
                                } else {
                                    rgb1 = (maxPixel.getRed() << 16) | (maxPixel.getGreen() << 8) | (maxPixel.getBlue());
                                    rgb2 = (cur.getRed() << 16) | (cur.getGreen() << 8) | (cur.getBlue());
                                    if (rgb2 > rgb1){
                                        maxPixel = cur;
                                        maxX = x;
                                        maxY = y;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            palette[i] = maxPixel;
            marked[maxX][maxY] = true;
            maxPixel = null;
            max = 0;
            maxX = 0;
            maxY = 0;
        }
        return palette;
    }

    /**
     * Helper method for generateColorPalette()
     * 
     * @param cur current pixel
     * @param arr current palette
     * 
     * @return whether the pixel with the same RGB value has been seen yet
     */
    private boolean seen(Pixel cur, Pixel[] arr){
        for (int i = 0; i < arr.length; i++){
            if (arr[i] == null) break;
            if (arr[i].equals(cur)) return true;
        }
        return false;
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
        double distance, count, redTotal, greenTotal, blueTotal, minDist = Double.MAX_VALUE;
        int closest = 0, redAvg, greenAvg, blueAvg;
        Map<Pixel,Integer> map = new HashMap();
        Pixel cur;
        boolean loop = true;
        // keep looping until convergence (palette does not change)
        while (loop){
            map = new HashMap();
            // group pixels into clusters by finding closest centroid to each pixel
            for (int x = 0; x < pixelArr.length; x++){
                for (int y = 0; y < pixelArr[0].length; y++){
                    cur = pixelArr[x][y];
                    minDist = Double.MAX_VALUE;
                    int j = 0;
                    // loop through each pixel in palette to find closest centroid
                    while ((j < initialColorPalette.length) && (initialColorPalette[j] != null)){
                        distance = dist.colorDistance(initialColorPalette[j], cur);
                        // if distance is less than the previously found closest centroid, update minDist
                        if (distance < minDist){
                            minDist = distance;
                            closest = j;
                        }
                        j++;
                    }
                    // map the pixel to the index of its closest centroid
                    map.put(cur, closest);
                }
            }

            // compute new centroids (wow the runtime on this is gonna be so bad)
            for (int i = 0; i < initialColorPalette.length; i++){
                count = 0;
                redTotal = 0;
                greenTotal = 0;
                blueTotal = 0;
                // loop thru every pixel to get averages
                for (int r = 0; r < pixelArr.length; r++){
                    for (int c = 0; c < pixelArr[0].length; c++){
                        cur = pixelArr[r][c];
                        // if the pixel maps to the current cluster, include in the average
                        if (map.get(cur) == i){     
                            count++;
                            redTotal += cur.getRed();
                            greenTotal += cur.getGreen();
                            blueTotal += cur.getBlue();
                        }
                    }
                }
                redAvg = (int)(redTotal/(double)count);
                greenAvg = (int)(greenTotal/(double)count);
                blueAvg = (int)(blueTotal/(double)count);
                loop = false;
                if (initialColorPalette[i] != null){
                    // continue loop if any of the colors are not equal to those of the current palette
                    if (initialColorPalette[i].getRed() != redAvg || initialColorPalette[i].getGreen() != greenAvg || initialColorPalette[i].getBlue() != blueAvg){
                        loop = true;
                    }
                }
                // update the color palette's centroids to be the average color of the
                // pixels currently in each cluster
                initialColorPalette[i] = new Pixel(redAvg, greenAvg, blueAvg);
            }
        }
        // after the while loop, initialColorPalette now has the palette we want, so we
        // just have to map the pixels to the centroids instead of the index of the centroids
        Map colorMap = new HashMap();
        for (int row = 0; row < pixelArr.length; row++){
            for (int col = 0; col < pixelArr[0].length; col++){
                cur = pixelArr[row][col];
                colorMap.put(cur, initialColorPalette[map.get(cur)]);
            }
        }
        return colorMap;
    }

}