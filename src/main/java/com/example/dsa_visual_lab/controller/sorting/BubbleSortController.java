package com.example.dsa_visual_lab.controller.sorting;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class BubbleSortController {

    @FXML
    private Pane visualizationPane;

    private int[] array;
    private Rectangle[] bars;

    private final int SIZE = 12;
    private final double BAR_WIDTH = 40;
    private final double SPACING = 10;
    private final double PANE_HEIGHT = 350;

    @FXML
    public void initialize() {
        generateArray();
        drawBars();
    }

    private void generateArray() {
        array = new int[SIZE];
        Random rand = new Random();
        for (int i = 0; i < SIZE; i++) {
            array[i] = rand.nextInt(200) + 50; // height 50-250
        }
    }

    private void drawBars() {
        visualizationPane.getChildren().clear();
        bars = new Rectangle[SIZE];

        for (int i = 0; i < SIZE; i++) {
            Rectangle rect = new Rectangle(BAR_WIDTH, array[i]);
            rect.setFill(Color.CORNFLOWERBLUE);
            rect.setX(i * (BAR_WIDTH + SPACING) + 20);
            rect.setY(PANE_HEIGHT - array[i]);
            bars[i] = rect;
            visualizationPane.getChildren().add(rect);
        }
    }

    @FXML
    private void startSort() {
        new Thread(() -> {
            try {
                for (int i = 0; i < SIZE - 1; i++) {
                    for (int j = 0; j < SIZE - i - 1; j++) {
                        final int a = j;
                        final int b = j + 1;

                        // Highlight bars
                        javafx.application.Platform.runLater(() -> {
                            bars[a].setFill(Color.ORANGE);
                            bars[b].setFill(Color.ORANGE);
                        });
                        Thread.sleep(400);

                        // Swap if needed
                        if (array[a] > array[b]) {
                            int temp = array[a];
                            array[a] = array[b];
                            array[b] = temp;

                            javafx.application.Platform.runLater(() -> {
                                double x1 = bars[a].getX();
                                double x2 = bars[b].getX();

                                bars[a].setX(x2);
                                bars[b].setX(x1);

                                Rectangle tempRect = bars[a];
                                bars[a] = bars[b];
                                bars[b] = tempRect;
                            });
                            Thread.sleep(400);
                        }

                        // Reset color
                        javafx.application.Platform.runLater(() -> {
                            bars[a].setFill(Color.CORNFLOWERBLUE);
                            bars[b].setFill(Color.CORNFLOWERBLUE);
                        });
                        Thread.sleep(50);
                    }

                    // Mark last element of pass as sorted
                    final int sortedIndex = SIZE - i - 1;
                    javafx.application.Platform.runLater(() -> bars[sortedIndex].setFill(Color.LIMEGREEN));
                }

                // First element sorted at the end
                javafx.application.Platform.runLater(() -> bars[0].setFill(Color.LIMEGREEN));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void resetArray() {
        generateArray();
        drawBars();
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
}