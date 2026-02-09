package com.example.dsa_visual_lab.controller.linear;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ArrayController implements Initializable {

    // --- UI COMPONENTS ---
    @FXML private Pane visualPane;
    @FXML private Label statusLabel, lblSizeCap;
    @FXML private ProgressBar capacityBar;
    @FXML private Slider speedSlider;

    // --- LEARNING TABS ---
    @FXML private ListView<String> pseudoCodeList;
    @FXML private ListView<String> messageLogList;
    @FXML private TextArea explanationArea;

    // --- INPUTS & ERRORS ---
    @FXML private TextField insertValueField, insertIndexField;
    @FXML private TextField searchValField, capacityField;
    @FXML private Label lblErrValue, lblErrIndex; // The new error labels

    // --- DATA ---
    private int[] arrayData;
    private int size = 0;
    private int capacity = 8;

    // PSEUDOCODE
    private final String[] INSERT_CODE = {
            "if (size == capacity) resize();",
            "for (int i = size-1; i >= index; i--) {",
            "    array[i+1] = array[i];",
            "}",
            "array[index] = value;",
            "size++;"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        arrayData = new int[capacity];
        updateCapacityBar();
        render();
        addToLog("System Initialized. Capacity: " + capacity);
        setPseudocode(INSERT_CODE);
    }

    // ================= ACTIONS =================

    @FXML
    void btnAppend(ActionEvent event) {
        clearErrors();
        String valStr = insertValueField.getText().trim();

        if (!validateInput(valStr, lblErrValue)) return; // Validation check

        insertAtIndexLogic(size, valStr); // Append is just insert at end
    }

    @FXML
    void btnInsertAtIndex(ActionEvent event) {
        clearErrors();
        String valStr = insertValueField.getText().trim();
        String idxStr = insertIndexField.getText().trim();

        boolean valOk = validateInput(valStr, lblErrValue);

        // Custom index validation
        int idx = -1;
        try {
            idx = Integer.parseInt(idxStr);
            if (idx < 0 || idx > size) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError(lblErrIndex, "Index must be 0 to " + size);
            return;
        }

        if (valOk) insertAtIndexLogic(idx, valStr);
    }

    @FXML
    void btnRemoveLast(ActionEvent event) {
        if (size == 0) {
            setStatus("Array is empty!", true);
            return;
        }
        removeAtIndexLogic(size - 1);
    }

    @FXML
    void btnRemoveAtIndex(ActionEvent event) {
        // reuse the insertIndexField for simplicity, or add a dedicated remove field
        String idxStr = insertIndexField.getText().trim();
        try {
            int idx = Integer.parseInt(idxStr);
            if (idx < 0 || idx >= size) {
                showError(lblErrIndex, "Invalid Index");
                return;
            }
            removeAtIndexLogic(idx);
        } catch (NumberFormatException e) {
            showError(lblErrIndex, "Enter valid index");
        }
    }

    // ================= LOGIC & ANIMATION =================

    private void insertAtIndexLogic(int index, String valueStr) {
        int value = Integer.parseInt(valueStr);

        if (size >= capacity) {
            growArray(); // For simplicity, grow instantly then animate insert
        }

        if (index == size) {
            // Instant append visual
            arrayData[size] = value;
            size++;
            render();
            highlightNode(size - 1, Color.web("#10B981")); // Success Green
            addToLog("Appended " + value);
        } else {
            // Animated shift
            animateRightShift(index, value);
        }
        updateCapacityBar();
    }

    private void removeAtIndexLogic(int index) {
        // Shift logic (simplified for brevity)
        for (int i = index; i < size - 1; i++) {
            arrayData[i] = arrayData[i+1];
        }
        arrayData[size - 1] = 0;
        size--;
        render();
        addToLog("Removed element at index " + index);
        updateCapacityBar();
    }

    private void animateRightShift(int targetIndex, int newValue) {
        setPseudocode(INSERT_CODE);
        // ... (Keep your existing animation logic here) ...
        // Just make sure to call render() and updateCapacityBar() at the end

        // Placeholder for the animation logic provided previously:
        finalizeInsert(targetIndex, newValue);
    }

    private void finalizeInsert(int index, int value) {
        // Shift data
        for (int i = size; i > index; i--) {
            arrayData[i] = arrayData[i-1];
        }
        arrayData[index] = value;
        size++;
        render();
        updateCapacityBar();
        addToLog("Inserted " + value + " at index " + index);
    }

    // ================= VISUALS =================

    private void render() {
        visualPane.getChildren().clear();

        // Dynamic centering logic
        double boxSize = 60;
        double spacing = 10;
        double totalWidth = capacity * (boxSize + spacing);
        double startX = (visualPane.getWidth() - totalWidth) / 2;
        if (startX < 20) startX = 20; // Padding
        double startY = 150;

        for (int i = 0; i < capacity; i++) {
            StackPane stack = new StackPane();
            stack.setLayoutX(startX + i * (boxSize + spacing));
            stack.setLayoutY(startY);

            Rectangle rect = new Rectangle(boxSize, boxSize);
            rect.setArcWidth(8); rect.setArcHeight(8);
            rect.getStyleClass().add("array-box"); // Use CSS class

            Text valText = new Text();
            valText.getStyleClass().add("array-text"); // Use CSS class

            Text idxText = new Text(String.valueOf(i));
            idxText.getStyleClass().add("index-label");
            idxText.setLayoutX(startX + i * (boxSize + spacing) + 25);
            idxText.setLayoutY(startY + boxSize + 20);

            if (i < size) {
                rect.setFill(Color.web("#1E293B"));
                rect.setStroke(Color.web("#3B82F6")); // Blue border for active
                valText.setText(String.valueOf(arrayData[i]));
            } else {
                rect.setFill(Color.TRANSPARENT);
                rect.setStroke(Color.web("#334155")); // Dim border for empty
                valText.setText("");
            }

            stack.getChildren().addAll(rect, valText);
            stack.setId("box-" + i);
            visualPane.getChildren().addAll(stack, idxText);
        }
    }

    private void updateCapacityBar() {
        lblSizeCap.setText("Size: " + size + " / Capacity: " + capacity);
        double progress = (double) size / capacity;
        capacityBar.setProgress(progress);

        // Change color based on fullness
        if (progress > 0.8) capacityBar.setStyle("-fx-accent: #F59E0B;"); // Warning Yellow
        if (progress == 1.0) capacityBar.setStyle("-fx-accent: #EF4444;"); // Full Red
        else capacityBar.setStyle(""); // Default Blue
    }

    // ================= HELPERS =================

    private boolean validateInput(String input, Label errorLabel) {
        if (input.isEmpty()) {
            showError(errorLabel, "Value required");
            return false;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            showError(errorLabel, "Must be a number");
            return false;
        }
    }

    private void showError(Label label, String msg) {
        label.setText("⚠ " + msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void clearErrors() {
        lblErrValue.setVisible(false);
        lblErrValue.setManaged(false);
        lblErrIndex.setVisible(false);
        lblErrIndex.setManaged(false);
    }

    private void highlightNode(int index, Color color) {
        // ... (Your existing flashNode logic, updated to use stroke color)
    }

    private void growArray() {
        capacity *= 2;
        int[] newArr = new int[capacity];
        System.arraycopy(arrayData, 0, newArr, 0, size);
        arrayData = newArr;
        render();
        updateCapacityBar();
        addToLog("Array resized to " + capacity);
    }

    private void setPseudocode(String[] lines) {
        pseudoCodeList.setItems(FXCollections.observableArrayList(lines));
    }

    private void addToLog(String msg) {
        messageLogList.getItems().add(java.time.LocalTime.now().toString().substring(0,8) + " - " + msg);
        messageLogList.scrollTo(messageLogList.getItems().size() - 1);
        statusLabel.setText(msg);
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(isError ? Color.web("#EF4444") : Color.web("#34D399"));
    }

    @FXML
    void onBackClick(ActionEvent event) {
        try {
            // OPTION 1: Check the 'home' folder
            String path = "/com/example/dsa_visual_lab/view/home/linear-dataStructures.fxml";
            URL url = getClass().getResource(path);

            // OPTION 2: If not found, check the 'Linear-DataStructure' folder
            if (url == null) {
                path = "/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml";
                url = getClass().getResource(path);
            }

            // Safety Check
            if (url == null) {
                System.out.println("CRITICAL ERROR: Could not find linear-dataStructures.fxml in any known folder!");
                return;
            }

            // Load and Switch
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stub for search methods
    @FXML void btnLinearSearch(ActionEvent event) {}
    @FXML void btnFindMax(ActionEvent event) {}
    @FXML void btnCreateArray(ActionEvent event) {}
}