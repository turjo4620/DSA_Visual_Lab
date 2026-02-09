package com.example.dsa_visual_lab.controller.home;

import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class HomeController {

    private final String CSS_PATH = "/com/example/dsa_visual_lab/view/styles/home.css";



    @FXML
    public void initialize() {
        System.out.println("Home Page Loaded Successfully!");
    }

    // ================= Sorting Page =================
    @FXML
    private void openSorting(ActionEvent event) {
        try {
            Parent sortingRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/Sorting/sorting-view.fxml"));

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            // Replace root
            scene.setRoot(sortingRoot);

            // Reapply CSS so buttons keep style
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= Back Button =================
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            // Replace root
            scene.setRoot(homeRoot);

            // Reapply CSS
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= Linear Data Structures =================
    @FXML
    void openDataStructures(@NotNull ActionEvent event) throws IOException {
// 1. Load the FXML file for the Linear Data Structures page
// MAKE SURE THIS FILE NAME MATCHES WHAT YOU SAVED
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/Linear-dataStructure/linear-dataStructures.fxml")); Parent root = loader.load();
// 2. Get the current stage (window) from the button that was clicked
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // 3. Set the new scene
        Scene scene = new Scene(root); stage.setScene(scene); stage.show(); }
    // ================= Other Features =================
    @FXML
    public void openTrees() {
        showFeatureAlert("Tree Algorithms (BST, AVL)");
    }

    @FXML
    public void openGraphs() {
        showFeatureAlert("Graph Algorithms (BFS, DFS)");
    }

    @FXML
    public void openMST() {
        showFeatureAlert("Minimum Spanning Trees");
    }

    @FXML
    public void openDP() {
        showFeatureAlert("Dynamic Programming");
    }

    private void showFeatureAlert(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feature Selected");
        alert.setHeaderText(null);
        alert.setContentText("You clicked: " + featureName + "\n\n(We will build this feature next!)");
        alert.showAndWait();
    }
}
