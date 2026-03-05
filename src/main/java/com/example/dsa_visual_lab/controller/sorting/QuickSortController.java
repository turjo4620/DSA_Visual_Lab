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
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;

public class QuickSortController {

    @FXML
    private HBox arrayContainer;

    @FXML private Label line1;
    @FXML private Label line2;
    @FXML private Label line3;
    @FXML private Label line4;
    @FXML private Label line5;
    @FXML private Label line6;

    private int[] array = {30, 10, 50, 20, 60, 40, 80, 70, 90, 15, 35, 55};
    private Rectangle[] bars;

    private static final int BAR_WIDTH = 40;
    private static final int SCALE = 3;

    @FXML
    public void initialize() {
        drawArray();
    }

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

    @FXML
    private void handleStart() {

        new Thread(() -> {

            try {

                quickSort(0, array.length - 1);

                // AFTER SORTING → MAKE EVERYTHING GREEN
                Platform.runLater(() -> {
                    for (Rectangle bar : bars) {
                        bar.setFill(Color.LIMEGREEN);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void quickSort(int low, int high) throws InterruptedException {

        Platform.runLater(() -> highlight(line1));
        Thread.sleep(300);

        Platform.runLater(() -> highlight(line2));
        Thread.sleep(300);

        if (low < high) {

            Platform.runLater(() -> highlight(line3));
            Thread.sleep(300);

            int pivotIndex = partition(low, high);

            Platform.runLater(() -> highlight(line6));
            Thread.sleep(300);

            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        }
    }

    private int partition(int low, int high) throws InterruptedException {

        int pivot = array[high];
        int i = low - 1;

        final int pivotIndex = high;

        Platform.runLater(() -> {
            highlight(line4);
            bars[pivotIndex].setFill(Color.RED);
        });

        Thread.sleep(400);

        for (int j = low; j < high; j++) {

            final int currentJ = j;

            Platform.runLater(() -> {
                highlight(line5);
                bars[currentJ].setFill(Color.ORANGE);
            });

            Thread.sleep(400);

            if (array[j] < pivot) {

                i++;
                final int swapI = i;

                int temp = array[swapI];
                array[swapI] = array[j];
                array[j] = temp;

                Platform.runLater(() -> {

                    double tempHeight = bars[swapI].getHeight();
                    bars[swapI].setHeight(bars[currentJ].getHeight());
                    bars[currentJ].setHeight(tempHeight);

                    bars[swapI].setFill(Color.RED);
                });

                Thread.sleep(400);
            }

            final int resetIndex = j;
            final int currentI = i;

            Platform.runLater(() -> {

                if (resetIndex != currentI) {
                    bars[resetIndex].setFill(Color.CORNFLOWERBLUE);
                }

            });
        }

        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;

        final int pivotFinal = i + 1;
        final int highIndex = high;

        Platform.runLater(() -> {

            double tempHeight = bars[pivotFinal].getHeight();
            bars[pivotFinal].setHeight(bars[highIndex].getHeight());
            bars[highIndex].setHeight(tempHeight);

            bars[pivotFinal].setFill(Color.LIMEGREEN);
            bars[highIndex].setFill(Color.CORNFLOWERBLUE);

        });

        Thread.sleep(400);

        return pivotFinal;
    }

    @FXML
    private void handleReset() {

        array = new int[]{30, 10, 50, 20, 60, 40, 80, 70, 90, 15, 35, 55};
        drawArray();
        resetHighlights();
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

            label.setStyle(
                    "-fx-text-fill: #22c55e; -fx-font-size: 14px; -fx-font-weight: bold;"
            );
        });
    }

    private void resetHighlights() {

        line1.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line2.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line3.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line4.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line5.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        line6.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    }
}