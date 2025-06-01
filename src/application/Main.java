package application;
	
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


/**
 * Driver class
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			/**
			 * Check if all essential directories containing files exists
			 */
			String[] listDirectories = {"src/data/documents","src/data/images",
										"src/data/images/images","src/data/images/patients_scans",
										"src/data/images/samples"};
			for(String dirPathString : listDirectories) {
				checkDirectory(dirPathString);
			}
			System.out.println("----------------------------------------------------------\n");
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Interface/UserInterface.fxml"));
			
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			/*
			 * Set the icon of the application
			 * Set application title
			 * Set window to max
			 */
	        try {
	        	Image icon = new Image(getClass().getResourceAsStream("../data/images/icon.png"));
	        	primaryStage.getIcons().add(icon);
	        } catch (Exception e) {
	            System.out.println("Error loading image: " + e.getMessage());
	        }
			primaryStage.setTitle("Chest X-Ray Scans (Pneumonia) Classification System");
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * A function to check if a directory exists and it contains file
	 * @param path location of the directory
	 */
    public static void checkDirectory(String path) {
        Path dirPath = Path.of(path);

        if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dirPath)) {
                if (dirStream.iterator().hasNext()) {
                    System.out.println("The directory '" + path + "' exists and contains files.");
                } else {
                    System.out.println("The directory '" + path + "' exists but is empty.");
                }
            } catch (IOException e) {
                System.out.println("Error reading directory: " + e.getMessage());
            }
        } else {
            System.out.println("The directory '" + path + "' does not exist.");
        }
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
