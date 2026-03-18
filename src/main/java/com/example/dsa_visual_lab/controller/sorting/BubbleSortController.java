package com.example.dsa_visual_lab.controller.sorting;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class BubbleSortController {

    @FXML
    private Pane visualizationPane;

    private int[] array;
    private VBox[] bars;

    private final int SIZE = 11;
    private final double BAR_WIDTH = 40;
    private final double SPACING = 10;
    private final double PANE_HEIGHT = 350;
    private final int MAX_VALUE = 50;
    private final int HEIGHT_SCALE = 5;

    @FXML private Label line1;
    @FXML private Label line2;
    @FXML private Label line3;
    @FXML private Label line4;

    private void highlight(Label label) {
        line1.setStyle("-fx-text-fill: white;");
        line2.setStyle("-fx-text-fill: white;");
        line3.setStyle("-fx-text-fill: white;");
        line4.setStyle("-fx-text-fill: white;");
        label.setStyle("-fx-text-fill: #22C55E; -fx-font-weight: bold;");
    }

    @FXML
    public void initialize() {
        generateArray();
        drawBars();
    }

    private void generateArray() {
        array = new int[SIZE];
        Random rand = new Random();
        for (int i = 0; i < SIZE; i++) {
            array[i] = rand.nextInt(MAX_VALUE); // 0 to 49
        }
    }

    // 🔥 Helper to get Rectangle safely
    private Rectangle getRect(int i) {
        StackPane stack = (StackPane) bars[i].getChildren().get(0);
        return (Rectangle) stack.getChildren().get(0);
    }

    private void drawBars() {
        visualizationPane.getChildren().clear();
        bars = new VBox[SIZE];

        HBox container = new HBox(SPACING);
        container.setLayoutX(20);
        container.setLayoutY(0);

        for (int i = 0; i < SIZE; i++) {

            // --- BAR ---
            Rectangle rect = new Rectangle(BAR_WIDTH, array[i] * HEIGHT_SCALE);
            rect.setFill(Color.CORNFLOWERBLUE);
            rect.setArcWidth(10);
            rect.setArcHeight(10);

            // --- VALUE (INSIDE BAR) ---
            Label valueLabel = new Label(String.valueOf(array[i]));
            valueLabel.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;"
            );

            StackPane stack = new StackPane();
            stack.setPrefWidth(BAR_WIDTH);
            stack.setAlignment(Pos.BOTTOM_CENTER);
            valueLabel.setTranslateY(-5);

            stack.getChildren().addAll(rect, valueLabel);

            // --- INDEX ---
            Label indexLabel = new Label(String.valueOf(i));
            indexLabel.setStyle(
                    "-fx-text-fill: #9CA3AF;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;"
            );

            VBox box = new VBox(5);
            box.setAlignment(Pos.BOTTOM_CENTER);
            box.setPrefHeight(PANE_HEIGHT);

            box.getChildren().addAll(stack, indexLabel);

            bars[i] = box;
            container.getChildren().add(box);
        }

        visualizationPane.getChildren().add(container);
    }

    @FXML
    private void startSort() {
        new Thread(() -> {
            try {
                for (int i = 0; i < SIZE - 1; i++) {

                    Platform.runLater(() -> highlight(line1));
                    Thread.sleep(500);

                    for (int j = 0; j < SIZE - i - 1; j++) {

                        int a = j;
                        int b = j + 1;

                        Platform.runLater(() -> highlight(line2));
                        Thread.sleep(400);

                        Platform.runLater(() -> {
                            highlight(line3);
                            getRect(a).setFill(Color.ORANGE);
                            getRect(b).setFill(Color.ORANGE);
                        });

                        Thread.sleep(400);

                        if (array[a] > array[b]) {

                            Platform.runLater(() -> highlight(line4));
                            Thread.sleep(400);

                            // swap array
                            int temp = array[a];
                            array[a] = array[b];
                            array[b] = temp;

                            Platform.runLater(() -> {
                                // swap UI boxes
                                VBox tempBox = bars[a];
                                bars[a] = bars[b];
                                bars[b] = tempBox;

                                HBox parent = (HBox) bars[0].getParent();
                                parent.getChildren().setAll(bars);
                            });

                            Thread.sleep(400);
                        }

                        Platform.runLater(() -> {
                            getRect(a).setFill(Color.CORNFLOWERBLUE);
                            getRect(b).setFill(Color.CORNFLOWERBLUE);
                        });

                        Thread.sleep(200);
                    }

                    int sortedIndex = SIZE - i - 1;

                    Platform.runLater(() ->
                            getRect(sortedIndex).setFill(Color.LIMEGREEN)
                    );
                }

                Platform.runLater(() ->
                        getRect(0).setFill(Color.LIMEGREEN)
                );

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