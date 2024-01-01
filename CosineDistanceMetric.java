//Author Griffin McCool
package cs1501_p5;

public class CosineDistanceMetric implements DistanceMetric_Inter{

    /**
     * Computes the distance between the RGB values of two pixels
     *
     * @param p1 the first pixel to compute the distance with
     * @param p2 the second pixel to compute the distance with
     * @return The distance between the RGB values of p1 and p2
     */
    public double colorDistance(Pixel p1, Pixel p2){
        //c(p1, p2) = 1 - ((R1*R2)+(G1*G2)+(B1*B2)) / (sqrt(R1^2 + G1^2 + B1^2) * sqrt(R2^2 + G2^2 + B2^2))
        return 1 - ((p1.getRed() * p2.getRed()) + (p1.getGreen() * p2.getGreen()) + (p1.getBlue() * p2.getBlue()))
        / (Math.sqrt(Math.pow(p1.getRed(), 2) + Math.pow(p1.getGreen(), 2) + Math.pow(p1.getBlue(), 2)) * Math.sqrt(Math.pow(p2.getRed(), 2)
        + Math.pow(p2.getGreen(), 2) + Math.pow(p2.getBlue(), 2)));
    }

}