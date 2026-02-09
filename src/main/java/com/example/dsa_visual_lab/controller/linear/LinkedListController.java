package com.example.dsa_visual_lab.controller.linear;

import javafx.animation.ScaleTransition;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.LinkedList;

public class LinkedListController {

    @FXML private Pane visualPane;
    @FXML private Label statusLabel;
    @FXML private TextField valueField, indexField;

    // We use a Java LinkedList to store data, but re-draw visuals manually
    private final LinkedList<String> listData = new LinkedList<>();

    // Visual Constants
    private static final double START_X = 50;
    private static final double START_Y = 150;
    private static final double NODE_WIDTH = 80;
    private static final double NODE_HEIGHT = 50;
    private static final double GAP = 60; // Space for arrow

    // ================= INSERT =================

    @FXML
    void onInsertHead(ActionEvent event) {
        String val = valueField.getText().trim();
        if (val.isEmpty()) { setStatus("Enter a value", true); return; }

        listData.addFirst(val);
        render();
        setStatus("Inserted " + val + " at Head", false);
    }

    @FXML
    void onInsertTail(ActionEvent event) {
        String val = valueField.getText().trim();
        if (val.isEmpty()) { setStatus("Enter a value", true); return; }

        listData.addLast(val);
        render();
        setStatus("Inserted " + val + " at Tail", false);
    }

    @FXML
    void onInsertIndex(ActionEvent event) {
        String val = valueField.getText().trim();
        try {
            int idx = Integer.parseInt(indexField.getText().trim());
            if (idx < 0 || idx > listData.size()) {
                setStatus("Index out of bounds", true);
                return;
            }
            listData.add(idx, val);
            render();
            setStatus("Inserted at index " + idx, false);
        } catch (NumberFormatException e) {
            setStatus("Invalid Index", true);
        }
    }

    // ================= DELETE =================

    @FXML
    void onDeleteHead(ActionEvent event) {
        if (listData.isEmpty()) { setStatus("List is empty", true); return; }
        listData.removeFirst();
        render();
        setStatus("Deleted Head", false);
    }

    @FXML
    void onDeleteTail(ActionEvent event) {
        if (listData.isEmpty()) { setStatus("List is empty", true); return; }
        listData.removeLast();
        render();
        setStatus("Deleted Tail", false);
    }

    @FXML
    void onDeleteIndex(ActionEvent event) {
        try {
            int idx = Integer.parseInt(indexField.getText().trim());
            if (idx < 0 || idx >= listData.size()) {
                setStatus("Index out of bounds", true);
                return;
            }
            listData.remove(idx);
            render();
            setStatus("Deleted at index " + idx, false);
        } catch (NumberFormatException e) {
            setStatus("Invalid Index", true);
        }
    }

    // ================= SEARCH =================
    @FXML
    void onSearch(ActionEvent event) {
        String target = valueField.getText().trim();
        int idx = listData.indexOf(target);

        if (idx != -1) {
            setStatus("Found " + target + " at index " + idx, false);
            highlightNode(idx);
        } else {
            setStatus("Value not found", true);
        }
    }

    // ================= RENDERING =================

    private void render() {
        visualPane.getChildren().clear();

        double currentX = START_X;

        for (int i = 0; i < listData.size(); i++) {
            // 1. Draw Node
            StackPane node = createNode(listData.get(i));
            node.setLayoutX(currentX);
            node.setLayoutY(START_Y);
            visualPane.getChildren().add(node);

            // 2. Draw Arrow (if not last element)
            if (i < listData.size() - 1) {
                drawArrow(currentX + NODE_WIDTH, START_Y + NODE_HEIGHT/2,
                        currentX + NODE_WIDTH + GAP, START_Y + NODE_HEIGHT/2);
            } else {
                // Draw "NULL" text after last node
                Text nullText = new Text("NULL");
                nullText.setFill(Color.web("#64748B"));
                nullText.setLayoutX(currentX + NODE_WIDTH + 15);
                nullText.setLayoutY(START_Y + NODE_HEIGHT/2 + 5);
                visualPane.getChildren().add(nullText);
            }

            // Move X for next node
            currentX += NODE_WIDTH + GAP;
        }
    }

    private StackPane createNode(String val) {
        StackPane stack = new StackPane();

        // Split node visual: Data | Next
        Rectangle box = new Rectangle(NODE_WIDTH, NODE_HEIGHT);
        box.setFill(Color.web("#1E293B"));
        box.setStroke(Color.web("#818CF8")); // Indigo Border
        box.setStrokeWidth(2);
        box.setArcWidth(10); box.setArcHeight(10);

        // Vertical line to separate Data and Pointer areas
        Line separator = new Line(NODE_WIDTH * 0.7, 0, NODE_WIDTH * 0.7, NODE_HEIGHT);
        separator.setStroke(Color.web("#818CF8"));

        Text text = new Text(val);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        text.setTranslateX(-8); // Shift left to fit in data area

        stack.getChildren().addAll(box, separator, text);
        return stack;
    }

    private void drawArrow(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.web("#94A3B8")); // Gray arrow
        line.setStrokeWidth(2);

        // Arrow Head
        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(0.0, -5.0, 10.0, 0.0, 0.0, 5.0);
        arrowHead.setFill(Color.web("#94A3B8"));
        arrowHead.setLayoutX(endX - 10);
        arrowHead.setLayoutY(endY);

        visualPane.getChildren().addAll(line, arrowHead);
    }

    private void highlightNode(int index) {
        // Simple logic: Find the node at index (based on children order)
        // Since we add Node then Arrow, the node index in children list is complex.
        // Easier: Just re-render and add a highlight check inside render loop if needed.
        // For simplicity here, just log it.
        // (If you want visual flash, you need to store references to StackPanes)
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(isError ? Color.web("#F87171") : Color.web("#34D399"));
    }

    @FXML
    void onBackClick(ActionEvent event) {
        try {
            // Try 'home' folder first, then 'Linear-DataStructure'
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
}