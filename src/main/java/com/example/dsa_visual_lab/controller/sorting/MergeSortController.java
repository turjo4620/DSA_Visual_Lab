package com.example.dsa_visual_lab.controller.sorting;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeSortController {

    @FXML
    private Pane visualPane;

    @FXML
    private Button startButton; // optional: connect Start button from FXML
    @FXML
    private Button mergeButton; // optional: connect Merge button from FXML

    private int[] array;
    private Rectangle[] bars;
    private Text[] numbers;
    private List<KeyFrame> keyFrames;
    private int delay = 0;

    private final int NUM_BARS = 20;
    private final int BAR_WIDTH = 25;
    private final int SPACING = 35; // spacing between bars

    private final Color[] mergeColors = {Color.ORANGE, Color.LIMEGREEN, Color.DEEPSKYBLUE, Color.MAGENTA};

    @FXML
    public void initialize() {
        visualPane.setPrefWidth(600);
        visualPane.setPrefHeight(300);
    }

    // ================== START: GENERATE RANDOM BARS ==================
    @FXML
    private void handleStart(ActionEvent event) {
        generateBars();
        // Optional: enable Merge button after showing bars
        if (mergeButton != null) mergeButton.setDisable(false);
    }

    private void generateBars() {
        array = new int[NUM_BARS];
        bars = new Rectangle[NUM_BARS];
        numbers = new Text[NUM_BARS];
        visualPane.getChildren().clear();

        double paneHeight = visualPane.getHeight();
        double totalWidth = NUM_BARS * SPACING;
        double startX = (visualPane.getWidth() - totalWidth) / 2;

        for (int i = 0; i < NUM_BARS; i++) {
            array[i] = (int) (Math.random() * 200 + 50);

            // Rectangle bar
            Rectangle rect = new Rectangle(BAR_WIDTH, array[i]);
            rect.setFill(Color.CORNFLOWERBLUE);
            rect.setX(startX + i * SPACING);
            rect.setY(paneHeight - array[i]);
            bars[i] = rect;

            // Numerical value below bar
            Text num = new Text(String.valueOf(array[i]));
            num.setFill(Color.WHITE);
            num.setX(rect.getX() + BAR_WIDTH / 2.0 - 8); // center text
            num.setY(paneHeight + 15); // below bars
            numbers[i] = num;

            visualPane.getChildren().addAll(rect, num);
        }
    }

    // ================== MERGE SORT: ANIMATION ==================
    @FXML
    private void handleMergeSort(ActionEvent event) {
        keyFrames = new ArrayList<>();
        delay = 0;
        mergeSort(0, array.length - 1);
        playAnimation();
    }

    private void mergeSort(int left, int right) {
        if (left >= right) return;

        int mid = (left + right) / 2;
        mergeSort(left, mid);
        mergeSort(mid + 1, right);
        merge(left, mid, right);
    }

    private void merge(int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (array[i] <= array[j]) temp[k++] = array[i++];
            else temp[k++] = array[j++];
        }
        while (i <= mid) temp[k++] = array[i++];
        while (j <= right) temp[k++] = array[j++];

        double paneHeight = visualPane.getHeight();

        for (int t = 0; t < temp.length; t++) {
            int index = left + t;
            int value = temp[t];
            array[index] = value;

            Color highlight = mergeColors[(index - left) % mergeColors.length];

            // Smooth animation for Height and Y
            KeyFrame kf1 = new KeyFrame(Duration.millis(delay),
                    new KeyValue(bars[index].heightProperty(), value),
                    new KeyValue(bars[index].yProperty(), paneHeight - value));
            keyFrames.add(kf1);

            // Change color during merge
            KeyFrame colorHighlight = new KeyFrame(Duration.millis(delay), e -> {
                bars[index].setFill(highlight);
            });
            keyFrames.add(colorHighlight);

            delay += 300; // pause for effect

            // Revert color to base
            KeyFrame colorRevert = new KeyFrame(Duration.millis(delay), e -> {
                bars[index].setFill(Color.CORNFLOWERBLUE);
            });
            keyFrames.add(colorRevert);
        }
    }

    private void playAnimation() {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrames);
        timeline.play();
    }

    // ================== BACK BUTTON ==================
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(
                "/com/example/dsa_visual_lab/view/Sorting/sorting-view.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}
