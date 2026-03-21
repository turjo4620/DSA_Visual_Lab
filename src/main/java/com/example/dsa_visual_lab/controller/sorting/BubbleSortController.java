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
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BubbleSortController {

    @FXML private HBox arrayContainer;
    @FXML private TextField inputField;
    @FXML private Label statusLabel;
    @FXML private HBox controlsBox;
    @FXML private Slider speedSlider;

    @FXML private Label line1, line2, line3, line4;

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
        codeLines = new Label[]{line1, line2, line3, line4};
        inputField.setText("95, 23, 76, 12, 54, 88, 33, 10, 67, 42");
        handleSetArray();
    }

    private Duration getStepDuration() {
        double multiplier = 100.0 / (speedSlider != null ? speedSlider.getValue() : 100.0);
        return Duration.millis(300 * multiplier);
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
        generateBubbleSortSteps(tempArray);

        animationSteps.add(() -> {
            highlightCode(-1);
            for (BarNode b : visualBars) b.setColor("#4ADE80");
            statusLabel.setText("Bubble Sort Complete!");
            controlsBox.setDisable(false);
        });

        playAnimationSequence(0);
    }

    private void generateBubbleSortSteps(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            animationSteps.add(() -> highlightCode(0));

            for (int j = 0; j < arr.length - i - 1; j++) {
                final int a = j;
                final int b = j + 1;

                animationSteps.add(() -> {
                    highlightCode(1);
                    visualBars[a].setColor("#FCD34D");
                    visualBars[b].setColor("#FCD34D");
                    statusLabel.setText("Comparing " + arr[a] + " and " + arr[b]);
                });

                animationSteps.add(() -> highlightCode(2));

                if (arr[a] > arr[b]) {
                    int temp = arr[a];
                    arr[a] = arr[b];
                    arr[b] = temp;

                    final int valA = arr[a];
                    final int valB = arr[b];

                    animationSteps.add(() -> {
                        highlightCode(3);
                        visualBars[a].updateValue(valA);
                        visualBars[b].updateValue(valB);
                        visualBars[a].setColor("#EF4444");
                        visualBars[b].setColor("#EF4444");
                        statusLabel.setText("Swapped!");
                    });
                }

                animationSteps.add(() -> {
                    visualBars[a].setColor("#38BDF8");
                    visualBars[b].setColor("#38BDF8");
                });
            }

            final int sortedIdx = arr.length - i - 1;
            animationSteps.add(() -> visualBars[sortedIdx].setColor("#4ADE80"));
        }

        animationSteps.add(() -> visualBars[0].setColor("#4ADE80"));
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
                codeLines[i].setStyle("-fx-background-color: #374151; -fx-text-fill: #FCD34D; -fx-padding: 2; -fx-background-radius: 4;");
            } else {
                codeLines[i].setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
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