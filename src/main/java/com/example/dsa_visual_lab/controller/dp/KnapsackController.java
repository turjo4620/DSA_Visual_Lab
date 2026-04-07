package com.example.dsa_visual_lab.controller.dp;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;

public class KnapsackController {

    @FXML private GridPane dpGrid;
    @FXML private TextField capacityField;
    @FXML private TextField weightsField;
    @FXML private TextField valuesField;
    @FXML private Label statusLabel;
    @FXML private Label complexityLabel;
    @FXML private VBox pseudoCodeBox;
    @FXML private HBox controlsBox;
    @FXML private Slider speedSlider;

    private int[][] dpTable;
    private int[] itemWeights;
    private int[] itemValues;
    private int maxCapacity;
    private int itemCount;
    private StackPane[][] visualCells;

    private int currentItem = 1;
    private int currentCapacity = 1;

    private static final double CELL_SIZE = 50;
    private static final String CODE_COLOR = "#34D399";
    private static final String HIGHLIGHT_BG = "#374151";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    @FXML
    public void initialize() {
        complexityLabel.setText("Waiting for input...");
        setupPseudoCode(new String[]{""});

        weightsField.setText("2, 3, 4, 5");
        valuesField.setText("3, 4, 5, 6");
        capacityField.setText("8");
    }

    private Duration getStepDuration() {
        double speedMultiplier = 100.0 / (speedSlider != null ? speedSlider.getValue() : 100.0);
        return Duration.millis(600 * speedMultiplier);
    }

    @FXML
    public void onSolve(ActionEvent event) {
        try {
            maxCapacity = Integer.parseInt(capacityField.getText().trim());
            itemWeights = Arrays.stream(weightsField.getText().split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
            itemValues = Arrays.stream(valuesField.getText().split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();

            if (itemWeights.length != itemValues.length) {
                setStatus("Weights and Values arrays must be the same length!", true);
                return;
            }

            itemCount = itemWeights.length;
            dpTable = new int[itemCount + 1][maxCapacity + 1];
            visualCells = new StackPane[itemCount + 1][maxCapacity + 1];

            currentItem = 1;
            currentCapacity = 1;

            complexityLabel.setText("O(N × W) Time & Space\nGenerates a 2D table to store subproblem results.");

            String[] dpAlgorithmLines = {
                    "for i = 1 to n:",
                    "  for w = 1 to capacity:",
                    "    if weight[i-1] <= w:",
                    "      take = value[i-1] + dp[i-1][w-weight[i-1]]",
                    "      not_take = dp[i-1][w]",
                    "      dp[i][w] = max(take, not_take)",
                    "    else:",
                    "      dp[i][w] = dp[i-1][w]",
                    "return dp[n][capacity]"
            };
            setupPseudoCode(dpAlgorithmLines);

            controlsBox.setDisable(true);

            drawEmptyGrid();
            setStatus("Building DP Table...", false);

            PauseTransition initialDelay = new PauseTransition(Duration.seconds(1));
            initialDelay.setOnFinished(e -> processCell());
            initialDelay.play();

        } catch (Exception e) {
            setStatus("Invalid input formatting. Please use comma-separated integers.", true);
        }
    }

    private void drawEmptyGrid() {
        dpGrid.getChildren().clear();
        dpGrid.getRowConstraints().clear();
        dpGrid.getColumnConstraints().clear();

        for (int w = 0; w <= maxCapacity; w++) {
            dpGrid.add(createHeaderLabel("w=" + w, "#38BDF8"), w + 1, 0);
        }

        dpGrid.add(createHeaderLabel("Item 0", "#F59E0B"), 0, 1);
        for (int i = 1; i <= itemCount; i++) {
            dpGrid.add(createHeaderLabel("i=" + i + "\n(w:" + itemWeights[i-1] + ",v:" + itemValues[i-1] + ")", "#F59E0B"), 0, i + 1);
        }

        for (int i = 0; i <= itemCount; i++) {
            for (int w = 0; w <= maxCapacity; w++) {
                StackPane cellNode = createDataCell("0", "#1E293B", "#475569");
                visualCells[i][w] = cellNode;
                dpGrid.add(cellNode, w + 1, i + 1);

                if (i == 0 || w == 0) {
                    dpTable[i][w] = 0;
                    setCellTextContent(visualCells[i][w], "0", "#64748B");
                } else {
                    setCellTextContent(visualCells[i][w], "", "#FFFFFF");
                }
            }
        }
    }

    private void processCell() {
        if (currentItem > itemCount) {
            applyCellBorder(visualCells[itemCount][maxCapacity], "#22C55E"); // green()
            highlightCodeLine(8);
            setStatus("Optimal Value: " + dpTable[itemCount][maxCapacity], false);
            controlsBox.setDisable(false);
            return;
        }

        if (currentCapacity > maxCapacity) {
            currentCapacity = 1;
            currentItem++;
            processCell();
            return;
        }

        resetGridColors();
        highlightCodeLine(1);

        StackPane activeCell = visualCells[currentItem][currentCapacity];
        applyCellBorder(activeCell, "#FCD34D");

        PauseTransition calculationDelay = new PauseTransition(getStepDuration());
        calculationDelay.setOnFinished(e -> {
            int weight = itemWeights[currentItem - 1];
            int value = itemValues[currentItem - 1];

            if (weight <= currentCapacity) {
                highlightCodeLine(5);

                int include = value + dpTable[currentItem - 1][currentCapacity - weight];
                int exclude = dpTable[currentItem - 1][currentCapacity];

                dpTable[currentItem][currentCapacity] = Math.max(include, exclude);

                applyCellBorder(visualCells[currentItem - 1][currentCapacity], "#A78BFA");
                applyCellBorder(visualCells[currentItem - 1][currentCapacity - weight], "#4ADE80");

                setStatus("max(" + exclude + ", " + include + ")", false);

            } else {
                highlightCodeLine(7);

                dpTable[currentItem][currentCapacity] = dpTable[currentItem - 1][currentCapacity];

                applyCellBorder(visualCells[currentItem - 1][currentCapacity], "#A78BFA");

                setStatus("Copy " + dpTable[currentItem][currentCapacity], false);
            }

            setCellTextContent(activeCell, String.valueOf(dpTable[currentItem][currentCapacity]), "#FFFFFF");

            currentCapacity++;

            PauseTransition moveNextDelay = new PauseTransition(getStepDuration());
            moveNextDelay.setOnFinished(ev -> processCell());
            moveNextDelay.play();
        });
        calculationDelay.play();
    }

    private StackPane createHeaderLabel(String content, String colorHexCode) {
        StackPane container = new StackPane();
        container.setMinSize(CELL_SIZE, CELL_SIZE);
        Text labelText = new Text(content);
        labelText.setFill(Color.web(colorHexCode));
        labelText.setFont(Font.font("System", FontWeight.BOLD, 12));
        labelText.setStyle("-fx-text-alignment: center;");
        container.getChildren().add(labelText);
        return container;
    }

    private StackPane createDataCell(String content, String bgColorHex, String borderColorHex) {
        StackPane container = new StackPane();
        Rectangle backgroundRect = new Rectangle(CELL_SIZE, CELL_SIZE);
        backgroundRect.setFill(Color.web(bgColorHex));
        backgroundRect.setStroke(Color.web(borderColorHex));
        backgroundRect.setStrokeWidth(2);
        backgroundRect.setArcWidth(8);
        backgroundRect.setArcHeight(8);

        Text cellText = new Text(content);
        cellText.setFill(Color.WHITE);
        cellText.setFont(Font.font("System", FontWeight.BOLD, 16));

        container.getChildren().addAll(backgroundRect, cellText);
        return container;
    }

    private void setCellTextContent(StackPane targetCell, String newContent, String textColorHexCode) {
        if (targetCell != null && targetCell.getChildren().size() > 1 && targetCell.getChildren().get(1) instanceof Text) {
            Text textElement = (Text) targetCell.getChildren().get(1);
            textElement.setText(newContent);
            textElement.setFill(Color.web(textColorHexCode));
        }
    }

    private void applyCellBorder(StackPane targetCell, String strokeColorHexCode) {
        if (targetCell != null && targetCell.getChildren().size() > 0 && targetCell.getChildren().get(0) instanceof Rectangle) {
            Rectangle rectElement = (Rectangle) targetCell.getChildren().get(0);
            rectElement.setStroke(Color.web(strokeColorHexCode));
            rectElement.setStrokeWidth(4);
        }
    }

    private void resetGridColors() {
        if (visualCells == null) return;
        for (int i = 0; i <= itemCount; i++) {
            for (int w = 0; w <= maxCapacity; w++) {
                if (visualCells[i][w] != null && visualCells[i][w].getChildren().get(0) instanceof Rectangle) {
                    Rectangle rectElement = (Rectangle) visualCells[i][w].getChildren().get(0);
                    if (i < currentItem || (i == currentItem && w < currentCapacity)) {
                        rectElement.setStroke(Color.web("#475569"));
                        rectElement.setStrokeWidth(2);
                    }
                }
            }
        }
    }

    @FXML
    public void onClear(ActionEvent event) {
        dpGrid.getChildren().clear();
        capacityField.clear();
        weightsField.clear();
        valuesField.clear();
        currentItem = 1;
        currentCapacity = 1;
        setStatus("Ready", false);
        setupPseudoCode(new String[]{""});
        complexityLabel.setText("Cleared");
        controlsBox.setDisable(false);
    }

    private void setStatus(String messageContent, boolean isErrorState) {
        statusLabel.setText(messageContent);
        if (isErrorState) {
            statusLabel.setStyle("-fx-text-fill: #F87171; -fx-background-color: #334155; -fx-padding: 10 25; -fx-background-radius: 12; -fx-border-color: #475569; -fx-border-radius: 12;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #FCD34D; -fx-background-color: #334155; -fx-padding: 10 25; -fx-background-radius: 12; -fx-border-color: #475569; -fx-border-radius: 12;");
        }
    }

    private void setupPseudoCode(String[] sourceLines) {
        pseudoCodeBox.getChildren().clear();
        for (String lineText : sourceLines) {
            Label codeLabel = new Label(lineText);
            codeLabel.setTextFill(Color.web(CODE_COLOR));
            codeLabel.setFont(Font.font("Consolas", 14));
            codeLabel.setMaxWidth(Double.MAX_VALUE);
            codeLabel.setStyle("-fx-padding: 4; -fx-background-radius: 4;");
            pseudoCodeBox.getChildren().add(codeLabel);
        }
    }

    private void highlightCodeLine(int targetIndex) {
        for (int i = 0; i < pseudoCodeBox.getChildren().size(); i++) {
            Label currentLabel = (Label) pseudoCodeBox.getChildren().get(i);
            if (i == targetIndex) {
                currentLabel.setStyle("-fx-padding: 4; -fx-background-color: " + HIGHLIGHT_BG + "; -fx-background-radius: 4;");
                currentLabel.setTextFill(Color.web(HIGHLIGHT_TEXT));
            } else {
                currentLabel.setStyle("-fx-padding: 4; -fx-background-color: transparent; -fx-background-radius: 4;");
                currentLabel.setTextFill(Color.web(CODE_COLOR));
            }
        }
    }

    @FXML
    public void onBackClick(ActionEvent event) {
        try {
            FXMLLoader layoutLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));
            Parent newRootNode = layoutLoader.load();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.getScene().setRoot(newRootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}