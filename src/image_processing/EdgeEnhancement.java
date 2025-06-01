package image_processing;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This class is used to perform edge enhancement
 * @author RV SELLO
 */
public class EdgeEnhancement {

	/**
	 * Performs edge enhancement
	 * @param image image to enhance edges
	 * @return an enhanced image
	 */
    public static BufferedImage applyEdgeEnhancement(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Laplacian kernel for edge enhancement
        int[][] kernel = {
            {  0, -1,  0 },
            { -1,  5, -1 },
            {  0, -1,  0 }
        };

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int newPixel = 0;

                // Convolution operation
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = new Color(image.getRGB(x + i, y + j)).getRed();
                        newPixel += pixel * kernel[i + 1][j + 1];
                    }
                }

                // Normalize pixel value
                newPixel = Math.min(Math.max(newPixel, 0), 255);
                result.setRGB(x, y, new Color(newPixel, newPixel, newPixel).getRGB());
            }
        }

        return result;
    }
	
}
