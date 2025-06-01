package similarity_classification;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class is for feature extraction on
 * gray scale images(scans)
 * @ RV SELLO, T HLOPHE, R SEBEYI
 */
public class FeatureExtractor {
 
	/**
	 * 16-bin histogram
	 * @param imagePath location of the image being processed
	 * @return flattened gray scale values
	 * @throws IOException an exception thrown when operations fail 
	 */
    public static double[] extractGrayscaleHistogram(String imagePath) throws IOException {
        BufferedImage img = ImageIO.read(new File(imagePath));
        int[] histogram = new int[16];

        int width = img.getWidth();
        int height = img.getHeight();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int gray = (rgb >> 16) & 0xff;
                int bin = gray / 16;
                histogram[bin]++;
            }
        }

        /**
         * Normalize histogram
         */
        double[] normalized = new double[16];
        int total = width * height;
        for (int i = 0; i < 16; i++) {
            normalized[i] = (double) histogram[i] / total;
        }

        return normalized;
    }

    /**
     * Computes euclidean distance between the elements of 2 arrays
     * @param a 1st array
     * @param b 2nd array
     * @return returns sum of the euclidean distances
     */
    public static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}
