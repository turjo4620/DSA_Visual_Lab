package com.example.dsa_visual_lab.controller.home;

import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class HomeController {

    private final String CSS_PATH = "/com/example/dsa_visual_lab/view/styles/home.css";

    @FXML
    public void initialize() {
        System.out.println("Home Page Loaded Successfully!");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/auth/login-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(loginRoot);
            scene.getStylesheets().clear();
        } catch (IOException e) {
            System.out.println("Error loading Login View:");
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
    }

    @FXML
    public void openGraphs(ActionEvent event) {
        try {
            Parent graphRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/graph/graph-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(graphRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openMST(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/MST/mst-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFeatureAlert(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feature Selected");
        alert.setHeaderText(null);
        alert.setContentText("You clicked: " + featureName + "\n\n(We will build this feature next!)");
        alert.showAndWait();
    }
}