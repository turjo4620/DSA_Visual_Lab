package com.example.dsa_visual_lab.controller.home;

import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import org.jetbrains.annotations.NotNull;

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

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();


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

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();


            scene.setRoot(homeRoot);


            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void openDataStructures(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        double width = stage.getWidth();
        double height = stage.getHeight();
        boolean isMaximized = stage.isMaximized();


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml"));

        Scene scene = new Scene(fxmlLoader.load());


        stage.setScene(scene);


        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMaximized(isMaximized);

        stage.show();
    }

    @FXML
    public void openTrees(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/BST/bst-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading BST View:");
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
