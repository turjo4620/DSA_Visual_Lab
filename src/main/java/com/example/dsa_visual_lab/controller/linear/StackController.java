package com.example.dsa_visual_lab.controller.linear;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Stack;

public class StackController {

    @FXML private ScrollPane scrollPane;
    @FXML private VBox visualPane;
    @FXML private TextField inputField;
    @FXML private Label statusLabel;
    @FXML private Label complexityLabel;
    @FXML private VBox pseudoCodeBox;
    @FXML private HBox controlsBox;

    private Stack<Integer> stack = new Stack<>();
    private static final double RECT_WIDTH = 100;
    private static final double RECT_HEIGHT = 40;
    private static final String CODE_COLOR = "#34D399";
    private static final String HIGHLIGHT_BG = "#374151";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    @FXML
    public void onPush(ActionEvent event) {
        try {
            int value = Integer.parseInt(inputField.getText());
            inputField.clear();

            complexityLabel.setText("O(1) - Constant Time\nPushing only updates the top pointer.");
            String[] codeLines = {
                    "push(value):",
                    "  if stack is full: return OVERFLOW",
                    "  top = top + 1",
                    "  stack[top] = value"
            };
            setupPseudoCode(codeLines);
            controlsBox.setDisable(true);
            statusLabel.setText("Animating Push...");

            PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
            step1.setOnFinished(e -> highlightLine(1));

            PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
            step2.setOnFinished(e -> highlightLine(2));

            PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
            step3.setOnFinished(e -> {
                highlightLine(3);
                stack.push(value);
                drawStack();
                statusLabel.setText("Pushed: " + value);
                if (scrollPane != null) {
                    Platform.runLater(() -> scrollPane.setVvalue(0.0));
                }
            });

            PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
            step4.setOnFinished(e -> {
                highlightLine(-1);
                controlsBox.setDisable(false);
            });

            step1.play(); step2.play(); step3.play(); step4.play();

        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Enter a valid number");
        }
    }

    @FXML
    public void onPop(ActionEvent event) {
        if (stack.isEmpty()) {
            statusLabel.setText("Error: Stack is empty (Underflow)!");
            return;
        }

        complexityLabel.setText("O(1) - Constant Time\nPopping only updates the top pointer.");
        String[] codeLines = {
                "pop():",
                "  if stack is empty: return UNDERFLOW",
                "  value = stack[top]",
                "  top = top - 1",
                "  return value"
        };
        setupPseudoCode(codeLines);
        controlsBox.setDisable(true);
        statusLabel.setText("Animating Pop...");

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> highlightLine(2));

        PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
        step3.setOnFinished(e -> {
            highlightLine(3);
            int poppedValue = stack.pop();
            drawStack();
            statusLabel.setText("Popped: " + poppedValue);
        });

        PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
        step4.setOnFinished(e -> highlightLine(4));

        PauseTransition step5 = new PauseTransition(Duration.seconds(3.3));
        step5.setOnFinished(e -> {
            highlightLine(-1);
            controlsBox.setDisable(false);
        });

        step1.play(); step2.play(); step3.play(); step4.play(); step5.play();
    }

    @FXML
    public void onPeek(ActionEvent event) {
        if (stack.isEmpty()) {
            statusLabel.setText("Stack is empty");
            return;
        }

        complexityLabel.setText("O(1) - Constant Time\nDirect access via top pointer.");
        String[] codeLines = {
                "peek():",
                "  if stack is empty: return EMPTY",
                "  return stack[top]"
        };
        setupPseudoCode(codeLines);
        controlsBox.setDisable(true);
        statusLabel.setText("Animating Peek...");

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> {
            highlightLine(2);
            statusLabel.setText("Top Element: " + stack.peek());
            drawStack();

            if (!visualPane.getChildren().isEmpty()) {
                StackPane topNode = (StackPane) visualPane.getChildren().get(0);
                Rectangle rect = (Rectangle) topNode.getChildren().get(0);
                rect.setFill(Color.web("#4ADE80"));
                rect.setStroke(Color.web("#14532D"));
            }
        });

        PauseTransition step3 = new PauseTransition(Duration.seconds(2.2));
        step3.setOnFinished(e -> {
            highlightLine(-1);
            controlsBox.setDisable(false);
        });

        step1.play(); step2.play(); step3.play();
    }

    @FXML
    public void onIsEmpty(ActionEvent event) {
        complexityLabel.setText("O(1) - Constant Time\nChecks if the top pointer is -1 (or size is 0).");

        String[] codeLines = {
                "isEmpty():",
                "  if top == -1:",
                "    return True",
                "  else:",
                "    return False"
        };
        setupPseudoCode(codeLines);
        controlsBox.setDisable(true);
        statusLabel.setText("Animating isEmpty...");

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> {
            boolean empty = stack.isEmpty();
            if (empty) {
                highlightLine(2);
            } else {
                highlightLine(4);
            }
            statusLabel.setText("Is Empty? " + (empty ? "Yes (True)" : "No (False)"));
        });

        PauseTransition step3 = new PauseTransition(Duration.seconds(2.2));
        step3.setOnFinished(e -> {
            highlightLine(-1);
            controlsBox.setDisable(false);
        });

        step1.play(); step2.play(); step3.play();
    }

    @FXML
    public void onClear(ActionEvent event) {
        stack.clear();
        drawStack();
        statusLabel.setText("Stack Cleared");
        pseudoCodeBox.getChildren().clear();
        complexityLabel.setText("Cleared");
    }

    @FXML
    public void onBackClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void drawStack() {
        visualPane.getChildren().clear();
        for (Integer value : stack) {
            Rectangle rect = new Rectangle(RECT_WIDTH, RECT_HEIGHT);
            rect.setFill(Color.web("#A78BFA"));
            rect.setStroke(Color.web("#0F172A"));
            rect.setArcWidth(10);
            rect.setArcHeight(10);

            Text text = new Text(String.valueOf(value));
            text.setFill(Color.web("#0F172A"));
            text.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            StackPane itemNode = new StackPane(rect, text);
            visualPane.getChildren().add(0, itemNode);
        }
    }

    private void setupPseudoCode(String[] lines) {
        pseudoCodeBox.getChildren().clear();
        for (String line : lines) {
            Label lbl = new Label(line);
            lbl.setTextFill(Color.web(CODE_COLOR));
            lbl.setFont(Font.font("Consolas", 14));
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setStyle("-fx-padding: 4; -fx-background-radius: 4;");
            pseudoCodeBox.getChildren().add(lbl);
        }
    }

    private void highlightLine(int index) {
        for (int i = 0; i < pseudoCodeBox.getChildren().size(); i++) {
            Label lbl = (Label) pseudoCodeBox.getChildren().get(i);
            if (i == index) {
                lbl.setStyle("-fx-padding: 4; -fx-background-color: " + HIGHLIGHT_BG + "; -fx-background-radius: 4;");
                lbl.setTextFill(Color.web(HIGHLIGHT_TEXT));
            } else {
                lbl.setStyle("-fx-padding: 4; -fx-background-color: transparent; -fx-background-radius: 4;");
                lbl.setTextFill(Color.web(CODE_COLOR));
            }
        }
    }
}