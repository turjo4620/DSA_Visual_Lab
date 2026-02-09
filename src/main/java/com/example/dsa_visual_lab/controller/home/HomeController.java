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
    protected void openDataStructures(ActionEvent event) throws IOException {
        // 1. Get the current stage (window) from the button click
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 2. Capture the current size and maximized state BEFORE switching
        double width = stage.getWidth();
        double height = stage.getHeight();
        boolean isMaximized = stage.isMaximized();

        // 3. Load the new FXML (Make sure the path matches your linear structure file!)
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml"));
        // ^ Note: adjust this path if your file is in a different folder like "view/home"

        Scene scene = new Scene(fxmlLoader.load());

        // 4. Set the new scene
        stage.setScene(scene);

        // 5. Restore the size and maximized state immediately
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMaximized(isMaximized);

        stage.show();
    }
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
