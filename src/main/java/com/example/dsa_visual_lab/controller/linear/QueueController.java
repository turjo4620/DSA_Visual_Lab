package com.example.dsa_visual_lab.controller.linear;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.LinkedList;

public class QueueController {

    @FXML private Pane visualPane;
    @FXML private TextField inputField;      // Single input
    @FXML private TextField listInputField;  // Comma-separated input
    @FXML private Label statusLabel;

    // NOTE: LinkedList implements Deque, allowing easy access to First and Last
    private final LinkedList<StackPane> visualQueue = new LinkedList<>();

    // Constants
    private static final double START_X = 50;
    private static final double Y_POS = 150;
    private static final double BOX_SIZE = 60;
    private static final double GAP = 10;
    private static final int MAX_SIZE = 12; // Increased slightly

    // ================== BASIC OPERATIONS ==================

    @FXML
    void onEnqueue(ActionEvent event) {
        String value = inputField.getText().trim();
        if (value.isEmpty()) {
            setStatus("Please enter a value!", true);
            return;
        }
        enqueueItem(value);
        inputField.clear();
    }

    @FXML
    void onDequeue(ActionEvent event) {
        if (visualQueue.isEmpty()) {
            setStatus("Queue is empty!", true);
            return;
        }

        StackPane head = visualQueue.pollFirst(); // Remove front

        // Animate removal (Move Up and Fade)
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), head);
        tt.setByY(-100);
        tt.setOnFinished(e -> {
            visualPane.getChildren().remove(head);
            shiftNodes(); // Shift remaining items left
        });
        tt.play();

        setStatus("Dequeued item.", false);
    }

    // ================== NEW FEATURES ==================

    @FXML
    void onCreateList(ActionEvent event) {
        String input = listInputField.getText().trim();
        if (input.isEmpty()) {
            setStatus("Please enter a list (e.g. 10,20,30)", true);
            return;
        }

        // 1. Clear existing queue
        onClear(null);

        // 2. Parse input by comma
        String[] items = input.split(",");

        if (items.length > MAX_SIZE) {
            setStatus("List too long! Max " + MAX_SIZE + " items.", true);
            return;
        }

        // 3. Add all items
        for (String item : items) {
            enqueueItem(item.trim());
        }
        setStatus("Created queue from list.", false);
    }

    @FXML
    void onPeekFront(ActionEvent event) {
        if (visualQueue.isEmpty()) {
            setStatus("Queue is empty!", true);
            return;
        }

        // Peek First
        StackPane frontNode = visualQueue.getFirst();
        String value = extractValue(frontNode);

        setStatus("Front of Queue: " + value, false);
        highlightNode(frontNode, Color.GREEN);
    }

    @FXML
    void onPeekBack(ActionEvent event) {
        if (visualQueue.isEmpty()) {
            setStatus("Queue is empty!", true);
            return;
        }

        // Peek Last
        StackPane backNode = visualQueue.getLast();
        String value = extractValue(backNode);

        setStatus("Back of Queue: " + value, false);
        highlightNode(backNode, Color.GREEN);
    }

    // ================== HELPERS ==================

    private void enqueueItem(String value) {
        if (visualQueue.size() >= MAX_SIZE) {
            setStatus("Queue is full!", true);
            return;
        }

        StackPane node = createNode(value);

        // Calculate Position
        double targetX = START_X + (visualQueue.size() * (BOX_SIZE + GAP));
        node.setLayoutX(targetX);
        node.setLayoutY(Y_POS);

        visualPane.getChildren().add(node);
        visualQueue.add(node);

        // Pop-in animation
        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setFromX(0); st.setFromY(0);
        st.setToX(1); st.setToY(1);
        st.play();

        setStatus("Enqueued: " + value, false);
    }

    @FXML
    void onClear(ActionEvent event) {
        visualPane.getChildren().clear();
        visualQueue.clear();
        setStatus("Queue Cleared", false);
    }

    @FXML
    void onBackClick(ActionEvent event) {
        try {
            String fxmlPath = "/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper to shift nodes after dequeue
    private void shiftNodes() {
        int index = 0;
        for (StackPane node : visualQueue) {
            double targetX = START_X + (index * (BOX_SIZE + GAP));
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), node);
            // We use layoutX for static pos, but translate for smooth movement if needed
            // Here, simply updating layout via animation logic or direct set
            // For simplicity in JavaFX standard:
            node.setLayoutX(targetX);
            index++;
        }
    }

    // Helper to create the visual box
    private StackPane createNode(String text) {
        StackPane stack = new StackPane();
        Rectangle box = new Rectangle(BOX_SIZE, BOX_SIZE);
        box.setFill(Color.web("#1E293B"));
        box.setStroke(Color.web("#34D399"));
        box.setStrokeWidth(2);
        box.setArcWidth(10); box.setArcHeight(10);

        Text value = new Text(text);
        value.setFill(Color.WHITE);
        value.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        stack.getChildren().addAll(box, value);
        return stack;
    }

    // Helper to extract text from a node (StackPane -> Text)
    private String extractValue(StackPane stack) {
        for (Node n : stack.getChildren()) {
            if (n instanceof Text) {
                return ((Text) n).getText();
            }
        }
        return "?";
    }

    // Helper to highlight a node briefly (Pop effect)
    private void highlightNode(StackPane node, Color color) {

        if (!node.getChildren().isEmpty() && node.getChildren().get(0) instanceof Rectangle) {
            Rectangle box = (Rectangle) node.getChildren().get(0);

            box.setStroke(color);      // <--- Change the color!
            box.setStrokeWidth(4);      // <--- Make it THICK so we see it well
        }
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setByX(0.2); st.setByY(0.2);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }


    // Helper for status messages
    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        if (isError) statusLabel.setStyle("-fx-text-fill: #F87171"); // Red
        else statusLabel.setStyle("-fx-text-fill: #34D399");       // Green
    }
}