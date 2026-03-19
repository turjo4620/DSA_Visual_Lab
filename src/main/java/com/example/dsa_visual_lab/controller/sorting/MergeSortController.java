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

public class MergeSortController {

    @FXML private HBox arrayContainer;
    @FXML private TextField inputField;
    @FXML private Label statusLabel;
    @FXML private HBox controlsBox;
    @FXML private Slider speedSlider;

    @FXML private Label line1, line2, line3, line4, line5, line6, line8, line9, line10, line11, line12;
    private Label[] codeLines;

    private int[] mainArray;
    private BarNode[] visualBars;
    private List<Runnable> animationSteps;
    private double heightMultiplier;

    private static final String COLOR_DEFAULT = "#38BDF8";
    private static final String COLOR_ACTIVE = "#FCD34D";
    private static final String COLOR_MERGING = "#A78BFA";
    private static final String COLOR_SORTED = "#4ADE80";

    private class BarNode {
        VBox container;
        Rectangle bar;
        Text valText;

        BarNode(int value, int index) {
            valText = new Text(String.valueOf(value));
            valText.setFill(Color.WHITE);
            valText.setFont(Font.font("System", FontWeight.BOLD, 14));

            bar = new Rectangle(35, value * heightMultiplier);
            bar.setFill(Color.web(COLOR_DEFAULT));
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
        codeLines = new Label[]{line1, line2, line3, line4, line5, line6, line8, line9, line10, line11, line12};
        inputField.setText("45, 12, 89, 33, 67, 21, 5, 99, 54");
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
            heightMultiplier = 250.0 / maxVal;

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

        generateMergeSortSteps(tempArray, 0, tempArray.length - 1);

        animationSteps.add(() -> {
            highlightCode(-1);
            for (BarNode b : visualBars) b.setColor(COLOR_SORTED);
            statusLabel.setText("Merge Sort Complete!");
            controlsBox.setDisable(false);
        });

        playAnimationSequence(0);
    }

    private void generateMergeSortSteps(int[] arr, int l, int r) {
        animationSteps.add(() -> { highlightCode(0); setRangeColor(l, r, COLOR_ACTIVE); statusLabel.setText("Dividing array from index " + l + " to " + r); });
        animationSteps.add(() -> highlightCode(1));

        if (l < r) {
            int m = l + (r - l) / 2;
            animationSteps.add(() -> highlightCode(2));

            animationSteps.add(() -> highlightCode(3));
            generateMergeSortSteps(arr, l, m);

            animationSteps.add(() -> highlightCode(4));
            generateMergeSortSteps(arr, m + 1, r);

            animationSteps.add(() -> highlightCode(5));
            generateMergeSteps(arr, l, m, r);
        } else {
            animationSteps.add(() -> setRangeColor(l, r, COLOR_DEFAULT));
        }
    }

    private void generateMergeSteps(int[] arr, int l, int m, int r) {
        animationSteps.add(() -> { highlightCode(6); setRangeColor(l, r, COLOR_MERGING); statusLabel.setText("Merging sub-arrays [" + l + "-" + m + "] and [" + (m+1) + "-" + r + "]"); });

        int n1 = m - l + 1;
        int n2 = r - m;

        int[] L = new int[n1];
        int[] R = new int[n2];

        animationSteps.add(() -> highlightCode(7));
        System.arraycopy(arr, l, L, 0, n1);
        System.arraycopy(arr, m + 1, R, 0, n2);

        int i = 0, j = 0, k = l;

        animationSteps.add(() -> highlightCode(8));
        while (i < n1 && j < n2) {
            animationSteps.add(() -> highlightCode(9));

            final int currK = k;
            if (L[i] <= R[j]) {
                final int val = L[i];
                arr[k] = L[i];
                animationSteps.add(() -> { visualBars[currK].updateValue(val); visualBars[currK].setColor(COLOR_SORTED); });
                i++;
            } else {
                final int val = R[j];
                arr[k] = R[j];
                animationSteps.add(() -> { visualBars[currK].updateValue(val); visualBars[currK].setColor(COLOR_SORTED); });
                j++;
            }
            k++;
        }

        animationSteps.add(() -> highlightCode(10));
        while (i < n1) {
            final int currK = k;
            final int val = L[i];
            arr[k] = L[i];
            animationSteps.add(() -> { visualBars[currK].updateValue(val); visualBars[currK].setColor(COLOR_SORTED); });
            i++; k++;
        }

        while (j < n2) {
            final int currK = k;
            final int val = R[j];
            arr[k] = R[j];
            animationSteps.add(() -> { visualBars[currK].updateValue(val); visualBars[currK].setColor(COLOR_SORTED); });
            j++; k++;
        }
    }

    private void playAnimationSequence(int index) {
        if (index >= animationSteps.size()) return;

        animationSteps.get(index).run();
        PauseTransition delay = new PauseTransition(getStepDuration());
        delay.setOnFinished(e -> playAnimationSequence(index + 1));
        delay.play();
    }

    private void setRangeColor(int start, int end, String hex) {
        for (int i = start; i <= end; i++) {
            visualBars[i].setColor(hex);
        }
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