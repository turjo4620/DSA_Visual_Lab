package com.example.dsa_visual_lab.controller.sorting;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
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
            container.setMinHeight(170);
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
        return Duration.millis(500 * multiplier);
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
            heightMultiplier = 120.0 / maxVal;

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

        generateMergeSortSteps(tempArray, 0, tempArray.length - 1, 0);

        animationSteps.add(() -> {
            highlightCode(-1);
            for (BarNode b : visualBars) b.setColor(COLOR_SORTED);
            statusLabel.setText("Merge Sort Complete! Full array back at Depth 0.");
            controlsBox.setDisable(false);
        });

        playAnimationSequence(0);
    }

    private void generateMergeSortSteps(int[] arr, int l, int r, int depth) {
        animationSteps.add(() -> {
            highlightCode(0);
            setRangeColor(l, r, COLOR_ACTIVE);
            setRangeDepth(l, r, depth);
            statusLabel.setText("Depth " + depth + ": Splitting array [" + l + "..." + r + "]");
        });
        animationSteps.add(() -> highlightCode(1));

        if (l < r) {
            int m = l + (r - l) / 2;
            animationSteps.add(() -> highlightCode(2));

            animationSteps.add(() -> highlightCode(3));
            generateMergeSortSteps(arr, l, m, depth + 1);

            animationSteps.add(() -> {
                highlightCode(4);
                statusLabel.setText("Depth " + depth + ": Left half split. Now splitting right half [" + (m+1) + "..." + r + "]");
            });
            generateMergeSortSteps(arr, m + 1, r, depth + 1);

            animationSteps.add(() -> {
                highlightCode(5);
                statusLabel.setText("Depth " + depth + ": Both halves split. Ready to merge.");
            });
            generateMergeSteps(arr, l, m, r, depth);

            animationSteps.add(() -> {
                setRangeDepth(l, r, depth);
                statusLabel.setText("Depth " + depth + ": Array [" + l + "..." + r + "] merged and pulled up.");
            });

        } else {
            animationSteps.add(() -> {
                setRangeColor(l, r, COLOR_DEFAULT);
                statusLabel.setText("Depth " + depth + ": Base case. Single element [" + l + "] is sorted.");
            });
        }
    }

    private void generateMergeSteps(int[] arr, int l, int m, int r, int depth) {

        animationSteps.add(() -> {
            highlightCode(6);
            setRangeColor(l, r, COLOR_MERGING);
            statusLabel.setText("Merging [" + l + "-" + m + "] and [" + (m+1) + "-" + r + "]");
        });

        int[] L = Arrays.copyOfRange(arr, l, m + 1);
        int[] R = Arrays.copyOfRange(arr, m + 1, r + 1);

        int i = 0, j = 0, k = l;

        animationSteps.add(() -> highlightCode(8));

        while (i < L.length && j < R.length) {

            animationSteps.add(() -> highlightCode(9));

            final int val;
            if (L[i] <= R[j]) val = L[i++];
            else val = R[j++];

            arr[k] = val;
            final int idx = k++;

            animationSteps.add(() -> {
                visualBars[idx].updateValue(val);
                visualBars[idx].setColor(COLOR_MERGING);


                TranslateTransition tt = new TranslateTransition(Duration.millis(300), visualBars[idx].container);
                tt.setToY(depth * 40 + 60);
                tt.play();

                statusLabel.setText("Dropping: " + val);
            });
        }

        while (i < L.length) {
            final int val = L[i++];
            arr[k] = val;
            final int idx = k++;

            animationSteps.add(() -> {
                visualBars[idx].updateValue(val);
                visualBars[idx].setColor(COLOR_MERGING);

                TranslateTransition tt = new TranslateTransition(Duration.millis(300), visualBars[idx].container);
                tt.setToY(depth * 40 + 60);
                tt.play();
            });
        }

        while (j < R.length) {
            final int val = R[j++];
            arr[k] = val;
            final int idx = k++;

            animationSteps.add(() -> {
                visualBars[idx].updateValue(val);
                visualBars[idx].setColor(COLOR_MERGING);

                TranslateTransition tt = new TranslateTransition(Duration.millis(300), visualBars[idx].container);
                tt.setToY(depth * 40 + 60);
                tt.play();
            });
        }


        animationSteps.add(() -> {
            for (int x = l; x <= r; x++) {
                visualBars[x].setColor(COLOR_SORTED);

                TranslateTransition tt = new TranslateTransition(Duration.millis(300), visualBars[x].container);
                tt.setToY(depth * 40);
                tt.play();
            }

            statusLabel.setText("Merged.");
        });
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

    private void setRangeDepth(int start, int end, int depth) {
        for (int i = start; i <= end; i++) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), visualBars[i].container);
            tt.setToY(depth * 40);
            tt.play();
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