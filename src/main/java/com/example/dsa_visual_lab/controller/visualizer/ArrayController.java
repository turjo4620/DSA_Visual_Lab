package com.example.dsa_visual_lab.controller.visualizer;

import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
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

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ArrayController implements Initializable {

    // --- VISUAL ELEMENTS ---
    @FXML private Pane visualPane;
    @FXML private Label statusLabel;
    @FXML private Slider speedSlider;

    // LISTS
    @FXML private ListView<String> pseudoCodeList;
    @FXML private ListView<String> messageLogList;

    // --- INPUT FIELDS ---
    @FXML private TextField capacityField, initialDataField;
    @FXML private TextField insertValueField, insertIndexField;
    @FXML private TextField removeIndexField;
    @FXML private TextField searchValField, countValField;

    // --- DATA ---
    private int[] arrayData;
    private int size = 0;
    private int capacity = 8;

    // PSEUDOCODE TEMPLATES
    private final String[] INSERT_CODE = {
            "if (size == capacity) resize();",
            "for (int i=size-1; i>=index; i--) {",
            "    array[i+1] = array[i];",
            "}",
            "array[index] = value;",
            "size++;"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        arrayData = new int[capacity];
        render();
        addToLog("Initialized System. Cap: 8");
        setPseudocode(INSERT_CODE);
    }

    // ================= 1. CREATE ARRAY =================
    @FXML
    void btnCreateArray(ActionEvent event) {
        try {
            int newCap = Integer.parseInt(capacityField.getText().trim());
            if (newCap <= 0 || newCap > 20) {
                addToLog("Error: Capacity must be 1-20");
                return;
            }
            capacity = newCap;
            arrayData = new int[capacity];
            size = 0;

            String initialData = initialDataField.getText();
            if (initialData != null && !initialData.isEmpty()) {
                String[] parts = initialData.split(",");
                for (String p : parts) {
                    if (size < capacity) {
                        arrayData[size++] = Integer.parseInt(p.trim());
                    }
                }
            }
            render();
            addToLog("Created new array. Size: " + size);
        } catch (Exception e) {
            addToLog("Error: Invalid input format.");
        }
    }

    // ================= 2. INSERT OPERATIONS (ANIMATED) =================
    @FXML
    void btnAppend(ActionEvent event) {
        insertAtIndexLogic(size, insertValueField.getText());
    }

    @FXML
    void btnInsertAtIndex(ActionEvent event) {
        try {
            int idx = Integer.parseInt(insertIndexField.getText().trim());
            insertAtIndexLogic(idx, insertValueField.getText());
        } catch (NumberFormatException e) {
            addToLog("Error: Invalid Index");
        }
    }

    private void insertAtIndexLogic(int index, String valueStr) {
        try {
            int value = Integer.parseInt(valueStr.trim());

            if(index < 0 || index > size) {
                addToLog("Error: Index " + index + " out of bounds");
                return;
            }
            if(size == capacity) growArray();

            // Logic: Append is instant, Insert in middle is animated
            if (index == size) finalizeInsert(index, value);
            else animateRightShift(index, value);

        } catch (NumberFormatException e) {
            addToLog("Invalid Input");
        }
    }

    private void animateRightShift(int targetIndex, int newValue) {
        setPseudocode(INSERT_CODE);
        SequentialTransition masterSequence = new SequentialTransition();
        double speed = getDuration();

        // 1. Highlight Loop Start
        masterSequence.getChildren().add(createCodeHighlightStep(1, "Starting loop from " + (size-1)));

        // 2. Loop Backwards
        for (int i = size - 1; i >= targetIndex; i--) {
            final int currentI = i;

            // STEP A: UI Updates
            PauseTransition updateUI = new PauseTransition(Duration.ONE);
            updateUI.setOnFinished(e -> {
                highlightLine(2);
                addToLog("Shifting index " + currentI + " -> " + (currentI+1));
            });

            // STEP B: Visual Move
            Node node = visualPane.lookup("#box-" + i);
            SequentialTransition blockMove = new SequentialTransition();

            if (node instanceof StackPane) {
                StackPane stack = (StackPane) node;
                Rectangle rect = (Rectangle) stack.getChildren().get(0);

                FillTransition ft = new FillTransition(Duration.millis(speed * 0.3), rect);
                ft.setToValue(Color.web("#F59E0B")); // Yellow
                ft.setFromValue((Color) rect.getFill());

                TranslateTransition tt = new TranslateTransition(Duration.millis(speed), stack);
                tt.setByX(70);

                blockMove.getChildren().addAll(ft, tt);
            }

            SequentialTransition step = new SequentialTransition(updateUI, blockMove);
            masterSequence.getChildren().add(step);
        }

        // 3. Finalize
        masterSequence.setOnFinished(e -> {
            finalizeInsert(targetIndex, newValue);
            highlightLine(4);
            addToLog("Inserted " + newValue + " at " + targetIndex);
        });

        masterSequence.play();
    }

    private void finalizeInsert(int index, int value) {
        // Shift actual data
        for (int i = size; i > index; i--) {
            arrayData[i] = arrayData[i-1];
        }
        arrayData[index] = value;
        size++;
        render();
        highlightLine(5);
    }

    // ================= 3. REMOVE OPERATIONS =================
    @FXML
    void btnRemoveLast(ActionEvent event) {
        removeAtIndexLogic(size - 1);
    }

    @FXML
    void btnRemoveAtIndex(ActionEvent event) {
        try {
            int idx = Integer.parseInt(removeIndexField.getText().trim());
            removeAtIndexLogic(idx);
        } catch (NumberFormatException e) {
            addToLog("Error: Invalid Index");
        }
    }

    private void removeAtIndexLogic(int index) {
        if (index < 0 || index >= size) {
            addToLog("Error: Index out of bounds");
            return;
        }

        // Shift Left
        for (int i = index; i < size - 1; i++) {
            arrayData[i] = arrayData[i+1];
        }
        arrayData[size - 1] = 0;
        size--;
        render();
        addToLog("Removed element at index " + index);
    }

    // ================= 4. SEARCH & SELECT =================
    @FXML
    void btnFindMax(ActionEvent event) {
        if(size == 0) return;
        int maxIndex = 0;
        for(int i=1; i<size; i++) {
            if(arrayData[i] > arrayData[maxIndex]) maxIndex = i;
        }
        flashNode(maxIndex, Color.ORANGE);
        addToLog("Max Value: " + arrayData[maxIndex]);
    }

    @FXML
    void btnFindMin(ActionEvent event) {
        if(size == 0) return;
        int minIndex = 0;
        for(int i=1; i<size; i++) {
            if(arrayData[i] < arrayData[minIndex]) minIndex = i;
        }
        flashNode(minIndex, Color.ORANGE);
        addToLog("Min Value: " + arrayData[minIndex]);
    }

    @FXML
    void btnLinearSearch(ActionEvent event) {
        try {
            int target = Integer.parseInt(searchValField.getText().trim());
            for(int i=0; i<size; i++) {
                if(arrayData[i] == target) {
                    flashNode(i, Color.GREEN);
                    addToLog("Found " + target + " at index " + i);
                    return;
                }
            }
            addToLog("Value " + target + " not found.");
        } catch(Exception e) { addToLog("Invalid Search Input"); }
    }

    // ================= 5. SPECIAL OPS =================
    @FXML
    void btnRemoveDuplicates(ActionEvent event) {
        Set<Integer> unique = new HashSet<>();
        int newSize = 0;
        for(int i=0; i<size; i++) {
            if(!unique.contains(arrayData[i])) {
                unique.add(arrayData[i]);
                arrayData[newSize++] = arrayData[i];
            }
        }
        for(int i=newSize; i<size; i++) arrayData[i] = 0;
        size = newSize;
        render();
        addToLog("Duplicates removed.");
    }

    @FXML
    void btnCountOccurrences(ActionEvent event) {
        try {
            int target = Integer.parseInt(countValField.getText().trim());
            int count = 0;
            for(int i=0; i<size; i++) {
                if(arrayData[i] == target) {
                    count++;
                    flashNode(i, Color.YELLOW);
                }
            }
            addToLog("Count of " + target + ": " + count);
        } catch(Exception e) { addToLog("Invalid Count Input"); }
    }

    // ================= HELPER FUNCTIONS =================

    private void growArray() {
        capacity *= 2;
        int[] newArr = new int[capacity];
        System.arraycopy(arrayData, 0, newArr, 0, size);
        arrayData = newArr;
        render();
        addToLog("Array Resized to " + capacity);
    }

    private void setPseudocode(String[] lines) {
        pseudoCodeList.setItems(FXCollections.observableArrayList(lines));
    }

    private void highlightLine(int lineIndex) {
        pseudoCodeList.getSelectionModel().select(lineIndex);
        pseudoCodeList.scrollTo(lineIndex);
    }

    private SequentialTransition createCodeHighlightStep(int line, String logMsg) {
        PauseTransition pt = new PauseTransition(Duration.millis(200));
        pt.setOnFinished(e -> {
            highlightLine(line);
            if(logMsg != null) addToLog(logMsg);
        });
        return new SequentialTransition(pt);
    }

    private void addToLog(String msg) {
        messageLogList.getItems().add(msg);
        messageLogList.scrollTo(messageLogList.getItems().size() - 1);
        statusLabel.setText(msg);
    }

    private void render() {
        visualPane.getChildren().clear();
        double startX = 50, startY = 250, boxSize = 60, spacing = 10;

        Text stats = new Text("Size: " + size + " | Capacity: " + capacity);
        stats.setFont(Font.font(20)); stats.setFill(Color.WHITE);
        stats.setLayoutX(50); stats.setLayoutY(50);
        visualPane.getChildren().add(stats);

        for (int i = 0; i < capacity; i++) {
            StackPane stack = new StackPane();
            stack.setLayoutX(startX + i * (boxSize + spacing));
            stack.setLayoutY(startY);

            Rectangle rect = new Rectangle(boxSize, boxSize);
            rect.setArcWidth(10); rect.setArcHeight(10);
            rect.setStroke(Color.web("#334155")); rect.setStrokeWidth(2);

            Text valText = new Text();
            valText.setFont(Font.font(18));

            if (i < size) {
                rect.setFill(Color.web("#38BDF8")); // Blue
                valText.setText(String.valueOf(arrayData[i]));
            } else {
                rect.setFill(Color.TRANSPARENT);
                valText.setText("");
            }
            Text idxText = new Text(String.valueOf(i));
            idxText.setFill(Color.GRAY);
            idxText.setLayoutX(startX + i * (boxSize + spacing) + 25);
            idxText.setLayoutY(startY + boxSize + 25);

            stack.getChildren().addAll(rect, valText);
            stack.setId("box-" + i);
            visualPane.getChildren().addAll(stack, idxText);
        }
    }

    private void flashNode(int index, Color color) {
        Node node = visualPane.lookup("#box-" + index);
        if (node instanceof StackPane) {
            StackPane stack = (StackPane) node;
            Rectangle rect = (Rectangle) stack.getChildren().get(0);
            Color original = (Color) rect.getFill();
            rect.setFill(color);
            PauseTransition pt = new PauseTransition(Duration.millis(800));
            pt.setOnFinished(e -> rect.setFill(original));
            pt.play();
        }
    }

    private double getDuration() { return 1100 - (speedSlider.getValue() * 10); }

    @FXML
    void onBackClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}