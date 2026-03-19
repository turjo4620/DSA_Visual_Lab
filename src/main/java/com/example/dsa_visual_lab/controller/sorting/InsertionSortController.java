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

public class InsertionSortController {

    @FXML private HBox arrayContainer;
    @FXML private TextField inputField;
    @FXML private Label statusLabel;
    @FXML private HBox controlsBox;
    @FXML private Slider speedSlider;

    @FXML private Label line1, line2, line3, line4, line5, line6, line7;
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
        codeLines = new Label[]{line1, line2, line3, line4, line5, line6, line7};
        inputField.setText("62, 25, 88, 14, 47, 91, 33, 19, 55, 76");
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

        generateInsertionSortSteps(tempArray);

        animationSteps.add(() -> {
            highlightCode(-1);
            for (BarNode b : visualBars) b.setColor("#4ADE80");
            statusLabel.setText("Insertion Sort Complete!");
            controlsBox.setDisable(false);
        });

        playAnimationSequence(0);
    }

    private void generateInsertionSortSteps(int[] arr) {
        animationSteps.add(() -> {
            visualBars[0].setColor("#4ADE80");
            statusLabel.setText("First element is trivially sorted.");
        });

        for (int i = 1; i < arr.length; i++) {
            final int currI = i;
            animationSteps.add(() -> highlightCode(0));

            int key = arr[i];
            final int keyVal = key;

            animationSteps.add(() -> {
                highlightCode(1);
                visualBars[currI].setColor("#EF4444");
                statusLabel.setText("Selected key: " + keyVal + " at index " + currI);
            });

            animationSteps.add(() -> highlightCode(2));
            int j = i - 1;

            while (j >= 0 && arr[j] > key) {
                final int currJ = j;
                final int currJPlus1 = j + 1;
                final int valJ = arr[j];

                animationSteps.add(() -> {
                    highlightCode(3);
                    visualBars[currJ].setColor("#FCD34D");
                    statusLabel.setText("Comparing key (" + keyVal + ") with " + valJ);
                });

                arr[j + 1] = arr[j];

                animationSteps.add(() -> {
                    highlightCode(4);
                    visualBars[currJPlus1].updateValue(valJ);
                    visualBars[currJPlus1].setColor("#FCD34D");
                    visualBars[currJ].setColor("#38BDF8");
                    statusLabel.setText(valJ + " is greater than " + keyVal + ", shifting it right.");
                });

                animationSteps.add(() -> highlightCode(5));
                j = j - 1;
            }

            final int insertIdx = j + 1;
            arr[insertIdx] = key;

            animationSteps.add(() -> {
                highlightCode(6);
                visualBars[insertIdx].updateValue(keyVal);
                visualBars[insertIdx].setColor("#4ADE80");
                statusLabel.setText("Inserted key " + keyVal + " at index " + insertIdx);

                for (int k = 0; k <= currI; k++) {
                    visualBars[k].setColor("#4ADE80");
                }
            });
        }
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