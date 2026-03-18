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

    private final String CSS_PATH = "/com/example/dsa_visual_lab/view/styles/home.css";

    @FXML
    public void initialize() {
        System.out.println("Home Page Loaded Successfully!");
    }

    @FXML
    private void openSorting(ActionEvent event) {
        try {
            Parent sortingRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/Sorting/sorting-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(sortingRoot);

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(homeRoot);

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void openDataStructures(ActionEvent event) {
        try {
            Parent linearRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(linearRoot);
        } catch (IOException e) {
            System.out.println("Error loading Linear Data Structures View:");
            e.printStackTrace();
        }
    }

    @FXML
    public void openTrees(ActionEvent event) {
        try {
            Parent bstRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/BST/bst-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(bstRoot);
        } catch (IOException e) {
            System.out.println("Error loading BST View:");
            e.printStackTrace();
        }
    }

    @FXML
    public void openDP(ActionEvent event) {
        try {
            Parent dpRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/dp/knapsack-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(dpRoot);
        } catch (IOException e) {
            System.out.println("Error loading Knapsack View:");
            e.printStackTrace();
        }
    }

    @FXML
    public void openGraphs() {
        showFeatureAlert("Graph Algorithms (BFS, DFS)");
    }

    @FXML
    public void openMST() {
        showFeatureAlert("Minimum Spanning Trees");
    }

    private void showFeatureAlert(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feature Selected");
        alert.setHeaderText(null);
        alert.setContentText("You clicked: " + featureName + "\n\n(We will build this feature next!)");
        alert.showAndWait();
    }
}