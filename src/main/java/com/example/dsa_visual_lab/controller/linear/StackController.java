package com.example.dsa_visual_lab.controller.linear;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import java.util.Stack;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class StackController {

    @FXML private Pane visualPane;
    @FXML private TextField inputField;
    @FXML private Label statusLabel;

    // Internal data structure to track values
    private Stack<Integer> stack = new Stack<>();

    // Constants for visualization positioning
    private static final double RECT_WIDTH = 100;
    private static final double RECT_HEIGHT = 40;
    private static final double START_X = 350; // Horizontal center
    private static final double START_Y = 350; // Bottom of the pane

    @FXML
    public void onPush(ActionEvent event) {
        try {
            int value = Integer.parseInt(inputField.getText());
            stack.push(value);
            drawStack();
            statusLabel.setText("Pushed: " + value);
            inputField.clear();
        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Enter a valid number");
        }
    }

    @FXML
    public void onPop(ActionEvent event) {
        if (stack.isEmpty()) {
            statusLabel.setText("Error: Stack is empty!");
            return;
        }
        int value = stack.pop();
        drawStack();
        statusLabel.setText("Popped: " + value);
    }

    @FXML
    public void onPeek(ActionEvent event) {
        if (stack.isEmpty()) {
            statusLabel.setText("Stack is empty");
        } else {
            statusLabel.setText("Top Element: " + stack.peek());
        }
    }

    @FXML
    public void onClear(ActionEvent event) {
        stack.clear();
        drawStack();
        statusLabel.setText("Stack Cleared");
    }

    @FXML
    public void onBackClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // Helper method to draw the stack visually
    private void drawStack() {
        visualPane.getChildren().clear();

        double currentY = START_Y;

        // Iterate through stack to draw items
        // We draw from bottom up
        for (Integer value : stack) {

            // 1. Create Rectangle
            Rectangle rect = new Rectangle(RECT_WIDTH, RECT_HEIGHT);
            rect.setFill(Color.web("#A78BFA")); // Purple color
            rect.setStroke(Color.web("#0F172A"));
            rect.setArcWidth(10);
            rect.setArcHeight(10);

            // 2. Create Text
            Text text = new Text(String.valueOf(value));
            text.setFill(Color.web("#0F172A"));
            text.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // 3. Combine in a StackPane (to center text on rect)
            StackPane itemNode = new StackPane(rect, text);
            itemNode.setLayoutX(START_X);
            itemNode.setLayoutY(currentY);

            // Add to visual pane
            visualPane.getChildren().add(itemNode);

            // Move Y position up for next item (Stack grows upwards)
            currentY -= (RECT_HEIGHT + 5);
        }
    }
}