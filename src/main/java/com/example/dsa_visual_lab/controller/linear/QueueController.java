package com.example.dsa_visual_lab.controller.linear;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import java.util.LinkedList;

public class QueueController {

    @FXML private ScrollPane scrollPane;
    @FXML private HBox visualPane;
    @FXML private TextField inputField;
    @FXML private TextField listInputField;
    @FXML private Label statusLabel;
    @FXML private Label complexityLabel;
    @FXML private VBox pseudoCodeBox;
    @FXML private Node controlsBox;

    private final LinkedList<StackPane> visualQueue = new LinkedList<>();
    private static final double BOX_SIZE = 70;
    private static final int MAX_SIZE = 12;
    private static final String CODE_COLOR = "#34D399";
    private static final String HIGHLIGHT_BG = "#334155";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    private void setControlsDisabled(boolean disabled) {
        if (controlsBox != null) {
            controlsBox.setDisable(disabled);
        }
    }

    @FXML
    void onEnqueue(ActionEvent event) {
        String value = inputField.getText().trim();
        if (value.isEmpty()) {
            setStatus("Please enter a value!", true);
            return;
        }

        if (visualQueue.size() >= MAX_SIZE) {
            setStatus("Queue is full! (Overflow)", true);
            return;
        }

        inputField.clear();
        setControlsDisabled(true);

        complexityLabel.setText("O(1) - Constant Time\nEnqueue updates the rear pointer.");
        String[] codeLines = {
                "enqueue(value):",
                "  if queue is full: return OVERFLOW",
                "  rear = rear + 1",
                "  queue[rear] = value"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Enqueue...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> highlightLine(2));

        PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
        step3.setOnFinished(e -> {
            highlightLine(3);
            StackPane node = createNode(value);
            visualPane.getChildren().add(node);
            visualQueue.add(node);

            ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
            st.setFromX(0); st.setFromY(0);
            st.setToX(1); st.setToY(1);
            st.play();

            setStatus("Enqueued: " + value, false);
            if (scrollPane != null) {
                Platform.runLater(() -> scrollPane.setHvalue(1.0));
            }
        });

        PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
        step4.setOnFinished(e -> {
            highlightLine(-1);
            setControlsDisabled(false);
        });

        step1.play(); step2.play(); step3.play(); step4.play();
    }

    @FXML
    void onDequeue(ActionEvent event) {
        if (visualQueue.isEmpty()) {
            setStatus("Error: Queue is empty (Underflow)!", true);
            return;
        }

        setControlsDisabled(true);

        complexityLabel.setText("O(1) - Constant Time\nDequeue updates the front pointer.");
        String[] codeLines = {
                "dequeue():",
                "  if queue is empty: return UNDERFLOW",
                "  value = queue[front]",
                "  front = front + 1",
                "  return value"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Dequeue...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> highlightLine(2));

        PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
        step3.setOnFinished(e -> {
            highlightLine(3);
            StackPane head = visualQueue.pollFirst();
            String val = extractValue(head);

            ScaleTransition st = new ScaleTransition(Duration.millis(300), head);
            st.setToX(0); st.setToY(0);
            st.setOnFinished(ev -> visualPane.getChildren().remove(head));
            st.play();

            setStatus("Dequeued: " + val, false);
        });

        PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
        step4.setOnFinished(e -> highlightLine(4));

        PauseTransition step5 = new PauseTransition(Duration.seconds(3.3));
        step5.setOnFinished(e -> {
            highlightLine(-1);
            setControlsDisabled(false);
        });

        step1.play(); step2.play(); step3.play(); step4.play(); step5.play();
    }

    @FXML
    void onPeekFront(ActionEvent event) {
        if (visualQueue.isEmpty()) {
            setStatus("Queue is empty!", true);
            return;
        }

        setControlsDisabled(true);

        complexityLabel.setText("O(1) - Constant Time\nDirect access to front pointer.");
        String[] codeLines = {
                "peekFront():",
                "  if queue is empty: return EMPTY",
                "  return queue[front]"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Peek Front...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> {
            highlightLine(2);
            StackPane frontNode = visualQueue.getFirst();
            setStatus("Front of Queue: " + extractValue(frontNode), false);
            highlightNode(frontNode, Color.GREEN);
        });

        PauseTransition step3 = new PauseTransition(Duration.seconds(2.2));
        step3.setOnFinished(e -> {
            highlightLine(-1);
            setControlsDisabled(false);
        });

        step1.play(); step2.play(); step3.play();
    }

    @FXML
    void onPeekBack(ActionEvent event) {
        if (visualQueue.isEmpty()) {
            setStatus("Queue is empty!", true);
            return;
        }

        setControlsDisabled(true);

        complexityLabel.setText("O(1) - Constant Time\nDirect access to rear pointer.");
        String[] codeLines = {
                "peekBack():",
                "  if queue is empty: return EMPTY",
                "  return queue[rear]"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Peek Back...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> {
            highlightLine(2);
            StackPane backNode = visualQueue.getLast();
            setStatus("Back of Queue: " + extractValue(backNode), false);
            highlightNode(backNode, Color.GREEN);
        });

        PauseTransition step3 = new PauseTransition(Duration.seconds(2.2));
        step3.setOnFinished(e -> {
            highlightLine(-1);
            setControlsDisabled(false);
        });

        step1.play(); step2.play(); step3.play();
    }

    @FXML
    void onIsEmpty(ActionEvent event) {
        setControlsDisabled(true);

        complexityLabel.setText("O(1) - Constant Time\nChecks if front > rear or size is 0.");
        String[] codeLines = {
                "isEmpty():",
                "  if front > rear:",
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
            boolean empty = visualQueue.isEmpty();
            if (empty) {
                highlightLine(2);
            } else {
                highlightLine(4);
            }
            setStatus("Is Empty? " + (empty ? "Yes (True)" : "No (False)"), false);
        });

        PauseTransition step3 = new PauseTransition(Duration.seconds(2.2));
        step3.setOnFinished(e -> {
            highlightLine(-1);
            setControlsDisabled(false);
        });

        step1.play(); step2.play(); step3.play();
    }

    @FXML
    void onCreateList(ActionEvent event) {
        String input = listInputField.getText().trim();
        if (input.isEmpty()) {
            setStatus("Please enter a list (e.g. 10,20,30)", true);
            return;
        }

        onClear(null);
        String[] items = input.split(",");

        if (items.length > MAX_SIZE) {
            setStatus("List too long! Max " + MAX_SIZE + " items.", true);
            return;
        }

        for (String item : items) {
            StackPane node = createNode(item.trim());
            visualPane.getChildren().add(node);
            visualQueue.add(node);
        }

        complexityLabel.setText("O(N) - Linear Time\nRequires iterating through the list elements.");
        pseudoCodeBox.getChildren().clear();
        setStatus("Created queue from list.", false);
        listInputField.clear();
    }

    @FXML
    void onClear(ActionEvent event) {
        visualPane.getChildren().clear();
        visualQueue.clear();
        setStatus("Queue Cleared", false);
        pseudoCodeBox.getChildren().clear();
        complexityLabel.setText("Cleared");
    }

    @FXML
    void onBackClick(ActionEvent event) {
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

    private StackPane createNode(String text) {
        StackPane stack = new StackPane();
        Rectangle box = new Rectangle(BOX_SIZE, BOX_SIZE);
        box.setFill(Color.web("#0F172A"));
        box.setStroke(Color.web("#34D399"));
        box.setStrokeWidth(2);
        box.setArcWidth(8);
        box.setArcHeight(8);

        Text value = new Text(text);
        value.setFill(Color.WHITE);
        value.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        stack.getChildren().addAll(box, value);
        return stack;
    }

    private String extractValue(StackPane stack) {
        for (Node n : stack.getChildren()) {
            if (n instanceof Text) {
                return ((Text) n).getText();
            }
        }
        return "?";
    }

    private void highlightNode(StackPane node, Color color) {
        if (!node.getChildren().isEmpty() && node.getChildren().get(0) instanceof Rectangle) {
            Rectangle box = (Rectangle) node.getChildren().get(0);
            box.setStroke(color);
            box.setStrokeWidth(4);

            PauseTransition reset = new PauseTransition(Duration.seconds(1.5));
            reset.setOnFinished(e -> {
                box.setStroke(Color.web("#34D399"));
                box.setStrokeWidth(2);
            });
            reset.play();
        }
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setByX(0.2); st.setByY(0.2);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
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