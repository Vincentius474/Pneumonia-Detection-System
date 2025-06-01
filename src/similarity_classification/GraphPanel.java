package similarity_classification;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used for quality graph presentation
 * @author RV SELLO, T HLOPHE, R SEBEYI
 */
public class GraphPanel extends JPanel {
	
	/**
	 * Class attributes
	 */
	private static final long serialVersionUID = -8220487148986503810L;
    private final List<KNNGraph.Node> nodes;
    private final KNNGraph.Node patientNode;
    private final int[][] positions;
    private String hoverLabel = null;
    private int hoverX = 0, hoverY = 0;
    private final Map<Rectangle, KNNGraph.Node> hitboxes = new HashMap<>();

    /**
     * Parameterized constructor
     * @param nodes list of sorted scans
     */
    public GraphPanel(List<KNNGraph.Node> nodes, String patientScanLabel, KNNGraph.Node patientNode) {
    	
        this.nodes = nodes;
        this.patientNode = patientNode;
        this.positions = new int[nodes.size()][2];
        computeNodePositions();
        setPreferredSize(new Dimension(800, 800));
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverLabel = null;
                for (Map.Entry<Rectangle, KNNGraph.Node> entry : hitboxes.entrySet()) {
                    if (entry.getKey().contains(e.getPoint())) {
                        hoverLabel = entry.getValue().imagePath;
                        hoverX = e.getX();
                        hoverY = e.getY();
                        break;
                    }
                }
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Map.Entry<Rectangle, KNNGraph.Node> entry : hitboxes.entrySet()) {
                    if (entry.getKey().contains(e.getPoint())) {
                        showFullImage(entry.getValue(), patientScanLabel);
                        break;
                    }
                }
            }
        });

        
    }

    /**
     * Computes nodes position on the graph
     */
    private void computeNodePositions() {
        int centerX = 400;
        int centerY = 400;
        int radius = 300;

        for (int i = 0; i < nodes.size(); i++) {
            double angle = 2 * Math.PI * i / nodes.size();
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            positions[i][0] = x;
            positions[i][1] = y;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        /**
         * Draw edges with distance labels
         * Clear previous frames
         * Draw nodes as thumbnails
         */
		g2.setColor(Color.DARK_GRAY);
		for (int i = 0; i < nodes.size(); i++) {
		    KNNGraph.Node n = nodes.get(i);
		    int x1 = positions[i][0];
		    int y1 = positions[i][1];
		
		    for (KNNGraph.Neighbor neighbor : n.neighbors) {
		        int j = findNodeIndex(neighbor.node.imagePath);
		        if (j != -1) {
		            int x2 = positions[j][0];
		            int y2 = positions[j][1];
		            g2.drawLine(x1, y1, x2, y2);
		            
		        }
		    }
		}

        hitboxes.clear(); 
        
        for (int i = 0; i < nodes.size(); i++) {
            KNNGraph.Node node = nodes.get(i);
            int x = positions[i][0];
            int y = positions[i][1];
            
            if(node.imagePath.equalsIgnoreCase(patientNode.imagePath)) {
            	g2.setColor(Color.RED);
            	g2.setStroke(new BasicStroke(4));
            	g2.drawRect( x - 20, y - 20,  40,  40);
            	g2.setStroke(new BasicStroke(1));
            }

            if (node.thumbnail != null) {
                g2.drawImage(node.thumbnail, x - 20, y - 20, null);
                Rectangle bounds = new Rectangle(x - 20, y - 20, 70, 50);
                hitboxes.put(bounds, node);
            }

        }
        
        if (hoverLabel != null) {
            g2.setColor(new Color(255, 255, 200));
            g2.fillRect(hoverX + 10, hoverY, 160, 20);
            g2.setColor(Color.GRAY);
            g2.drawRect(hoverX + 10, hoverY, 160, 20);
            g2.drawString(hoverLabel, hoverX + 15, hoverY + 15);
        }

    }
    
    /**
     * Displays the selected image
     * @param node - represents a scan processed
     */
    private void showFullImage(KNNGraph.Node node, String patientScanLabel) {
        try {

            BufferedImage full = null;
            
            if(node.imagePath.equalsIgnoreCase(patientScanLabel))
            {
            	full = ImageIO.read(new File("src/data/images/patients_scans/" + node.imagePath));
            }
            else {
            	full = ImageIO.read(new File("src/data/images/images/" + node.imagePath));
            }
            
            /**
             * Define the max width & height for display
             * Get original dimensions
             * Scale while keeping aspect ratio
             * Resize the image
             * Display resized image
             */
            int maxWidth = 600;  
            int maxHeight = 600;

            int width = full.getWidth();
            int height = full.getHeight();

            double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
            int newWidth = (int) (width * scale);
            int newHeight = (int) (height * scale);

            Image resizedImage = full.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedBuffered = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = resizedBuffered.createGraphics();
            g2.drawImage(resizedImage, 0, 0, null);
            g2.dispose();
 
            JLabel label = new JLabel(new ImageIcon(resizedBuffered));
            ImageIcon icon = new ImageIcon("src/data/images/icon.png");
            JOptionPane.showMessageDialog(this, label, "Viewing: " + node.imagePath, JOptionPane.PLAIN_MESSAGE, icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Finds and returns node's index
     * @param imagePath location of the scans
     * @return node's index, otherwise -1
     */
    private int findNodeIndex(String imagePath) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).imagePath.equals(imagePath)) {
                return i;
            }
        }
        return -1;
    }
}
