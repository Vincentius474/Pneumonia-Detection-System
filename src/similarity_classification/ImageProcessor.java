package similarity_classification;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import similarity_classification.KNNGraph.Neighbor;
import similarity_classification.KNNGraph.Node;

/**
 * @author RV SELLO, T HLOPHE, R SEBEYI
 */
public class ImageProcessor {
	
	/**
	 * class attributes
	 */
	private static double nearestDist = 0;
	private static String[] similarScanPath = new String[3];
	
	/**
	 * Performs image classification and similarity using K-NN
	 * @param currentNode patients scan node
	 * @throws Exception thrown when exceptions occurs
	 */
    public static void ProcessImage(KNNGraph.Node currentNode, File tempFile, String patientScanLabel) throws Exception {

    	// k-nearest neighbors
        final int k = 3; 
        
        /**
         * Check if the directory containing images exists
         * Or otherwise exit the system
         */
        File imageDir = new File("src/data/images/images");
        if (!imageDir.exists() || !imageDir.isDirectory()) {
            System.err.println("Missing 'images' directory.");
            System.exit(1);
        } 
        else {
			
        	/**
        	 * Check if directory has images first
        	 * Load and extract features
        	 */
            File[] files = imageDir.listFiles((dir, name) -> name.endsWith(".jpeg"));
            if (files == null || files.length == 0) {
                System.out.println("No images found in directory.");
                return;
            }
            
            List<KNNGraph.Node> nodes = new ArrayList<>();

            for (File file : files) {
                double[] features = FeatureExtractor.extractGrayscaleHistogram(file.getAbsolutePath());
                
                BufferedImage thumb = ImageIO.read(file);
                BufferedImage resized = new BufferedImage(40, 40, BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D g2d = resized.createGraphics();
                g2d.drawImage(thumb, 0, 0, 40, 40, null);
                g2d.dispose();

                KNNGraph.Node node = new KNNGraph.Node(file.getName(), features, resized);
                node.thumbnail = resized;
                nodes.add(node);

            }
            
            /*******************************************************************************************/
            double[] features = FeatureExtractor.extractGrayscaleHistogram(tempFile.getAbsolutePath());
            
            BufferedImage thumb = ImageIO.read(tempFile);
            BufferedImage resized = new BufferedImage(40, 40, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(thumb, 0, 0, 50, 100, null);
            g2d.dispose();

            KNNGraph.Node node = new KNNGraph.Node(tempFile.getName(), features, resized);
            node.thumbnail = resized;
            nodes.add(node);
            /*******************************************************************************************/
 
            /**
             * Build k-NN graph
             * Query using an image
             * Show graph in a window
             */
            KNNGraph.buildGraph(nodes, k);
            System.out.println("k-NN graph constructed.");

            double[] queryFeatures = currentNode.features;
            List<KNNGraph.Neighbor> similar = KNNGraph.query(queryFeatures, nodes, k+1);
            
            System.out.println("List simlar length "+ similar.size());

            System.out.println("Most similar to: " + currentNode.imagePath);
            int i = 0;
            for (KNNGraph.Neighbor n : similar) {
            	
            	if(n.distance>0) {
                    System.out.printf(" -> %s (dist: %.4f)\n", n.node.imagePath, n.distance);
                    similarScanPath[i] = n.node.imagePath;
                    i++;
            	}
            	
            }
            nearestDist = similar.get(1).distance;
            
            /**
             * New list for nodes that are going to be displayed in the graph
             */
            List<KNNGraph.Node> displayNodes = new ArrayList<>();
            for(Node tempNode : nodes) {
            	for(Neighbor tempNodeB : similar) {
            		if(tempNode.imagePath.equalsIgnoreCase(tempNodeB.node.imagePath)) {
            			displayNodes.add(tempNode);
            		}
            		
            	}
            }

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("k-NN Graph Viewer");
                Image icon = Toolkit.getDefaultToolkit().getImage("../data/images/icon.png");
                frame.setIconImage(icon);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.add(new GraphPanel(displayNodes, patientScanLabel, currentNode));
                frame.pack();
                frame.setVisible(true);
            });
        	
		}


    }

    /**
     * Returns the distance of the nearest neighbor
     * @return shortest distance
     */
	public static double getNearestDist() {
		return nearestDist;
	}


	/**
	 * Returns an a list of results
	 * @return classification and scan path as a list
	 */
	public static String[] getResults() {
		
		int viral = 0;
		int bacterial = 0;
		int normal = 0;
		String classification = "None";
		
		for(String diagnosis : similarScanPath) {
			if(diagnosis.contains("virus")) {
				viral++;
			}	
			else if(diagnosis.contains("bacteria")) {
				bacterial++;
			}
			else {
				normal++;
			}
		}
		
		if(viral >= bacterial && viral > normal) {
			classification = "Pneumonia (Viral)";
		}
		else if(bacterial >= viral && bacterial > normal) {
			classification = "Pnemonia (Bacterial)";
		}
		else {
			classification = "No Pneumonia (Healthy)";
		}
		
		String[] results = {classification, similarScanPath[0]};
		return results;
		
	}
    


}
