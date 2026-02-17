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

    // ================= Sorting Buttons =================
    @FXML
    private void handleBubbleSort(ActionEvent event) {
        showAlert("Visualizing Bubble Sort");
    }

    @FXML
    private void handleMergeSort(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(
                    "/com/example/dsa_visual_lab/view/Sorting/merge-sort-view.fxml"));
            Parent mergeRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            // Replace root instead of creating a new Scene
            scene.setRoot(mergeRoot);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load Merge Sort Visualization!");
        }
    }


    @FXML
    private void handleQuickSort(ActionEvent event) {
        showAlert("Visualizing Quick Sort");
    }

    @FXML
    private void handleInsertionSort(ActionEvent event) {
        showAlert("Visualizing Insertion Sort");
    }
    @FXML
    private void handleSelectionSort(ActionEvent event) {
        showAlert("Visualizing Selection Sort");
    }
    // ================= Back Button =================
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            // Replace root instead of creating a new Scene
            scene.setRoot(homeRoot);

            // Reapply CSS so buttons keep style
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/com/example/dsa_visual_lab/view/styles/home.css").toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load Home Page!");
        }
    }


    // ================= Helper Method =================
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sorting Visualization");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n\n(Here you can implement actual visualization!)");
        alert.showAndWait();
    }

}
