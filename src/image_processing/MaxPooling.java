package image_processing;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Max Pooling class
 * @author RV SELLO
 */
public class MaxPooling {
	
	/**
	 * Max pooling
	 * @param img image to use for max pooling
	 * @param poolSize pool size
	 * @return max pooled image
	 */
    public static BufferedImage maxPool(BufferedImage img, int poolSize) {
        int width = img.getWidth();
        int height = img.getHeight();
        
        int newWidth = width / poolSize;
        int newHeight = height / poolSize;
        
        BufferedImage pooledImage = new BufferedImage(newWidth, newHeight, img.getType());

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int maxPixel = 0; // Store max pixel value
                
                // Iterate over the pooling region
                for (int dy = 0; dy < poolSize; dy++) {
                    for (int dx = 0; dx < poolSize; dx++) {
                        int px = x * poolSize + dx;
                        int py = y * poolSize + dy;
                        
                        if (px < width && py < height) { // Check bounds
                            int pixel = img.getRGB(px, py) & 0xFF; // Extract grayscale value
                            maxPixel = Math.max(maxPixel, pixel);
                        }
                    }
                }

                // Set the max pixel value in the new image
                int rgb = (maxPixel << 16) | (maxPixel << 8) | maxPixel; // Convert to grayscale RGB
                pooledImage.setRGB(x, y, rgb);
            }
        }
        return pooledImage;
    }
    
    /**
     * Converts an instance of BufferedImage to an instance of Image
     * @param bufferedImage an instance of BufferedImage
     * @return returns an instance of Image
     */
    public static Image convertBufferedImageToFXImage(BufferedImage bufferedImage) {
        try {
            // Write BufferedImage to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            
            // Convert to ByteArrayInputStream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            
            // Create JavaFX Image
            return new Image(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Convert an Image instance in to a BufferedImage instance
     * @param fxImage Image to convert into BufferedImage instance
     * @return return BufferedImage instance
     */
    public static BufferedImage convertToBufferedImage(Image fxImage) {
        return SwingFXUtils.fromFXImage(fxImage, null);
    }

}

