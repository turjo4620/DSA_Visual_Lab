package com.example.dsa_visual_lab.controller.home;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

public class SortingController {

    @FXML
    public void initialize() {
        System.out.println("Sorting Page Loaded Successfully!");
    }

    @FXML
    private void handleBubbleSort(ActionEvent event) {
        navigateTo(event, "/com/example/dsa_visual_lab/view/Sorting/bubble-sort-view.fxml");
    }

    @FXML
    private void handleSelectionSort(ActionEvent event) {
        navigateTo(event, "/com/example/dsa_visual_lab/view/Sorting/selection-sort-view.fxml");
    }

    @FXML
    private void handleMergeSort(ActionEvent event) {
        navigateTo(event, "/com/example/dsa_visual_lab/view/Sorting/merge-sort-view.fxml");
    }

    @FXML
    private void handleQuickSort(ActionEvent event) {
        navigateTo(event, "/com/example/dsa_visual_lab/view/Sorting/quick-sort-view.fxml");
    }

    @FXML
    private void handleInsertionSort(ActionEvent event) {
        showAlert("Visualizing Insertion Sort");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            scene.setRoot(homeRoot);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/com/example/dsa_visual_lab/view/styles/home.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load Home Page!");
        }
    }

    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load page: " + fxmlPath);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sorting Visualization");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n\n(Here you can implement actual visualization!)");
        alert.showAndWait();
    }
}