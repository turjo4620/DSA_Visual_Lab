package com.example.dsa_visual_lab.controller.linear;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class ArrayController {

    @FXML private Pane visualPane;
    @FXML private Label statusLabel, lblSizeCap, complexityLabel;
    @FXML private Slider speedSlider;
    @FXML private TextField insertValueField, insertIndexField, searchValField, capacityField, initValuesField;
    @FXML private VBox pseudoCodeBox, controlsBox;

    private int[] arrayData;
    private int size = 0;
    private int capacity = 8;

    private static final double BOX_SIZE = 60;
    private static final double SPACING = 10;
    private static final String CODE_COLOR = "#34D399";
    private static final String HIGHLIGHT_BG = "#334155";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    @FXML
    public void initialize() {
        arrayData = new int[capacity];
        render();
        updateSizeAndCapacityText();
    }

    private Duration getStepDuration() {
        double multiplier = 100.0 / speedSlider.getValue();
        return Duration.millis(500 * multiplier);
    }

    @FXML
    void btnAppend(ActionEvent event) {
        String valStr = insertValueField.getText().trim();
        if (valStr.isEmpty()) { setStatus("Enter a value", true); return; }

        int val = Integer.parseInt(valStr);
        insertValueField.clear();

        complexityLabel.setText("O(1) Amortized\nAppending to end is fast, unless resize is needed (O(N)).");
        String[] codeLines = {
                "append(val):",
                "  if size == capacity:",
                "    resizeArray()",
                "  array[size] = val",
                "  size++"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Append...", false);

        PauseTransition step1 = new PauseTransition(getStepDuration());
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(getStepDuration().multiply(2));
        step2.setOnFinished(e -> {
            if (size >= capacity) {
                highlightLine(2);
                growArray();
            } else {
                highlightLine(3);
            }
        });

        PauseTransition step3 = new PauseTransition(getStepDuration().multiply(3));
        step3.setOnFinished(e -> {
            highlightLine(4);
            arrayData[size] = val;
            size++;
            render();
            highlightNode(size - 1, Color.web("#4ADE80"));
            updateSizeAndCapacityText();
            setStatus("Appended " + val, false);
        });

        PauseTransition step4 = new PauseTransition(getStepDuration().multiply(4));
        step4.setOnFinished(e -> {
            highlightLine(-1);
        });

        step1.play(); step2.play(); step3.play(); step4.play();
    }

    @FXML
    void btnInsertAtIndex(ActionEvent event) {
        String valStr = insertValueField.getText().trim();
        String idxStr = insertIndexField.getText().trim();
        if (valStr.isEmpty() || idxStr.isEmpty()) { setStatus("Enter value and index", true); return; }

        int val = Integer.parseInt(valStr);
        int idx = Integer.parseInt(idxStr);
        if (idx < 0 || idx > size) { setStatus("Index out of bounds", true); return; }

        insertValueField.clear();
        insertIndexField.clear();

        complexityLabel.setText("O(N) - Linear Time\nRequires shifting all subsequent elements right.");
        String[] codeLines = {
                "insert(idx, val):",
                "  if size == capacity: resize()",
                "  for i = size - 1 down to idx:",
                "    array[i + 1] = array[i]",
                "  array[idx] = val",
                "  size++"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Insert...", false);

        if (size >= capacity) growArray();
        highlightLine(2);

        PauseTransition delay = new PauseTransition(getStepDuration());
        delay.setOnFinished(e -> animateShiftRight(size - 1, idx, val));
        delay.play();
    }

    private void animateShiftRight(int i, int targetIndex, int value) {
        if (i < targetIndex) {
            highlightLine(4);
            arrayData[targetIndex] = value;
            size++;
            render();
            updateSizeAndCapacityText();
            highlightNode(targetIndex, Color.web("#4ADE80"));
            setStatus("Inserted " + value + " at index " + targetIndex, false);

            PauseTransition end = new PauseTransition(getStepDuration());
            end.setOnFinished(ev -> {
                highlightLine(-1);
            });
            end.play();
            return;
        }

        highlightLine(3);

        StackPane nodeToMove = getVisualNode(i);
        if (nodeToMove != null) {
            highlightNode(i, Color.web("#FCD34D"));

            TranslateTransition slide = new TranslateTransition(getStepDuration(), nodeToMove);
            slide.setByX(BOX_SIZE + SPACING);
            slide.setOnFinished(e -> {
                arrayData[i + 1] = arrayData[i];
                render();
                animateShiftRight(i - 1, targetIndex, value);
            });
            slide.play();
        } else {
            arrayData[i + 1] = arrayData[i];
            render();
            animateShiftRight(i - 1, targetIndex, value);
        }
    }

    @FXML
    void btnRemoveLast(ActionEvent event) {
        if (size == 0) { setStatus("Array is empty", true); return; }

        complexityLabel.setText("O(1) - Constant Time\nSimply updates the size counter.");
        String[] codeLines = {
                "removeLast():",
                "  if size == 0: return",
                "  array[size - 1] = 0",
                "  size--"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Remove Last...", false);

        PauseTransition step1 = new PauseTransition(getStepDuration());
        step1.setOnFinished(e -> highlightLine(2));

        PauseTransition step2 = new PauseTransition(getStepDuration().multiply(2));
        step2.setOnFinished(e -> {
            highlightLine(3);
            arrayData[size - 1] = 0;
            size--;
            render();
            updateSizeAndCapacityText();
            setStatus("Removed last element", false);
        });

        PauseTransition step3 = new PauseTransition(getStepDuration().multiply(3));
        step3.setOnFinished(e -> {
            highlightLine(-1);
        });

        step1.play(); step2.play(); step3.play();
    }

    @FXML
    void btnRemoveAtIndex(ActionEvent event) {
        String idxStr = insertIndexField.getText().trim();
        if (idxStr.isEmpty()) { setStatus("Enter index to remove", true); return; }

        int idx = Integer.parseInt(idxStr);
        if (idx < 0 || idx >= size) { setStatus("Index out of bounds", true); return; }

        insertIndexField.clear();

        complexityLabel.setText("O(N) - Linear Time\nRequires shifting subsequent elements left.");
        String[] codeLines = {
                "remove(idx):",
                "  if size == 0: return",
                "  for i = idx to size - 2:",
                "    array[i] = array[i + 1]",
                "  array[size - 1] = 0",
                "  size--"
        };
        setupPseudoCode(codeLines);
        setStatus("Animating Remove...", false);

        PauseTransition delay = new PauseTransition(getStepDuration());
        delay.setOnFinished(e -> animateShiftLeft(idx));
        delay.play();
    }

    private void animateShiftLeft(int i) {
        if (i >= size - 1) {
            highlightLine(4);
            arrayData[size - 1] = 0;
            size--;
            render();
            updateSizeAndCapacityText();
            setStatus("Removed element and shifted", false);

            PauseTransition end = new PauseTransition(getStepDuration());
            end.setOnFinished(ev -> {
                highlightLine(-1);
            });
            end.play();
            return;
        }

        highlightLine(3);

        StackPane nodeToMove = getVisualNode(i + 1);
        if (nodeToMove != null) {
            highlightNode(i + 1, Color.web("#F87171"));

            TranslateTransition slide = new TranslateTransition(getStepDuration(), nodeToMove);
            slide.setByX(-(BOX_SIZE + SPACING));
            slide.setOnFinished(e -> {
                arrayData[i] = arrayData[i + 1];
                render();
                animateShiftLeft(i + 1);
            });
            slide.play();
        } else {
            arrayData[i] = arrayData[i + 1];
            render();
            animateShiftLeft(i + 1);
        }
    }

    @FXML
    void btnLinearSearch(ActionEvent event) {
        String valStr = searchValField.getText().trim();
        if (valStr.isEmpty()) { setStatus("Enter a value to search", true); return; }

        int target = Integer.parseInt(valStr);

        complexityLabel.setText("O(N) - Linear Time\nChecks every element sequentially.");
        String[] codeLines = {
                "linearSearch(target):",
                "  for i = 0 to size - 1:",
                "    if array[i] == target:",
                "      return i",
                "  return -1"
        };
        setupPseudoCode(codeLines);
        setStatus("Searching for " + target + "...", false);

        animateSearchLoop(0, target);
    }

    private void animateSearchLoop(int i, int target) {
        if (i >= size) {
            highlightLine(4);
            setStatus("Value " + target + " not found", true);

            PauseTransition end = new PauseTransition(getStepDuration());
            end.setOnFinished(e -> {
                highlightLine(-1);
            });
            end.play();
            return;
        }

        highlightLine(2);
        highlightNode(i, Color.web("#FCD34D"));

        PauseTransition check = new PauseTransition(getStepDuration());
        check.setOnFinished(e -> {
            if (arrayData[i] == target) {
                highlightLine(3);
                highlightNode(i, Color.web("#4ADE80"));
                setStatus("Found " + target + " at index " + i, false);

                PauseTransition end = new PauseTransition(getStepDuration());
                end.setOnFinished(ev -> {
                    highlightLine(-1);
                });
                end.play();
            } else {
                render();
                animateSearchLoop(i + 1, target);
            }
        });
        check.play();
    }

    @FXML
    void btnFindMax(ActionEvent event) {
        if (size == 0) { setStatus("Array is empty", true); return; }

        complexityLabel.setText("O(N) - Linear Time\nMust check every element to find maximum.");
        String[] codeLines = {
                "findMax():",
                "  maxVal = array[0]",
                "  for i = 1 to size - 1:",
                "    if array[i] > maxVal:",
                "      maxVal = array[i]",
                "  return maxVal"
        };
        setupPseudoCode(codeLines);
        setStatus("Finding Max...", false);

        int[] maxData = { arrayData[0], 0 };
        highlightLine(1);
        highlightNode(0, Color.web("#A78BFA"));

        PauseTransition delay = new PauseTransition(getStepDuration());
        delay.setOnFinished(e -> animateMaxLoop(1, maxData));
        delay.play();
    }

    private void animateMaxLoop(int i, int[] maxData) {
        if (i >= size) {
            highlightLine(5);
            highlightNode(maxData[1], Color.web("#38BDF8"));
            setStatus("Maximum value is " + maxData[0] + " at index " + maxData[1], false);

            PauseTransition end = new PauseTransition(getStepDuration());
            end.setOnFinished(e -> {
                highlightLine(-1);
            });
            end.play();
            return;
        }

        highlightLine(3);
        highlightNode(i, Color.web("#FCD34D"));

        PauseTransition check = new PauseTransition(getStepDuration());
        check.setOnFinished(e -> {
            if (arrayData[i] > maxData[0]) {
                highlightLine(4);
                maxData[0] = arrayData[i];
                maxData[1] = i;
            }
            render();
            highlightNode(maxData[1], Color.web("#A78BFA"));
            animateMaxLoop(i + 1, maxData);
        });
        check.play();
    }

    @FXML
    void btnCreateArray(ActionEvent event) {
        try {
            capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0) capacity = 8;

            String valStr = initValuesField.getText().trim();

            if (!valStr.isEmpty()) {
                String[] strValues = valStr.split(",");
                if (strValues.length > capacity) {
                    capacity = strValues.length;
                    capacityField.setText(String.valueOf(capacity));
                }

                arrayData = new int[capacity];
                size = 0;

                for (String s : strValues) {
                    arrayData[size++] = Integer.parseInt(s.trim());
                }

                setStatus("Array initialized with " + size + " elements.", false);
            } else {
                arrayData = new int[capacity];
                size = 0;
                setStatus("Empty array created with capacity " + capacity, false);
            }

            render();
            updateSizeAndCapacityText();
            pseudoCodeBox.getChildren().clear();
            complexityLabel.setText("Cleared");

        } catch (NumberFormatException e) {
            setStatus("Error: Make sure values are numbers (e.g. 5, 10, 15)", true);
        }
    }

    private void growArray() {
        capacity *= 2;
        int[] newArr = new int[capacity];
        System.arraycopy(arrayData, 0, newArr, 0, size);
        arrayData = newArr;
        render();
        updateSizeAndCapacityText();
    }

    private void render() {
        visualPane.getChildren().clear();
        double startX = 20;
        double startY = 150;

        for (int i = 0; i < capacity; i++) {
            StackPane stack = new StackPane();
            stack.setLayoutX(startX + i * (BOX_SIZE + SPACING));
            stack.setLayoutY(startY);

            Rectangle rect = new Rectangle(BOX_SIZE, BOX_SIZE);
            rect.setArcWidth(5);
            rect.setArcHeight(5);
            rect.setStrokeWidth(2);

            Text valText = new Text();
            valText.setFill(Color.WHITE);
            valText.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 18));

            Text idxText = new Text(String.valueOf(i));
            idxText.setFill(Color.web("#94A3B8"));
            idxText.setFont(Font.font("System", 14));
            idxText.setLayoutX(startX + i * (BOX_SIZE + SPACING) + (BOX_SIZE / 2) - 5);
            idxText.setLayoutY(startY + BOX_SIZE + 20);

            if (i < size) {
                rect.setFill(Color.web("#1E293B"));
                rect.setStroke(Color.web("#38BDF8"));
                valText.setText(String.valueOf(arrayData[i]));
            } else {
                rect.setFill(Color.TRANSPARENT);
                rect.setStroke(Color.web("#334155"));
                rect.getStrokeDashArray().addAll(5d, 5d);
                valText.setText("");
            }

            stack.getChildren().addAll(rect, valText);
            visualPane.getChildren().addAll(stack, idxText);
        }
        visualPane.setMinWidth((capacity * (BOX_SIZE + SPACING)) + 40);
    }

    private StackPane getVisualNode(int index) {
        int targetNodeIndex = index * 2;
        if (targetNodeIndex < visualPane.getChildren().size()) {
            Node n = visualPane.getChildren().get(targetNodeIndex);
            if (n instanceof StackPane) {
                return (StackPane) n;
            }
        }
        return null;
    }

    private void highlightNode(int index, Color color) {
        StackPane node = getVisualNode(index);
        if (node != null) {
            Rectangle box = (Rectangle) node.getChildren().get(0);
            box.setStroke(color);
            box.setStrokeWidth(3);
            box.getStrokeDashArray().clear();
        }
    }

    private void updateSizeAndCapacityText() {
        lblSizeCap.setText("Size = " + size + " | Capacity = " + capacity);
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
            lbl.setFont(Font.font("Consolas", 14));
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

    @FXML
    void onBackClick(ActionEvent event) {
        try {
            String path = "/com/example/dsa_visual_lab/view/home/linear-dataStructures.fxml";
            URL url = getClass().getResource(path);
            if (url == null) {
                path = "/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml";
                url = getClass().getResource(path);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}