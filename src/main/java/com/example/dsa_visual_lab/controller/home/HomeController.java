package com.example.dsa_visual_lab.controller.home;

import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    // This method runs automatically when the page loads
    @FXML
    public void initialize() {
        System.out.println("Home Page Loaded Successfully!");
    }

    // These methods match the onAction="#..." in your FXML file
    @FXML
    public void openSorting() {
        showFeatureAlert("Sorting Algorithms (Bubble, Merge, Quick)");
    }

    @FXML
    void openDataStructures(ActionEvent event) throws IOException {
        // 1. Load the FXML file for the Linear Data Structures page
        // MAKE SURE THIS FILE NAME MATCHES WHAT YOU SAVED
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/Linear-dataStructure/linear-dataStructures.fxml"));
        Parent root = loader.load();

        // 2. Get the current stage (window) from the button that was clicked
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 3. Set the new scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

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

    // A helper method to show a popup message
    private void showFeatureAlert(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feature Selected");
        alert.setHeaderText(null);
        alert.setContentText("You clicked: " + featureName + "\n\n(We will build this feature next!)");
        alert.showAndWait();
    }
}