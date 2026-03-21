package com.example.dsa_visual_lab.controller.linear;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
    @FXML private TextField listInputField;
    @FXML private Label statusLabel;
    @FXML private Label complexityLabel;
    @FXML private VBox pseudoCodeBox;

    private Stack<Integer> stack = new Stack<>();
    private static final double RECT_WIDTH = 120; // Made slightly wider for bigger fonts
    private static final double RECT_HEIGHT = 45; // Taller for bigger fonts
    private static final String CODE_COLOR = "#A78BFA";
    private static final String HIGHLIGHT_BG = "#334155";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    @FXML
    public void onPush(ActionEvent event) {
        try {
            int value = Integer.parseInt(inputField.getText().trim());
            inputField.clear();

            complexityLabel.setText("O(1) - Constant Time\nPushing only updates the top pointer.");
            String[] codeLines = {
                    "push(value):",
                    "  if stack is full: return OVERFLOW",
                    "  top = top + 1",
                    "  stack[top] = value"
            };
            setupPseudoCode(codeLines);
            setStatus("Animating Push...", false);

            PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
            step1.setOnFinished(e -> highlightLine(1));

            PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
            step2.setOnFinished(e -> highlightLine(2));

            PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
            step3.setOnFinished(e -> {
                highlightLine(3);
                stack.push(value);
                drawStack();
                setStatus("Pushed: " + value, false);
                if (scrollPane != null) {
                    Platform.runLater(() -> scrollPane.setVvalue(0.0));
                }
            });

            PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
            step4.setOnFinished(e -> highlightLine(-1));

            step1.play(); step2.play(); step3.play(); step4.play();

        } catch (NumberFormatException e) {
            setStatus("Error: Enter a valid number", true);
        }
    }

    @FXML
    public void onPop(ActionEvent event) {
        if (stack.isEmpty()) {
            setStatus("Error: Stack is empty (Underflow)!", true);
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
        setStatus("Animating Pop...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> highlightLine(2));

        PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
        step3.setOnFinished(e -> {
            highlightLine(3);
            int poppedValue = stack.pop();
            drawStack();
            setStatus("Popped: " + poppedValue, false);
        });

        PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
        step4.setOnFinished(e -> highlightLine(4));

        PauseTransition step5 = new PauseTransition(Duration.seconds(3.3));
        step5.setOnFinished(e -> highlightLine(-1));

        step1.play(); step2.play(); step3.play(); step4.play(); step5.play();
    }

    @FXML
    public void onPeek(ActionEvent event) {
        if (stack.isEmpty()) {
            setStatus("Stack is empty", true);
            return;
        }

        complexityLabel.setText("O(1) - Constant Time\nDirect access via top pointer.");
        String[] codeLines = {
                "peek():",
                "  if stack is empty: return EMPTY",
                "  return stack[top]"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Peek...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> {
            highlightLine(2);
            setStatus("Top Element: " + stack.peek(), false);
            drawStack();

            if (!visualPane.getChildren().isEmpty()) {
                StackPane topNode = (StackPane) visualPane.getChildren().get(0);
                Rectangle rect = (Rectangle) topNode.getChildren().get(0);
                rect.setStroke(Color.web("#FCD34D"));
                rect.setStrokeWidth(4);
            }
        });

        PauseTransition step3 = new PauseTransition(Duration.seconds(2.2));
        step3.setOnFinished(e -> highlightLine(-1));

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
        setStatus("Animating isEmpty...", false);

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
            setStatus("Is Empty? " + (empty ? "Yes (True)" : "No (False)"), false);
        });

        PauseTransition step3 = new PauseTransition(Duration.seconds(2.2));
        step3.setOnFinished(e -> highlightLine(-1));

        step1.play(); step2.play(); step3.play();
    }

    @FXML
    void onCreateList(ActionEvent event) {
        String input = listInputField.getText().trim();
        if (input.isEmpty()) {
            setStatus("Please enter a list (e.g. 10,20,30)", true);
            return;
        }

        try {
            stack.clear();
            String[] items = input.split(",");
            for (String item : items) {
                stack.push(Integer.parseInt(item.trim()));
            }
            drawStack();
            setStatus("Stack initialized with " + stack.size() + " elements.", false);
            pseudoCodeBox.getChildren().clear();
            complexityLabel.setText("Cleared");
            listInputField.clear();
        } catch (NumberFormatException e) {
            setStatus("Error: Enter valid numbers separated by commas.", true);
        }
    }

    @FXML
    public void onClear(ActionEvent event) {
        stack.clear();
        drawStack();
        setStatus("Stack Cleared", false);
        pseudoCodeBox.getChildren().clear();
        complexityLabel.setText("Cleared");
    }

    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            String path = "/com/example/dsa_visual_lab/view/home/linear-dataStructures.fxml";
            if (getClass().getResource(path) == null) {
                path = "/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml";
            }
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawStack() {
        visualPane.getChildren().clear();
        for (Integer value : stack) {
            Rectangle rect = new Rectangle(RECT_WIDTH, RECT_HEIGHT);
            rect.setFill(Color.web("#0F172A"));
            rect.setStroke(Color.web("#A78BFA"));
            rect.setStrokeWidth(2);
            rect.setArcWidth(8);
            rect.setArcHeight(8);

            Text text = new Text(String.valueOf(value));
            text.setFill(Color.WHITE);
            text.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

            StackPane itemNode = new StackPane(rect, text);
            visualPane.getChildren().add(0, itemNode); // Insert at 0 so top of stack shows visually at the top
        }
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        if (isError) {
            statusLabel.setTextFill(Color.web("#F87171"));
        } else {
            statusLabel.setTextFill(Color.web("#FCD34D"));
        }
    }

    private void setupPseudoCode(String[] lines) {
        pseudoCodeBox.getChildren().clear();
        for (String line : lines) {
            Label lbl = new Label(line);
            lbl.setTextFill(Color.web(CODE_COLOR));
            lbl.setFont(Font.font("Consolas", 16));
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setStyle("-fx-padding: 2;");
            pseudoCodeBox.getChildren().add(lbl);
        }
    }

    private void highlightLine(int index) {
        for (int i = 0; i < pseudoCodeBox.getChildren().size(); i++) {
            Label lbl = (Label) pseudoCodeBox.getChildren().get(i);
            if (i == index) {
                lbl.setStyle("-fx-padding: 2; -fx-background-color: " + HIGHLIGHT_BG + "; -fx-background-radius: 2;");
                lbl.setTextFill(Color.web(HIGHLIGHT_TEXT));
            } else {
                lbl.setStyle("-fx-padding: 2; -fx-background-color: transparent;");
                lbl.setTextFill(Color.web(CODE_COLOR));
            }
        }
    }
}