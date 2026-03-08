package com.example.dsa_visual_lab.controller.home;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Button;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LinearDataStructureController implements Initializable {

    @FXML private ImageView gifArray;
    @FXML private ImageView gifQueue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load GIFs safely
        loadGif(gifArray, "/images/array_demo.gif");
        loadGif(gifQueue, "/images/queue_demo.gif");
    }

    private void loadGif(ImageView view, String path) {
        try {
            if (getClass().getResource(path) != null) {
                view.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
            }
        } catch (Exception e) {
            System.err.println("Error loading GIF: " + e.getMessage());
        }
    }


    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            String fxmlPath = "/com/example/dsa_visual_lab/view/home/home-view.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent homeRoot = loader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(homeRoot);

            try {
                stage.getScene().getStylesheets().add(getClass().getResource("/com/example/dsa_visual_lab/view/styles/home.css").toExternalForm());
            } catch (Exception e) {
            }

        } catch (IOException | NullPointerException e) {
            System.err.println("CRITICAL ERROR: Could not find the file!");
            System.err.println("Please check the file name in your project folder.");
            e.printStackTrace();
        }
    }

    @FXML
    void onArrayClick(ActionEvent event) {
        // Go TO Array Visualization
        // Note: Ensure this FXML path is correct!
        navigateTo(event, "/com/example/dsa_visual_lab/view/Linear-DataStructure/array-view.fxml");
    }

    @FXML
    void onQueueClick(ActionEvent event) {
        // Make sure this path matches where you saved the file above!
        navigateTo(event, "/com/example/dsa_visual_lab/view/Linear-DataStructure/queue-view.fxml");
    }

    @FXML
    void onLinkedListClick(ActionEvent event) {
        // Make sure this path matches exactly where you saved the LinkedList file!
        try {
            String path = "/com/example/dsa_visual_lab/view/Linear-DataStructure/linkedlist-view.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not find linkedlist-view.fxml");
        }
    }

    @FXML private Button btnStack;

    @FXML
    protected void onStackClick(ActionEvent event) {
        try {
            // 1. Load the FXML file
            // NOTE: Check that "stack-view.fxml" is in the correct folder.
            // If it's in a subfolder, use "/com/example/dsa_visual_lab/views/linear/stack-view.fxml"
            String path = "/com/example/dsa_visual_lab/view/Linear-DataStructure/stack-view.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(fxmlLoader.load());

            // 2. Get the current stage (window) from the button click event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Set the new scene and show it
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // This will print the specific error if the file path is wrong
        }
    }
    // ================== HELPER METHOD ==================
    // This single method handles all navigation without shrinking the window
    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();

            // Get current Stage and Scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            // Swap the content (Root) instead of the whole Scene
            scene.setRoot(newRoot);

            // Re-apply CSS if needed (Optional, but good for consistent button styling)
            // scene.getStylesheets().add(getClass().getResource("/com/example/dsa_visual_lab/view/styles/home.css").toExternalForm());

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}