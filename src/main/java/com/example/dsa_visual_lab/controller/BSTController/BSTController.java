package com.example.dsa_visual_lab.controller.BSTController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class BSTController {

    @FXML private Pane visualPane;
    @FXML private TextField valueField;
    @FXML private Label statusLabel;
    @FXML private ListView<String> pseudoCodeList;

    private TreeNode root;
    private final double RADIUS = 22;
    private final double VERTICAL_GAP = 70;

    // Internal Node Class with Frequency Counter
    private class TreeNode {
        int value;
        int count;
        TreeNode left, right;
        double x, y;

        TreeNode(int value) {
            this.value = value;
            this.count = 1;
        }
    }

    @FXML
    public void initialize() {

        pseudoCodeList.getItems().addAll(
                "if insertion point is found",
                "  create new vertex",
                "if value < current key",
                "  go left",
                "else if value > current key",
                "  go right",
                "else increment frequency"
        );
    }

    // ================= Animation & Insertion =================

    @FXML
    public void onInsert(ActionEvent event) {
        try {
            int val = Integer.parseInt(valueField.getText());
            statusLabel.setText("Starting insertion for: " + val);
            animateInsertion(val);
            valueField.clear();
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input!");
        }
    }

    private void animateInsertion(int val) {
        if (root == null) {
            highlightPseudo(0);
            root = new TreeNode(val);
            highlightPseudo(1);
            drawTree(null);
            return;
        }

        Timeline timeline = new Timeline();
        final TreeNode[] current = {root};

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1.2), e -> {
            drawTree(current[0]); // Highlight current node being compared

            if (val < current[0].value) {
                highlightPseudo(2);
                if (current[0].left == null) {
                    highlightPseudo(0);
                    current[0].left = new TreeNode(val);
                    highlightPseudo(1);
                    stopAnimation(timeline);
                } else {
                    current[0] = current[0].left;
                    highlightPseudo(3);
                }
            } else if (val > current[0].value) {
                highlightPseudo(4);
                if (current[0].right == null) {
                    highlightPseudo(0);
                    current[0].right = new TreeNode(val);
                    highlightPseudo(1);
                    stopAnimation(timeline);
                } else {
                    current[0] = current[0].right;
                    highlightPseudo(5);
                }
            } else {
                highlightPseudo(6);
                current[0].count++;
                stopAnimation(timeline);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopAnimation(Timeline t) {
        t.stop();
        drawTree(null);
        statusLabel.setText("Insertion Complete");
    }

    private void highlightPseudo(int index) {
        pseudoCodeList.getSelectionModel().select(index);
    }

    // ================= Randomization =================

    @FXML
    public void onRandomize(ActionEvent event) {
        onClear();
        Random random = new Random();
        int nodesToCreate = 6 + random.nextInt(3);
        for (int i = 0; i < nodesToCreate; i++) {
            // Generate value and insert without animation for bulk creation
            int val = random.nextInt(199) - 99;
            root = silentInsert(root, val);
        }
        drawTree(null);
        statusLabel.setText("Generated Random BST");
    }

    private TreeNode silentInsert(TreeNode node, int val) {
        if (node == null) return new TreeNode(val);
        if (val < node.value) node.left = silentInsert(node.left, val);
        else if (val > node.value) node.right = silentInsert(node.right, val);
        else node.count++;
        return node;
    }

    // ================= Visualization Logic =================

    private void drawTree(TreeNode activeNode) {
        visualPane.getChildren().clear();
        if (root != null) {
            double startX = visualPane.getWidth() / 2;
            double startY = 50;
            double hGap = visualPane.getWidth() / 4;
            renderRecursive(root, startX, startY, hGap, activeNode);
        }
    }

    private void renderRecursive(TreeNode node, double x, double y, double hGap, TreeNode activeNode) {
        node.x = x;
        node.y = y;

        if (node.left != null) {
            drawEdge(x, y, x - hGap, y + VERTICAL_GAP);
            renderRecursive(node.left, x - hGap, y + VERTICAL_GAP, hGap / 2, activeNode);
        }
        if (node.right != null) {
            drawEdge(x, y, x + hGap, y + VERTICAL_GAP);
            renderRecursive(node.right, x + hGap, y + VERTICAL_GAP, hGap / 2, activeNode);
        }

        // Draw the vertex
        Circle circle = new Circle(x, y, RADIUS);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(2);

        // Highlight logic
        if (node == activeNode) {
            circle.setFill(Color.web("#38BDF8")); // Cyan highlight for traversal
            circle.setStrokeWidth(4);
        } else {
            circle.setFill(Color.web("#F59E0B")); // Amber default
        }

        Text valText = new Text(String.valueOf(node.value));
        valText.setFont(Font.font("System", FontWeight.BOLD, 14));
        valText.setFill(Color.web("#0F172A"));

        StackPane nodeStack = new StackPane(circle, valText);
        nodeStack.setLayoutX(x - RADIUS);
        nodeStack.setLayoutY(y - RADIUS);
        visualPane.getChildren().add(nodeStack);

        // Duplicate badge
        if (node.count > 1) {
            drawBadge(x, y, node.count);
        }
    }

    private void drawEdge(double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.web("#64748B"));
        line.setStrokeWidth(2);
        visualPane.getChildren().add(line);
    }

    private void drawBadge(double x, double y, int count) {
        Circle badgeCircle = new Circle(x + 15, y - 15, 10);
        badgeCircle.setFill(Color.web("#EF4444"));
        Text badgeText = new Text(x + 10, y - 12, "x" + count);
        badgeText.setFill(Color.WHITE);
        badgeText.setFont(Font.font("System", FontWeight.BOLD, 10));
        visualPane.getChildren().addAll(badgeCircle, badgeText);
    }

    @FXML
    public void onClear() {
        root = null;
        visualPane.getChildren().clear();
        statusLabel.setText("Tree Cleared");
    }

    // ================= Back Navigation =================

    @FXML
    public void onBackClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(root);


            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/com/example/dsa_visual_lab/view/styles/home.css").toExternalForm());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}