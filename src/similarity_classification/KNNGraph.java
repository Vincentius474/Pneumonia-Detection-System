package similarity_classification;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 *  k-Nearest Neighbor Graphs (k-NN)
 *  This class is used for content-based image retrieval 
 *  and similarity measures
 *  @author RV SELLO, T HLOPHE, R SEBEYI
 */
public class KNNGraph {
	
	/**
	 * Node - inner class which represents the current
	 * patient scan which is being processed
	 * Neighbor - inner class which represents other patients
	 * scan that have similar features to the current scan
	 */
    public static class Node {
        String imagePath;
        double[] features;
        BufferedImage thumbnail;
        List<Neighbor> neighbors = new ArrayList<>();

        public Node(String path, double[] feat, BufferedImage thumb) {
            this.imagePath = path;
            this.features = feat;
            this.thumbnail = thumb;
        }
    }

    public static class Neighbor {
        Node node;
        double distance;

        Neighbor(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    /**
     * Function for graph construction
     * @param nodes list of scans
     * @param k k-means value
     * @return sorted(using euclidean distance) list of scans
     */
    public static List<Node> buildGraph(List<Node> nodes, int k) {
        for (Node n1 : nodes) {
            PriorityQueue<Neighbor> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
            for (Node n2 : nodes) {
                if (n1 == n2) continue;
                double dist = FeatureExtractor.euclideanDistance(n1.features, n2.features);
                pq.offer(new Neighbor(n2, dist));
            }

            for (int i = 0; i < k && !pq.isEmpty(); i++) {
                n1.neighbors.add(pq.poll());
            }
        }
        return nodes;
    }

    /**
     * Function for filtering down possible matches of the scan being processed
     * @param queryFeatures extracted features of the scan being processed
     * @param nodes lists of possible matches of the scan being processed
     * @param k k-means value
     * @return filtered list of possible matches of the scan being processed
     */
    public static List<Neighbor> query(double[] queryFeatures, List<Node> nodes, int k) {
        PriorityQueue<Neighbor> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        for (Node node : nodes) {
            double dist = FeatureExtractor.euclideanDistance(queryFeatures, node.features);
            pq.offer(new Neighbor(node, dist));
        }

        List<Neighbor> result = new ArrayList<>();
        for (int i = 0; i < k && !pq.isEmpty(); i++) {
            result.add(pq.poll());
        }
        return result;
    }
}
