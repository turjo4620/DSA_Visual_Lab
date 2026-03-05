package com.example.dsa_visual_lab.controller.sorting;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;

public class SelectionSortController {

    @FXML
    private HBox arrayContainer;
    @FXML private Label line1;
    @FXML private Label line2;
    @FXML private Label line3;
    @FXML private Label line4;
    @FXML private Label line5;

    private int[] array = {30, 10, 50, 20, 60, 40, 80, 70, 90, 15, 35, 55};
    private Rectangle[] bars;

    private static final int BAR_WIDTH = 40;
    private static final int SCALE = 3;

    @FXML
    public void initialize() {
        drawArray();
    }

    // Draw bars
    private void drawArray() {
        bars = new Rectangle[array.length];
        arrayContainer.getChildren().clear();

        for (int i = 0; i < array.length; i++) {
            Rectangle bar = new Rectangle(BAR_WIDTH, array[i] * SCALE);
            bar.setFill(Color.CORNFLOWERBLUE);

            bars[i] = bar;
            arrayContainer.getChildren().add(bar);
        }
    }

    // Start button
    @FXML
    private void handleStart() {

        new Thread(() -> {
            try {
                for (int i = 0; i < array.length - 1; i++) {

                    int minIndex = i; // set first unsorted element as minimum
                    int finalI = i; // for lambdas

                    // Highlight pseudocode: set minIndex
                    Platform.runLater(() -> highlight(line2));

                    Thread.sleep(400);

                    for (int j = i + 1; j < array.length; j++) {

                        int currentJ = j;
                        int currentMinIndex = minIndex; // effectively final for lambda

                        // Highlight pseudocode: for j loop
                        Platform.runLater(() -> highlight(line3));
                        Thread.sleep(200);

                        // Highlight comparison
                        Platform.runLater(() -> highlight(line4));

                        Thread.sleep(200);

                        // Highlight current min and current element
                        Platform.runLater(() -> {
                            bars[currentJ].setFill(Color.ORANGE);       // current comparing
                            bars[currentMinIndex].setFill(Color.RED);   // current minimum
                        });

                        Thread.sleep(400);

                        // Update minimum if found
                        if (array[j] < array[minIndex]) {
                            int oldMin = minIndex;
                            minIndex = j;

                            int finalOldMin = oldMin;
                            int finalNewMin = minIndex;

                            Platform.runLater(() -> {
                                highlight(line5); // pseudocode: minIndex = j
                                if (finalOldMin != finalI) {
                                    bars[finalOldMin].setFill(Color.CORNFLOWERBLUE); // reset old min
                                }
                                bars[finalNewMin].setFill(Color.RED); // new min
                            });

                            Thread.sleep(400);
                        }

                        // Reset current comparing bar if not the min
                        int finalMinIndex = minIndex;
                        Platform.runLater(() -> {
                            if (currentJ != finalMinIndex) {
                                bars[currentJ].setFill(Color.CORNFLOWERBLUE);
                            }
                        });
                    }

                    // Swap the found minimum with first unsorted
                    if (minIndex != i) {
                        int temp = array[i];
                        array[i] = array[minIndex];
                        array[minIndex] = temp;

                        int finalMinIndex = minIndex;

                        Platform.runLater(() -> {
                            double tempHeight = bars[finalI].getHeight();
                            bars[finalI].setHeight(bars[finalMinIndex].getHeight());
                            bars[finalMinIndex].setHeight(tempHeight);

                            bars[finalI].setFill(Color.LIMEGREEN);          // sorted
                            bars[finalMinIndex].setFill(Color.CORNFLOWERBLUE); // reset swapped
                        });

                        Thread.sleep(400);
                    } else {
                        // If no swap, mark sorted
                        Platform.runLater(() -> bars[finalI].setFill(Color.LIMEGREEN));
                    }
                }

                // Mark last element as sorted
                Platform.runLater(() -> bars[array.length - 1].setFill(Color.LIMEGREEN));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Reset button
    @FXML
    private void handleReset() {

        array = new int[]{30, 10, 50, 20, 60, 40, 80, 70, 90, 15, 35, 55};
        drawArray();
    }
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "/com/example/dsa_visual_lab/view/Sorting/sorting-view.fxml"
            ));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void highlight(Label label) {
        Platform.runLater(() -> {
            resetHighlights();
            label.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 14px; -fx-font-weight: bold;");
        });
    }

    private void resetHighlights() {
        line1.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line2.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line3.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line4.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line5.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    }
}