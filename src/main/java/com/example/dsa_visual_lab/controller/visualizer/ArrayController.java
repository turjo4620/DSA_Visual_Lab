package com.example.dsa_visual_lab.controller.visualizer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ArrayController implements Initializable {

    // ================= FXML LINKS =================
    @FXML private Pane visualPane;          // The drawing canvas
    @FXML private TextField inputField;     // Input for new numbers
    @FXML private TextArea codeArea;        // The code display panel
    @FXML private Label statusLabel;        // Bottom status message
    @FXML private Slider speedSlider;       // Animation speed control

    // ================= DATA =================
    private int[] arrayData;
    private int size = 0;
    private int capacity = 5;

    // ================= PSEUDOCODE STRINGS =================
    private final String CODE_INSERT = "if (size == capacity) {\n    resize();\n}\narray[size] = element;\nsize++;";
    private final String CODE_REMOVE = "if (size > 0) {\n    array[size-1] = 0;\n    size--;\n}\nelse {\n    error('Underflow');\n}";
    private final String CODE_RESIZE = "// Resizing Array...\nnew_capacity = capacity * 2;\nnew_array = int[new_capacity];\ncopy(old, new);\ncapacity = new_capacity;";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the array
        arrayData = new int[capacity];

        // Initial Draw
        render();
        statusLabel.setText("Initialized empty array with capacity " + capacity);
    }

    // ================= BUTTON ACTIONS =================

    @FXML
    void btnAdd(ActionEvent event) {
        try {
            int value = Integer.parseInt(inputField.getText());

            // 1. Check Resize
            if (size == capacity) {
                growArray(); // This handles the data logic + status update
                return; // Return early, user must click add again (or auto-add logic)
                // For this lab, let's auto-add after resize:
                // arrayData[size] = value; size++; render(); // (Simplified for now)
            }

            // 2. Normal Insert
            arrayData[size] = value;
            size++;

            // 3. Update UI
            codeArea.setText(CODE_INSERT);
            statusLabel.setText("Inserted " + value + " at index " + (size - 1));

            // 4. Trigger Animation/Render
            render();
            flashNode(size - 1, Color.LIGHTGREEN); // Highlight the new node

            inputField.clear();
            inputField.requestFocus();

        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Please enter a valid integer.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    void btnRemove(ActionEvent event) {
        codeArea.setText(CODE_REMOVE);

        if (size > 0) {
            // Flash the element before removing (Visual feedback)
            flashNode(size - 1, Color.RED);

            // Use a small delay so user sees the red flash before it disappears
            PauseTransition pause = new PauseTransition(Duration.millis(getDuration()));
            pause.setOnFinished(e -> {
                size--;
                arrayData[size] = 0; // Optional: Clear value
                statusLabel.setText("Removed element at index " + size);
                render();
            });
            pause.play();

        } else {
            statusLabel.setText("Error: Array is already empty!");
        }
    }

    @FXML
    void btnClear(ActionEvent event) {
        size = 0;
        capacity = 5; // Reset to default small size
        arrayData = new int[capacity];
        codeArea.setText("// Resetting...\nsize = 0;\ncapacity = 5;");
        statusLabel.setText("Array cleared.");
        render();
    }

    // ================= LOGIC & VISUALS =================

    private void growArray() {
        // Logic
        capacity *= 2;
        int[] newArray = new int[capacity];
        System.arraycopy(arrayData, 0, newArray, 0, size);
        arrayData = newArray;

        // UI Updates
        codeArea.setText(CODE_RESIZE);
        statusLabel.setText("Capacity Full! Resizing to " + capacity + "...");
        render();
    }

    /**
     * Draws the entire array on the Pane.
     * Calculates positions dynamically so it stays centered.
     */
    private void render() {
        visualPane.getChildren().clear();

        double startX = 50;
        double startY = 200;
        double boxSize = 60;
        double spacing = 10;

        // 1. Draw "Size vs Capacity" Text Info on Canvas
        Text infoText = new Text("Size: " + size + "  |  Capacity: " + capacity);
        infoText.setFont(Font.font("System", 20));
        infoText.setFill(Color.WHITE);
        infoText.setLayoutX(startX);
        infoText.setLayoutY(startY - 40);
        visualPane.getChildren().add(infoText);

        // 2. Draw Boxes
        for (int i = 0; i < capacity; i++) {
            // Create the container for one element
            StackPane stack = new StackPane();
            stack.setLayoutX(startX + i * (boxSize + spacing));
            stack.setLayoutY(startY);

            // Shape
            Rectangle rect = new Rectangle(boxSize, boxSize);
            rect.setArcWidth(10);
            rect.setArcHeight(10);
            rect.setStroke(Color.web("#334155")); // Slate border
            rect.setStrokeWidth(2);

            // Text (Number)
            Text valueText = new Text();
            valueText.setFont(Font.font("System", 18));

            // Style based on state (Filled vs Empty)
            if (i < size) {
                rect.setFill(Color.web("#38BDF8")); // Sky Blue (Filled)
                valueText.setText(String.valueOf(arrayData[i]));
                valueText.setFill(Color.web("#0F172A")); // Dark Text
            } else {
                rect.setFill(Color.TRANSPARENT); // Empty slot
                valueText.setText("");
            }

            // Index Label (Below the box)
            Text indexText = new Text(String.valueOf(i));
            indexText.setFill(Color.GRAY);
            indexText.setLayoutX(startX + i * (boxSize + spacing) + 25);
            indexText.setLayoutY(startY + boxSize + 20);

            stack.getChildren().addAll(rect, valueText);

            // Add ID to stack so we can find it later for animation (e.g., "box-3")
            stack.setId("box-" + i);

            visualPane.getChildren().addAll(stack, indexText);
        }
    }

    // ================= ANIMATION HELPER =================

    /**
     * Helper to flash a specific box color temporarily.
     */
    private void flashNode(int index, Color flashColor) {
        // Find the node by ID
        Node node = visualPane.lookup("#box-" + index);
        if (node instanceof StackPane) {
            StackPane stack = (StackPane) node;
            Rectangle rect = (Rectangle) stack.getChildren().get(0);
            Color originalColor = (Color) rect.getFill();

            rect.setFill(flashColor);

            PauseTransition pause = new PauseTransition(Duration.millis(getDuration()));
            pause.setOnFinished(e -> rect.setFill(originalColor));
            pause.play();
        }
    }

    /**
     * Gets duration based on the slider (Slower speed = Higher duration)
     */
    private double getDuration() {
        // Slider is 10 (slow) to 100 (fast). We invert logic for duration.
        // 100 speed -> 100ms duration. 10 speed -> 1000ms duration.
        return 1100 - (speedSlider.getValue() * 10);
    }

    // ================= NAVIGATION =================

    @FXML
    void onBackClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/linear-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}