package com.example.dsa_visual_lab.controller.home;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

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
    public void openDataStructures() {
        showFeatureAlert("Linear Structures (Linked List, Stack, Queue)");
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