package com.example.dsa_visual_lab.controller.sorting;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuickSortController {

    @FXML private HBox arrayContainer;
    @FXML private TextField inputField;
    @FXML private Label statusLabel;
    @FXML private HBox controlsBox;
    @FXML private Slider speedSlider;

    @FXML private Label line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11, line12;
    private Label[] codeLines;

    private int[] mainArray;
    private BarNode[] visualBars;
    private List<Runnable> animationSteps;
    private double heightMultiplier;

    private class BarNode {
        VBox container;
        Rectangle bar;
        Text valText;

        BarNode(int value, int index) {
            valText = new Text(String.valueOf(value));
            valText.setFill(Color.WHITE);
            valText.setFont(Font.font("System", FontWeight.BOLD, 14));

            bar = new Rectangle(35, value * heightMultiplier);
            bar.setFill(Color.web("#38BDF8"));
            bar.setArcWidth(8);
            bar.setArcHeight(8);

            Text idxText = new Text(String.valueOf(index));
            idxText.setFill(Color.web("#94A3B8"));
            idxText.setFont(Font.font("System", 12));

            container = new VBox(5);
            container.setAlignment(Pos.BOTTOM_CENTER);
            container.getChildren().addAll(valText, bar, idxText);
        }

        void updateValue(int newValue) {
            valText.setText(String.valueOf(newValue));
            bar.setHeight(newValue * heightMultiplier);
        }

        void setColor(String hex) {
            bar.setFill(Color.web(hex));
        }
    }

    @FXML
    public void initialize() {
        codeLines = new Label[]{line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11, line12};
        inputField.setText("38, 27, 43, 3, 9, 82, 10, 67, 50, 15");
        handleSetArray();
    }

    private Duration getStepDuration() {
        double multiplier = 100.0 / (speedSlider != null ? speedSlider.getValue() : 100.0);
        return Duration.millis(400 * multiplier);
    }

    @FXML
    public void handleSetArray() {
        try {
            mainArray = Arrays.stream(inputField.getText().split(","))
                    .map(String::trim)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            if (mainArray.length == 0 || mainArray.length > 20) {
                statusLabel.setText("Please enter between 1 and 20 numbers.");
                return;
            }

            int maxVal = Arrays.stream(mainArray).max().orElse(1);
            heightMultiplier = 450.0 / maxVal;

            drawInitialBars();
            statusLabel.setText("Array loaded. Ready to sort!");
            highlightCode(-1);
        } catch (Exception e) {
            statusLabel.setText("Invalid input! Use comma-separated integers.");
        }
    }

    private void drawInitialBars() {
        arrayContainer.getChildren().clear();
        visualBars = new BarNode[mainArray.length];
        for (int i = 0; i < mainArray.length; i++) {
            visualBars[i] = new BarNode(mainArray[i], i);
            arrayContainer.getChildren().add(visualBars[i].container);
        }
    }

    @FXML
    public void handleStart() {
        if (mainArray == null || mainArray.length == 0) return;
        controlsBox.setDisable(true);
        animationSteps = new ArrayList<>();
        int[] tempArray = mainArray.clone();

        generateQuickSortSteps(tempArray, 0, tempArray.length - 1);

        animationSteps.add(() -> {
            highlightCode(-1);
            for (BarNode b : visualBars) b.setColor("#4ADE80");
            statusLabel.setText("Quick Sort Complete!");
            controlsBox.setDisable(false);
        });

        playAnimationSequence(0);
    }

    private void generateQuickSortSteps(int[] arr, int low, int high) {
        animationSteps.add(() -> highlightCode(0));
        animationSteps.add(() -> highlightCode(1));
        if (low < high) {
            animationSteps.add(() -> highlightCode(2));
            int pi = partition(arr, low, high);

            animationSteps.add(() -> highlightCode(3));
            generateQuickSortSteps(arr, low, pi - 1);

            animationSteps.add(() -> highlightCode(4));
            generateQuickSortSteps(arr, pi + 1, high);
        } else if (low == high) {
            final int sortedIdx = low;
            animationSteps.add(() -> visualBars[sortedIdx].setColor("#4ADE80"));
        }
    }

    private int partition(int[] arr, int low, int high) {
        animationSteps.add(() -> highlightCode(5));
        int pivot = arr[high];

        animationSteps.add(() -> {
            highlightCode(6);
            visualBars[high].setColor("#EF4444");
            statusLabel.setText("Pivot selected at index " + high + " (Value: " + pivot + ")");
        });

        int i = low - 1;
        animationSteps.add(() -> highlightCode(7));

        for (int j = low; j < high; j++) {
            final int currJ = j;
            animationSteps.add(() -> {
                highlightCode(8);
                visualBars[currJ].setColor("#FCD34D");
            });

            animationSteps.add(() -> highlightCode(9));
            if (arr[j] < pivot) {
                i++;
                final int currI = i;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                final int valI = arr[i];
                final int valJ = arr[j];

                animationSteps.add(() -> {
                    highlightCode(10);
                    visualBars[currI].updateValue(valI);
                    visualBars[currJ].updateValue(valJ);
                    statusLabel.setText("Swapped element to left of pivot boundary.");
                });
            }
            animationSteps.add(() -> visualBars[currJ].setColor("#38BDF8"));
        }

        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        final int swapIdx = i + 1;
        final int valSwap = arr[swapIdx];
        final int valHigh = arr[high];

        animationSteps.add(() -> {
            highlightCode(11);
            visualBars[swapIdx].updateValue(valSwap);
            visualBars[high].updateValue(valHigh);
            visualBars[swapIdx].setColor("#4ADE80");
            if (swapIdx != high) visualBars[high].setColor("#38BDF8");
            statusLabel.setText("Placed pivot in sorted position.");
        });
        return i + 1;
    }

    private void playAnimationSequence(int index) {
        if (index >= animationSteps.size()) return;
        animationSteps.get(index).run();
        PauseTransition delay = new PauseTransition(getStepDuration());
        delay.setOnFinished(e -> playAnimationSequence(index + 1));
        delay.play();
    }

    private void highlightCode(int activeIndex) {
        for (int i = 0; i < codeLines.length; i++) {
            if (i == activeIndex) {
                codeLines[i].setStyle("-fx-background-color: #374151; -fx-text-fill: #FCD34D; -fx-padding: 2; -fx-background-radius: 4; -fx-font-size: 14px;");
            } else {
                codeLines[i].setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 2; -fx-font-size: 14px;");
            }
        }
    }

    @FXML
    public void handleReset() {
        handleSetArray();
        controlsBox.setDisable(false);
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/dsa_visual_lab/view/Sorting/sorting-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}