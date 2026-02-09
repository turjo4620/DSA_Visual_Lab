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

    // ================== FIXED NAVIGATION (PRESERVES WINDOW SIZE) ==================

    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            // 1. THIS IS THE TARGET FILE.
            // Make sure "home-view.fxml" is the correct name of your Dashboard file!
            // If your dashboard file is named "main-menu.fxml", change it here.
            String fxmlPath = "/com/example/dsa_visual_lab/view/home/home-view.fxml";

            // 2. Load the file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent homeRoot = loader.load();

            // 3. Get current Stage and replace the content
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(homeRoot);

            // 4. Re-apply CSS (Optional, keeps it pretty)
            try {
                stage.getScene().getStylesheets().add(getClass().getResource("/com/example/dsa_visual_lab/view/styles/home.css").toExternalForm());
            } catch (Exception e) {
                // Ignore CSS errors for now
            }

        } catch (IOException | NullPointerException e) {
            // This will print the error if the file is still not found
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