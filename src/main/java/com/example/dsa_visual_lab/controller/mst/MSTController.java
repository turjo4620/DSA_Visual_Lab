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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

    @FXML
    private Pane graphPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Slider speedSlider;
    @FXML
    private TextField edgeWeightField;
    @FXML
    private HBox controlsBox;

    @FXML
    private Label line1, line2, line3, line4, line5, line6;
    private Label[] pseudoLines;

    @FXML
    private ToggleButton btnAddNode, btnAddEdge, btnSelect;
    @FXML
    private ToggleGroup modeGroup;

    private int nextNodeId = 0;
    private static final double RADIUS = 22;

    private Object selectedItem = null;
    private GraphNode edgeStart = null;
    private int[] parent;
    private List<Runnable> steps = new ArrayList<>();

    private class GraphNode {
        int id;
        StackPane visual;
        Circle circle;

        GraphNode(int id, double x, double y) {
            this.id = id;

            circle = new Circle(RADIUS);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.web("#34D399"));
            circle.setStrokeWidth(2.5);

            Text text = new Text(String.valueOf(id));
            text.setFill(Color.WHITE);
            text.setFont(Font.font("System", FontWeight.BOLD, 14));

            visual = new StackPane(circle, text);
            visual.setLayoutX(x - RADIUS);
            visual.setLayoutY(y - RADIUS);
            visual.setPrefSize(RADIUS * 2, RADIUS * 2);

            visual.setOnMouseClicked(e -> {
                e.consume();
                if (btnAddEdge.isSelected()) {
                    handleNodeClickForEdge(this);
                } else if (btnSelect.isSelected()) {
                    handleNodeClickForSelect(this);
                }
            });
        }

        double centerX() {
            return visual.getLayoutX() + RADIUS;
        }

        double centerY() {
            return visual.getLayoutY() + RADIUS;
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

            double[] start = borderPoint(u, v);
            double[] end = borderPoint(v, u);

            line = new Line(start[0], start[1], end[0], end[1]);
            line.setStroke(Color.web("#94A3B8"));
            line.setStrokeWidth(2);

            double mx = (u.centerX() + v.centerX()) / 2;
            double my = (u.centerY() + v.centerY()) / 2;
            weightLabel = new Text(String.valueOf(w));
            weightLabel.setFill(Color.web("#FACC15"));
            weightLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            weightLabel.setX(mx + 5);
            weightLabel.setY(my - 5);

            line.setOnMouseClicked(e -> {
                e.consume();
                if (btnSelect.isSelected()) {
                    handleEdgeClickForSelect(this);
                }
            });

            weightLabel.setOnMouseClicked(e -> e.consume());
        }

        double[] borderPoint(GraphNode from, GraphNode to) {
            double fx = from.centerX();
            double fy = from.centerY();
            double tx = to.centerX();
            double ty = to.centerY();
            double angle = Math.atan2(ty - fy, tx - fx);
            return new double[]{
                    fx + RADIUS * Math.cos(angle),
                    fy + RADIUS * Math.sin(angle)
            };
        }
    }

    private Map<Integer, GraphNode> nodes = new HashMap<>();
    private List<GraphEdge> edges = new ArrayList<>();

    class Edge {
        int u, v, w;

        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    @FXML
    public void initialize() {
        pseudoLines = new Label[]{line1, line2, line3, line4, line5, line6};

        graphPane.setOnMouseClicked(event -> {
            if (!btnAddNode.isSelected()) return;
            GraphNode node = new GraphNode(nextNodeId++, event.getX(), event.getY());
            nodes.put(node.id, node);
            graphPane.getChildren().add(node.visual);
            statusLabel.setText("Added Node " + node.id);
        });

        modeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
                return;
            }
            clearSelection();
            if (edgeStart != null) {
                edgeStart.circle.setStroke(Color.web("#34D399"));
                edgeStart = null;
            }
        });

        double[][] predefinedPositions = {
                {400, 100}, {200, 250}, {600, 250},
                {250, 450}, {550, 450}, {400, 300}
        };

        for (double[] pos : predefinedPositions) {
            GraphNode node = new GraphNode(nextNodeId++, pos[0], pos[1]);
            nodes.put(node.id, node);
            graphPane.getChildren().add(node.visual);
        }

        int[][] predefinedEdges = {
                {0, 1}, {0, 2}, {1, 3}, {1, 5},
                {2, 4}, {2, 5}, {3, 4}, {4, 5}, {0, 5}
        };



        Random rand = new Random();
        for (int[] pair : predefinedEdges) {
            GraphNode u = nodes.get(pair[0]);
            GraphNode v = nodes.get(pair[1]);
            int w = rand.nextInt(20) + 1;
            GraphEdge e = new GraphEdge(u, v, w);
            edges.add(e);
            graphPane.getChildren().add(0, e.line);
            graphPane.getChildren().add(e.weightLabel);
        }

        statusLabel.setText("Random Graph Initialized");
    }

    private void handleNodeClickForEdge(GraphNode node) {
        if (edgeStart == null) {
            edgeStart = node;
            node.circle.setStroke(Color.web("#FCD34D"));
            statusLabel.setText("Selected Node " + node.id + " — click target node");
        } else {
            if (edgeStart != node) {
                int w = getEdgeWeight();
                GraphEdge e = new GraphEdge(edgeStart, node, w);
                edges.add(e);
                graphPane.getChildren().add(0, e.line);
                graphPane.getChildren().add(e.weightLabel);
                statusLabel.setText("Edge (" + edgeStart.id + " → " + node.id + ") w=" + w);
            }
            edgeStart.circle.setStroke(Color.web("#34D399"));
            edgeStart = null;
        }
    }

    private void handleNodeClickForSelect(GraphNode node) {
        clearSelection();
        selectedItem = node;
        node.circle.setStroke(Color.web("#EF4444"));
        statusLabel.setText("Selected Node " + node.id + " — click Delete Selected to remove");
    }

    private void handleEdgeClickForSelect(GraphEdge edge) {
        clearSelection();
        selectedItem = edge;
        edge.line.setStroke(Color.web("#EF4444"));
        edge.line.setStrokeWidth(4);
        statusLabel.setText("Selected Edge (" + edge.u.id + " → " + edge.v.id + ") — click Delete Selected to remove");
    }

    @FXML
    public void handleDeleteSelected() {
        if (selectedItem == null) {
            statusLabel.setText("Nothing selected. Use Select mode first.");
            return;
        }

        if (selectedItem instanceof GraphNode) {
            GraphNode node = (GraphNode) selectedItem;
            Iterator<GraphEdge> it = edges.iterator();
            while (it.hasNext()) {
                GraphEdge ge = it.next();
                if (ge.u == node || ge.v == node) {
                    graphPane.getChildren().remove(ge.line);
                    graphPane.getChildren().remove(ge.weightLabel);
                    it.remove();
                }
            }
            graphPane.getChildren().remove(node.visual);
            nodes.remove(node.id);
            statusLabel.setText("Deleted Node " + node.id + " and its edges.");

        } else if (selectedItem instanceof GraphEdge) {
            GraphEdge ge = (GraphEdge) selectedItem;
            graphPane.getChildren().remove(ge.line);
            graphPane.getChildren().remove(ge.weightLabel);
            edges.remove(ge);
            statusLabel.setText("Deleted Edge (" + ge.u.id + " → " + ge.v.id + ")");
        }

        selectedItem = null;
    }

    private void clearSelection() {
        if (selectedItem instanceof GraphNode) {
            ((GraphNode) selectedItem).circle.setStroke(Color.web("#34D399"));
        } else if (selectedItem instanceof GraphEdge) {
            GraphEdge ge = (GraphEdge) selectedItem;
            ge.line.setStroke(Color.web("#94A3B8"));
            ge.line.setStrokeWidth(2);
        }
        selectedItem = null;
    }

    private int getEdgeWeight() {
        try {
            return Integer.parseInt(edgeWeightField.getText().trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    @FXML
    public void handleStart() {
        if (nodes.isEmpty() || edges.isEmpty()) {
            statusLabel.setText("Add nodes and edges first!");
            return;
        }

        if (controlsBox != null) controlsBox.setDisable(true);

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
            if (controlsBox != null) controlsBox.setDisable(false);
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


    // highlighting lines by inline css
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
        selectedItem = null;
        statusLabel.setText("Reset done.");
        for (Label l : pseudoLines)
            l.setStyle("-fx-background-color:transparent; -fx-text-fill:white;");

        if (controlsBox != null) controlsBox.setDisable(false);
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