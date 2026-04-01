package com.example.dsa_visual_lab.controller.mst;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class MSTController {

    @FXML private Pane graphPane;
    @FXML private Label statusLabel;
    @FXML private Slider speedSlider;
    @FXML private TextField edgeWeightField; // 🔥 single weight input

    @FXML private Label line1, line2, line3, line4, line5, line6;
    private Label[] pseudoLines;

    @FXML private ToggleButton btnAddNode, btnAddEdge;

    private int nextNodeId = 0;
    private static final double RADIUS = 22;

    private class GraphNode {
        int id;
        Circle circle;
        Text label;

        GraphNode(int id, double x, double y) {
            this.id = id;

            circle = new Circle(RADIUS);
            circle.setCenterX(x);
            circle.setCenterY(y);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.web("#34D399"));
            circle.setStrokeWidth(2.5);

            label = new Text(String.valueOf(id));
            label.setFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));


            layoutLabel();

            circle.setOnMouseClicked(e -> {
                if (btnAddEdge.isSelected()) {
                    handleNodeClick(this);
                    e.consume();
                }
            });
            label.setOnMouseClicked(e -> {
                if (btnAddEdge.isSelected()) {
                    handleNodeClick(this);
                    e.consume();
                }
            });
        }

        void layoutLabel() {
            // Use applyCss trick: just offset by half of approximate char width
            double tw = label.getLayoutBounds().getWidth();
            double th = label.getLayoutBounds().getHeight();
            label.setX(circle.getCenterX() - tw / 2);
            label.setY(circle.getCenterY() + th / 4);
        }
    }

    private class GraphEdge {
        GraphNode u, v;
        int w;
        Line line;
        Text weightLabel;

        GraphEdge(GraphNode u, GraphNode v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;

            // ✅ Compute start/end on circle BORDER toward the other node
            double[] start = borderPoint(u, v);
            double[] end   = borderPoint(v, u);

            line = new Line(start[0], start[1], end[0], end[1]);
            line.setStroke(Color.web("#94A3B8"));
            line.setStrokeWidth(2);

            double mx = (u.circle.getCenterX() + v.circle.getCenterX()) / 2;
            double my = (u.circle.getCenterY() + v.circle.getCenterY()) / 2;
            weightLabel = new Text(String.valueOf(w));
            weightLabel.setFill(Color.web("#FACC15"));
            weightLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            weightLabel.setX(mx + 5);
            weightLabel.setY(my - 5);
        }

        // Returns the point on node 'from' circle border facing node 'to'
        double[] borderPoint(GraphNode from, GraphNode to) {
            double fx = from.circle.getCenterX();
            double fy = from.circle.getCenterY();
            double tx = to.circle.getCenterX();
            double ty = to.circle.getCenterY();
            double angle = Math.atan2(ty - fy, tx - fx);
            return new double[]{
                    fx + RADIUS * Math.cos(angle),
                    fy + RADIUS * Math.sin(angle)
            };
        }
    }

    private Map<Integer, GraphNode> nodes = new HashMap<>();
    private List<GraphEdge> edges = new ArrayList<>();
    private GraphNode edgeStart = null;
    private int[] parent;
    private List<Runnable> steps = new ArrayList<>();

    class Edge {
        int u, v, w;
        Edge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; }
    }

    @FXML
    public void initialize() {
        pseudoLines = new Label[]{line1, line2, line3, line4, line5, line6};
    }

    private int getEdgeWeight() {
        try {
            return Integer.parseInt(edgeWeightField.getText().trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    @FXML
    public void onPaneClick(MouseEvent event) {
        if (!btnAddNode.isSelected()) return;
        if (!(event.getTarget() instanceof Pane)) return;

        double x = event.getX();
        double y = event.getY();

        GraphNode node = new GraphNode(nextNodeId++, x, y);
        nodes.put(node.id, node);
        graphPane.getChildren().addAll(node.circle, node.label);
        statusLabel.setText("Added Node " + node.id);
    }

    private void handleNodeClick(GraphNode node) {
        if (edgeStart == null) {
            edgeStart = node;
            node.circle.setStroke(Color.YELLOW);
            statusLabel.setText("Selected Node " + node.id + " — click target node");
        } else {
            if (edgeStart != node) {
                int w = getEdgeWeight();
                GraphEdge e = new GraphEdge(edgeStart, node, w);
                edges.add(e);
                // Insert line at bottom, weight label on top (above circles)
                graphPane.getChildren().add(0, e.line);
                graphPane.getChildren().add(e.weightLabel);
                statusLabel.setText("Edge (" + edgeStart.id + "→" + node.id + ") w=" + w);
            }
            edgeStart.circle.setStroke(Color.web("#34D399"));
            edgeStart = null;
        }
    }

    @FXML
    public void handleStart() {
        if (nodes.isEmpty() || edges.isEmpty()) {
            statusLabel.setText("Add nodes and edges first!");
            return;
        }

        steps.clear();
        parent = new int[nextNodeId];
        for (int i = 0; i < nextNodeId; i++) parent[i] = i;

        List<Edge> mstEdges = new ArrayList<>();
        Map<Edge, GraphEdge> edgeMap = new HashMap<>();

        for (GraphEdge ge : edges) {
            Edge e = new Edge(ge.u.id, ge.v.id, ge.w);
            mstEdges.add(e);
            edgeMap.put(e, ge);
        }

        steps.add(() -> highlightPseudo(0));
        mstEdges.sort(Comparator.comparingInt(e -> e.w));

        for (Edge e : mstEdges) {
            GraphEdge ge = edgeMap.get(e);
            steps.add(() -> {
                ge.line.setStroke(Color.YELLOW);
                ge.line.setStrokeWidth(4);
                statusLabel.setText("Checking edge (" + e.u + ", " + e.v + ") w=" + e.w);
                highlightPseudo(1);
            });

            if (find(e.u) != find(e.v)) {
                union(e.u, e.v);
                steps.add(() -> {
                    ge.line.setStroke(Color.LIMEGREEN);
                    ge.line.setStrokeWidth(4);
                    statusLabel.setText("✅ Added to MST: (" + e.u + ", " + e.v + ")");
                    highlightPseudo(3);
                });
            } else {
                steps.add(() -> {
                    ge.line.setStroke(Color.web("#6B7280"));
                    ge.line.setStrokeWidth(2);
                    statusLabel.setText("🔁 Cycle → Skipped: (" + e.u + ", " + e.v + ")");
                    highlightPseudo(4);
                });
            }
        }

        steps.add(() -> {
            statusLabel.setText("✅ MST Complete!");
            highlightPseudo(5);
        });

        playSteps(0);
    }

    private int find(int x) {
        if (parent[x] == x) return x;
        return parent[x] = find(parent[x]);
    }

    private void union(int a, int b) {
        parent[find(a)] = find(b);
    }

    private void highlightPseudo(int activeIndex) {
        for (int i = 0; i < pseudoLines.length; i++) {
            pseudoLines[i].setStyle(i == activeIndex
                    ? "-fx-background-color:#374151; -fx-text-fill:#FCD34D;"
                    : "-fx-background-color:transparent; -fx-text-fill:white;");
        }
    }

    private Duration getStepDuration() {
        double multiplier = 100.0 / (speedSlider != null ? speedSlider.getValue() : 100.0);
        return Duration.millis(500 * multiplier);
    }

    private void playSteps(int i) {
        if (i >= steps.size()) return;
        steps.get(i).run();
        PauseTransition pause = new PauseTransition(getStepDuration());
        pause.setOnFinished(e -> playSteps(i + 1));
        pause.play();
    }

    @FXML
    public void handleReset() {
        graphPane.getChildren().clear();
        nodes.clear();
        edges.clear();
        nextNodeId = 0;
        edgeStart = null;
        statusLabel.setText("Reset done.");
        for (Label l : pseudoLines)
            l.setStyle("-fx-background-color:transparent; -fx-text-fill:white;");
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/com/example/dsa_visual_lab/view/home/home-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}