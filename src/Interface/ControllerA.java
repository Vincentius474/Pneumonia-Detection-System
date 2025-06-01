package Interface;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Duration;
import similarity_classification.FeatureExtractor;
import similarity_classification.ImageProcessor;
import similarity_classification.KNNGraph;
import similarity_classification.KNNGraph.Node;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

import image_processing.*;
import information.SinglyLinkedList;

/**
 * Controller class which binds the interface and all the algorithms
 * @author RV SELLO
 */
public class ControllerA {
	
	// Controls
	@FXML private ImageView userImg;
	@FXML private ImageView chosenImgDisplay;
	@FXML private ImageView edgeEnhancedImgDisplay;
	@FXML private ImageView MOImgDisplay;
	@FXML private ImageView MEEImgDisplay;

	@FXML private ComboBox<String> imageSelector;
	@FXML private TilePane healthyImgTilePane;
	@FXML private TilePane bacterialImgTilePane;
	@FXML private TilePane viralImgTilePane;
	
	@FXML private Button btnExit;
	@FXML private Button btnProcess;
	@FXML private Button btnClassifyScan;
	
	@FXML private GridPane grdPane;
	@FXML private TextArea descriptionArea;

    @FXML private TextField bottomTextField;
    @FXML private TextField leftTextArea;
    @FXML private TextField rightTextArea;
    @FXML private TextField classificationTextField;
    @FXML private TextField similarityTextField;

    @FXML private AnchorPane rootPane;
    @FXML private ScrollPane leftScrollPane;
    @FXML private ScrollPane rightScrollPane;
    @FXML private ScrollPane topRightScrollPane;
    @FXML private ScrollPane topLeftScrollPane;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void initialize() throws IOException {
    	
    	// Display patients profile
        try {
            Image dummyImage = new Image("/data/images/download.png");
        	userImg.setImage(dummyImage);
        	userImg.setStyle("-fx-background-color: transparent;");
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
        
        // Display scans
        displayScans();

    	// Place labels text fields and areas
        bottomTextField.setText("UJ CSC03A3 Mini Project @ ACSSE 2025");
        rightTextArea.setText("Chest X-Ray Scans (Pneumonia) Classification System");
        bottomTextField.setAlignment(javafx.geometry.Pos.CENTER);
        rightTextArea.setStyle("-fx-text-alignment: center;");
        
        // Start automatic date,time updates
        startDateTimeUpdates();

        /*
         * Bind widths dynamically to divide window equally
         * Bind heights dynamically to fit within window
         * Bind GridPane width to ScrollPane viewport width
         */
        leftScrollPane.prefWidthProperty().bind(rootPane.widthProperty().divide(2).subtract(1));
        rightScrollPane.prefWidthProperty().bind(rootPane.widthProperty().divide(2).subtract(1));
        leftScrollPane.prefHeightProperty().bind(rootPane.heightProperty()); 
        rightScrollPane.prefHeightProperty().bind(leftScrollPane.prefHeightProperty());
        topLeftScrollPane.prefWidthProperty().bind(rootPane.widthProperty().divide(2).subtract(1));
        topRightScrollPane.prefWidthProperty().bind(rootPane.widthProperty().divide(2).subtract(1));
        topLeftScrollPane.prefHeightProperty().bind(rootPane.heightProperty()); 
        topRightScrollPane.prefHeightProperty().bind(leftScrollPane.prefHeightProperty());
        grdPane.prefWidthProperty().bind(topLeftScrollPane.widthProperty());
        grdPane.prefHeightProperty().bind(topLeftScrollPane.heightProperty());
        
        /*
         * List of images available in resources folder
         * Set event listener when a user selects an image
         */
        imageSelector.getItems().addAll(
        		"patient001_virus.jpeg","patient002_bacteria.jpeg","patient003_normal.jpeg",
        		"patient004_virus.jpeg", "patient005_bacteria.jpeg","patient006_normal.jpeg",
        		"patient007_virus.jpeg", "patient008_bacteria.jpeg","patient009_normal.jpeg",
        		"patient010_virus.jpeg","patient011_bacteria.jpeg","patient012_normal.jpeg"
        		);
        imageSelector.setOnAction(event -> loadImage(imageSelector.getValue()));
        
    }
    
    /**
     * Terminates the application by closing the window 
     * @param event an event that closes the window
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        // Get the current stage (window) and close it
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Perform similarity and classification check
     * @param event trigger event
     */
    @FXML
    private void processScan(ActionEvent event) {
    	try {
    		
    		File file = new File("src/data/images/patients_scans/"+imageSelector.getValue());
			ImageProcessor.ProcessImage(generateNode(imageSelector.getValue()), file, imageSelector.getValue());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @FXML
    private void classifyScan(ActionEvent event) {
    	try {
			
			similarityTextField.setText(String.valueOf(ImageProcessor.getNearestDist()));
			String[] results = ImageProcessor.getResults();
			classificationTextField.setText(results[0]);
			
	        if (results[1] != null) {
	        	Image image = new Image(getClass().getResourceAsStream("../data/images/images/" + results[1]));
	            MOImgDisplay.setImage(image);
	            Image[] processedImgs = processImage(image);
	            MEEImgDisplay.setImage(processedImgs[1]);
	        }
    		
		} catch (Exception e) {
			System.err.println("An exception occurred, could not classify scan : \n"+ e);
		}
    }
    
    /**
     * Uses the image name to display the image
     * @param imageName name of the image
     */
    private void loadImage(String imageName) {
    	
        if (imageName != null) {
        	Image image = new Image(getClass().getResourceAsStream("../data/images/patients_scans/" + imageName));
            chosenImgDisplay.setImage(image);
            Image[] processedImgs = processImage(image);
            edgeEnhancedImgDisplay.setImage(processedImgs[1]);
            
            SinglyLinkedList list = new SinglyLinkedList();

            try {
                list.readDataFromFile("src/data/documents/dummy_patient_data.txt");
                String searchID = imageName;
                descriptionArea.setText(list.searchByPatientID(searchID));
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
    }
    
    /**
     * Performs edge detection on the given image, then performs max pooling on the 
     * the results of edge detection and saves the final results as an image
     * @param imgInputPath location of the image to process
     * @param imgOutputPathA location to save edge detection results
     * @param imgOutputPathB location to save max pooling results
     */
    public Image[] processImage(Image inputImg) {
    	
    	Image[] processedImages = new Image[2];
    	processedImages[0] = inputImg;
        BufferedImage inputImage = MaxPooling.convertToBufferedImage(inputImg);
        
        // Edge enhancement
		BufferedImage edgeEnhancedBI = EdgeEnhancement.applyEdgeEnhancement(inputImage);
		Image edgeEnhancedImg = MaxPooling.convertBufferedImageToFXImage(edgeEnhancedBI);
		processedImages[1] = edgeEnhancedImg;
		
		return processedImages;
	    	
    }

    /**
     * A function for updating time every second
     */
    private void startDateTimeUpdates() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateDateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * A function to display current data and time 
     */
    private void updateDateTime() {
        String formattedDateTime = LocalDateTime.now().format(formatter);
        leftTextArea.setText("[ Dr T.K Phatudi ] Date & Time : " + formattedDateTime );
    }
    
    /**
     * Generates a node for the current scan 
     * @param imagePath location of the scan
     * @return a node
     * @throws IOException thrown when interruption occurs
     */
    private Node generateNode(String imagePath) throws IOException {
    	
    	imagePath = "src/data/images/patients_scans/" + imagePath;
    	Node tempNode = null;
    	File file = new File(imagePath);
    	
        double[] features = FeatureExtractor.extractGrayscaleHistogram(file.getAbsolutePath());
       
        BufferedImage thumb = ImageIO.read(file);
        BufferedImage resized = new BufferedImage(40, 40, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(thumb, 0, 0, 40, 40, null);
        g2d.dispose();

        tempNode = new KNNGraph.Node(file.getName(), features, resized);

    	return tempNode;
    }
    
    /**
     * Function to displays sample scans for different cases
     * @throws IOException exception thrown when interruption occurs
     */
    public void displayScans() throws IOException {
        
        File imageDir = new File("src/data/images/samples/normal");
        if (!imageDir.exists() || !imageDir.isDirectory()) {
            System.err.println("Missing 'images' directory.");
            System.exit(1);
        } 
        else {
            File[] files = imageDir.listFiles((dir, name) -> name.endsWith(".jpeg"));
            if (files == null || files.length == 0) {
                System.out.println("No images found in directory.");
                return;
            }
            for (File file : files) {
                Image image = MaxPooling.convertBufferedImageToFXImage(ImageIO.read(file));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);
                imageView.setCache(true);
                healthyImgTilePane.getChildren().add(imageView);
            }
        }
        
        imageDir = new File("src/data/images/samples/bacterial");
        if (!imageDir.exists() || !imageDir.isDirectory()) {
            System.err.println("Missing 'images' directory.");
            System.exit(1);
        } 
        else {
            File[] files = imageDir.listFiles((dir, name) -> name.endsWith(".jpeg"));
            if (files == null || files.length == 0) {
                System.out.println("No images found in directory.");
                return;
            }
            for (File file : files) {
                Image image = MaxPooling.convertBufferedImageToFXImage(ImageIO.read(file));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);
                imageView.setCache(true);
                bacterialImgTilePane.getChildren().add(imageView);
            }
        }
        
        imageDir = new File("src/data/images/samples/viral");
        if (!imageDir.exists() || !imageDir.isDirectory()) {
            System.err.println("Missing 'images' directory.");
            System.exit(1);
        } 
        else {
            File[] files = imageDir.listFiles((dir, name) -> name.endsWith(".jpeg"));
            if (files == null || files.length == 0) {
                System.out.println("No images found in directory.");
                return;
            }
            for (File file : files) {
                Image image = MaxPooling.convertBufferedImageToFXImage(ImageIO.read(file));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);
                imageView.setCache(true);
                viralImgTilePane.getChildren().add(imageView);
            }
        }
        
    }
    
    
}
