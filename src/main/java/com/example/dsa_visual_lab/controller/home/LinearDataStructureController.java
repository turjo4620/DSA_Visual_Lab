package com.example.dsa_visual_lab.controller.home;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class LinearDataStructureController {

    @FXML
    private void onBackClick(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();

            scene.setRoot(homeRoot);

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/com/example/dsa_visual_lab/view/styles/home.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onArrayClick(ActionEvent event) {
        navigateTo(event, "/com/example/dsa_visual_lab/view/Linear-DataStructure/array-view.fxml");
    }

    @FXML
    private void onQueueClick(ActionEvent event) {
        navigateTo(event, "/com/example/dsa_visual_lab/view/Linear-DataStructure/queue-view.fxml");
    }

    @FXML
    private void onLinkedListClick(ActionEvent event) {
        navigateTo(event, "/com/example/dsa_visual_lab/view/Linear-DataStructure/linkedlist-view.fxml");
    }

    @FXML
    private void onStackClick(ActionEvent event) {
        navigateTo(event, "/com/example/dsa_visual_lab/view/Linear-DataStructure/stack-view.fxml");
    }

    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(newRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}