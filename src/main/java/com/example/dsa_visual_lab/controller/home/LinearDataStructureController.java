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

    // These link to the ImageViews in your FXML
    @FXML private ImageView gifArray;
    @FXML private ImageView gifQueue;
    @FXML private ImageView gifStack;
    @FXML private ImageView gifLinkedList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadGif(gifArray, "/images/array_demo.gif");
        loadGif(gifQueue, "/images/queue_demo.gif");
        // loadGif(gifStack, "/images/stack_demo.gif"); // Uncomment when you add the Stack card
        // loadGif(gifLinkedList, "/images/linkedlist_demo.gif"); // Uncomment when you add LL card
    }

    /**
     * Helper method to safely load GIFs.
     * Prevents the app from crashing if a file is missing.
     */
    private void loadGif(ImageView view, String path) {
        try {
            // Check if file exists in resources
            if (getClass().getResource(path) != null) {
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                view.setImage(image);
            } else {
                System.err.println("Warning: GIF not found at " + path);
            }
        } catch (Exception e) {
            System.err.println("Error loading GIF: " + e.getMessage());
        }
    }

    // ================= NAVIGATION LOGIC =================

    @FXML
    void onBackClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void onArrayClick(ActionEvent event) throws IOException {
        // TODO: Create array-view.fxml
        System.out.println("Navigating to Array Visualization...");
        // switchScene(event, "/com/example/dsa_visual_lab/visualizers/array-view.fxml");
    }

    @FXML
    void onQueueClick(ActionEvent event) throws IOException {
        // TODO: Create queue-view.fxml
        System.out.println("Navigating to Queue Visualization...");
        // switchScene(event, "/com/example/dsa_visual_lab/visualizers/queue-view.fxml");
    }

    // Helper to switch scenes cleanly
    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}