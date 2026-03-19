package com.example.dsa_visual_lab.controller.graph;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class GraphController {

    @FXML private Pane visualPane;
    @FXML private ToggleButton btnAddVertex, btnAddEdge, btnSelect;
    @FXML private ComboBox<String> traversalDropdown;
    @FXML private TextField startNodeField;
    @FXML private Label statusLabel, complexityLabel;
    @FXML private VBox pseudoCodeBox;
    @FXML private HBox controlsBox;
    @FXML private Slider speedSlider;

    private int nextNodeId = 0;
    private final Map<Integer, GraphNode> nodes = new HashMap<>();
    private final List<GraphEdge> edges = new ArrayList<>();
    private final Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

    private GraphNode edgeStartNode = null;
    private Object selectedItem = null; // Can be a GraphNode or GraphEdge

    private static final double RADIUS = 20;
    private static final String CODE_COLOR = "#34D399";
    private static final String HIGHLIGHT_BG = "#374151";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    private class GraphNode {
        int id;
        StackPane visual;
        Circle circle;

        GraphNode(int id, double x, double y) {
            this.id = id;
            circle = new Circle(RADIUS);
            circle.setFill(Color.web("#1E293B"));
            circle.setStroke(Color.web("#34D399"));
            circle.setStrokeWidth(2);

            Text text = new Text(String.valueOf(id));
            text.setFill(Color.WHITE);
            text.setFont(Font.font("System", FontWeight.BOLD, 14));

            visual = new StackPane(circle, text);
            visual.setLayoutX(x - RADIUS);
            visual.setLayoutY(y - RADIUS);

            visual.setOnMouseClicked(e -> handleNodeClick(this, e));
        }
    }

    private class GraphEdge {
        GraphNode source, target;
        Line line;

        GraphEdge(GraphNode source, GraphNode target) {
            this.source = source;
            this.target = target;
            line = new Line(source.visual.getLayoutX() + RADIUS, source.visual.getLayoutY() + RADIUS,
                    target.visual.getLayoutX() + RADIUS, target.visual.getLayoutY() + RADIUS);
            line.setStroke(Color.web("#64748B"));
            line.setStrokeWidth(3);

            line.setOnMouseClicked(e -> handleEdgeClick(this, e));
        }
    }

    @FXML
    public void initialize() {
        complexityLabel.setText("Waiting for action...");
        traversalDropdown.getItems().addAll("BFS", "DFS");
        traversalDropdown.getSelectionModel().selectFirst();
        setupPseudoCode(new String[]{"// Ready to build graph"});
    }

    private Duration getStepDuration() {
        double multiplier = 100.0 / (speedSlider != null ? speedSlider.getValue() : 100.0);
        return Duration.millis(600 * multiplier);
    }

    @FXML
    public void onCanvasClick(MouseEvent event) {
        if (event.getTarget() != visualPane) return; // Ignore clicks directly on nodes/edges

        if (btnAddVertex.isSelected()) {
            GraphNode newNode = new GraphNode(nextNodeId++, event.getX(), event.getY());
            nodes.put(newNode.id, newNode);
            adjacencyList.put(newNode.id, new ArrayList<>());
            visualPane.getChildren().add(newNode.visual);
            setStatus("Added Vertex " + newNode.id, false);
        } else {
            clearSelection();
        }
    }

    private void handleNodeClick(GraphNode node, MouseEvent event) {
        if (btnAddEdge.isSelected()) {
            if (edgeStartNode == null) {
                edgeStartNode = node;
                node.circle.setStroke(Color.web("#FCD34D")); // Highlight as start
                setStatus("Select target vertex to create edge.", false);
            } else {
                if (edgeStartNode != node) {
                    createEdge(edgeStartNode, node);
                }
                edgeStartNode.circle.setStroke(Color.web("#34D399")); // Reset
                edgeStartNode = null;
            }
        } else if (btnSelect.isSelected()) {
            clearSelection();
            selectedItem = node;
            node.circle.setStroke(Color.web("#EF4444")); // Highlight red for deletion
            setStatus("Selected Vertex " + node.id, false);
        }
    }

    private void handleEdgeClick(GraphEdge edge, MouseEvent event) {
        if (btnSelect.isSelected()) {
            clearSelection();
            selectedItem = edge;
            edge.line.setStroke(Color.web("#EF4444"));
            setStatus("Selected Edge between " + edge.source.id + " and " + edge.target.id, false);
        }
    }

    private void createEdge(GraphNode u, GraphNode v) {
        if (adjacencyList.get(u.id).contains(v.id)) return; // No duplicates

        GraphEdge newEdge = new GraphEdge(u, v);
        edges.add(newEdge);
        adjacencyList.get(u.id).add(v.id);
        adjacencyList.get(v.id).add(u.id); // Undirected graph

        visualPane.getChildren().add(0, newEdge.line); // Add behind nodes
        setStatus("Edge created: " + u.id + " - " + v.id, false);
    }

    @FXML
    public void onDeleteSelected(ActionEvent event) {
        if (selectedItem == null) return;

        if (selectedItem instanceof GraphNode) {
            GraphNode node = (GraphNode) selectedItem;
            visualPane.getChildren().remove(node.visual);
            nodes.remove(node.id);
            adjacencyList.remove(node.id);

            // Remove connected edges
            Iterator<GraphEdge> it = edges.iterator();
            while (it.hasNext()) {
                GraphEdge edge = it.next();
                if (edge.source == node || edge.target == node) {
                    visualPane.getChildren().remove(edge.line);
                    it.remove();
                    // Clean adjacency list of other nodes
                    if (adjacencyList.containsKey(edge.source.id)) adjacencyList.get(edge.source.id).remove(Integer.valueOf(node.id));
                    if (adjacencyList.containsKey(edge.target.id)) adjacencyList.get(edge.target.id).remove(Integer.valueOf(node.id));
                }
            }
            setStatus("Deleted Vertex " + node.id, false);
        } else if (selectedItem instanceof GraphEdge) {
            GraphEdge edge = (GraphEdge) selectedItem;
            visualPane.getChildren().remove(edge.line);
            edges.remove(edge);
            adjacencyList.get(edge.source.id).remove(Integer.valueOf(edge.target.id));
            adjacencyList.get(edge.target.id).remove(Integer.valueOf(edge.source.id));
            setStatus("Deleted Edge", false);
        }
        selectedItem = null;
    }

    private void clearSelection() {
        if (selectedItem instanceof GraphNode) {
            ((GraphNode) selectedItem).circle.setStroke(Color.web("#34D399"));
        } else if (selectedItem instanceof GraphEdge) {
            ((GraphEdge) selectedItem).line.setStroke(Color.web("#64748B"));
        }
        selectedItem = null;
    }

    private void resetVisuals() {
        for (GraphNode node : nodes.values()) {
            node.circle.setFill(Color.web("#1E293B"));
            node.circle.setStroke(Color.web("#34D399"));
        }
        for (GraphEdge edge : edges) {
            edge.line.setStroke(Color.web("#64748B"));
            edge.line.setStrokeWidth(3);
        }
        clearSelection();
    }

    @FXML
    public void onTraverse(ActionEvent event) {
        if (nodes.isEmpty()) { setStatus("Graph is empty!", true); return; }

        try {
            int startId = Integer.parseInt(startNodeField.getText().trim());
            if (!nodes.containsKey(startId)) { setStatus("Start node does not exist!", true); return; }

            resetVisuals();
            controlsBox.setDisable(true);

            String algo = traversalDropdown.getValue();
            if ("BFS".equals(algo)) {
                executeBFS(startId);
            } else {
                executeDFS(startId);
            }
        } catch (NumberFormatException e) {
            setStatus("Invalid start node ID!", true);
        }
    }

    private void executeBFS(int startNode) {
        complexityLabel.setText("O(V + E) Time / O(V) Space\nExplores graph level by level.");
        String[] code = {
                "BFS(start):",
                "  queue.enqueue(start)",
                "  mark start as visited",
                "  while queue is not empty:",
                "    curr = queue.dequeue()",
                "    for each neighbor in adj[curr]:",
                "      if neighbor is unvisited:",
                "        mark neighbor visited",
                "        queue.enqueue(neighbor)"
        };
        setupPseudoCode(code);
        setStatus("Starting BFS from " + startNode, false);

        List<Runnable> steps = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        steps.add(() -> highlightLine(1));
        queue.add(startNode);
        visited.add(startNode);
        steps.add(() -> { highlightLine(2); highlightNode(startNode, "#38BDF8"); }); // Blue for in queue

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            steps.add(() -> highlightLine(4));
            steps.add(() -> { highlightLine(4); highlightNode(curr, "#FCD34D"); }); // Yellow for current

            for (int neighbor : adjacencyList.get(curr)) {
                steps.add(() -> highlightLine(5));
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    steps.add(() -> {
                        highlightLine(7);
                        highlightEdge(curr, neighbor, "#38BDF8");
                        highlightNode(neighbor, "#38BDF8"); // Blue for queued
                    });
                    queue.add(neighbor);
                }
            }
            steps.add(() -> { highlightNode(curr, "#A78BFA"); }); // Purple for fully processed
        }
        steps.add(() -> { highlightLine(-1); controlsBox.setDisable(false); setStatus("BFS Complete", false); });

        playAnimationSequence(steps, 0);
    }

    private void executeDFS(int startNode) {
        complexityLabel.setText("O(V + E) Time / O(V) Space\nExplores as far as possible along each branch.");
        String[] code = {
                "DFS(start):",
                "  stack.push(start)",
                "  while stack is not empty:",
                "    curr = stack.pop()",
                "    if curr is unvisited:",
                "      mark curr as visited",
                "      for each neighbor in adj[curr]:",
                "        if neighbor is unvisited:",
                "          stack.push(neighbor)"
        };
        setupPseudoCode(code);
        setStatus("Starting DFS from " + startNode, false);

        List<Runnable> steps = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        Set<Integer> visited = new HashSet<>();

        steps.add(() -> highlightLine(1));
        stack.push(startNode);

        while (!stack.isEmpty()) {
            int curr = stack.pop();
            steps.add(() -> highlightLine(3));

            if (!visited.contains(curr)) {
                visited.add(curr);
                steps.add(() -> { highlightLine(5); highlightNode(curr, "#FCD34D"); }); // Current active

                for (int neighbor : adjacencyList.get(curr)) {
                    steps.add(() -> highlightLine(6));
                    if (!visited.contains(neighbor)) {
                        steps.add(() -> {
                            highlightLine(8);
                            highlightEdge(curr, neighbor, "#F59E0B"); // Pending path
                        });
                        stack.push(neighbor);
                    }
                }
                steps.add(() -> { highlightNode(curr, "#A78BFA"); }); // Processed
            }
        }
        steps.add(() -> { highlightLine(-1); controlsBox.setDisable(false); setStatus("DFS Complete", false); });

        playAnimationSequence(steps, 0);
    }

    private void playAnimationSequence(List<Runnable> steps, int index) {
        if (index >= steps.size()) return;

        steps.get(index).run();
        PauseTransition delay = new PauseTransition(getStepDuration());
        delay.setOnFinished(e -> playAnimationSequence(steps, index + 1));
        delay.play();
    }

    private void highlightNode(int id, String colorHex) {
        if (nodes.containsKey(id)) {
            nodes.get(id).circle.setFill(Color.web(colorHex));
            nodes.get(id).circle.setStroke(Color.WHITE);
        }
    }

    private void highlightEdge(int u, int v, String colorHex) {
        for (GraphEdge edge : edges) {
            if ((edge.source.id == u && edge.target.id == v) || (edge.source.id == v && edge.target.id == u)) {
                edge.line.setStroke(Color.web(colorHex));
                edge.line.setStrokeWidth(5);
                return;
            }
        }
    }

    @FXML
    public void onClear(ActionEvent event) {
        visualPane.getChildren().clear();
        nodes.clear();
        edges.clear();
        adjacencyList.clear();
        nextNodeId = 0;
        selectedItem = null;
        edgeStartNode = null;
        setStatus("Graph Cleared", false);
        setupPseudoCode(new String[]{""});
        complexityLabel.setText("Cleared");
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        if (isError) statusLabel.setStyle("-fx-text-fill: #F87171; -fx-background-color: #334155; -fx-padding: 10 25; -fx-background-radius: 12; -fx-border-color: #475569; -fx-border-radius: 12;");
        else statusLabel.setStyle("-fx-text-fill: #FCD34D; -fx-background-color: #334155; -fx-padding: 10 25; -fx-background-radius: 12; -fx-border-color: #475569; -fx-border-radius: 12;");
    }

    private void setupPseudoCode(String[] lines) {
        pseudoCodeBox.getChildren().clear();
        for (String line : lines) {
            Label lbl = new Label(line);
            lbl.setTextFill(Color.web(CODE_COLOR));
            lbl.setFont(Font.font("Consolas", 14));
            lbl.setStyle("-fx-padding: 4; -fx-background-radius: 4;");
            pseudoCodeBox.getChildren().add(lbl);
        }
    }

    private void highlightLine(int index) {
        for (int i = 0; i < pseudoCodeBox.getChildren().size(); i++) {
            Label lbl = (Label) pseudoCodeBox.getChildren().get(i);
            if (i == index) {
                lbl.setStyle("-fx-padding: 4; -fx-background-color: " + HIGHLIGHT_BG + "; -fx-background-radius: 4;");
                lbl.setTextFill(Color.web(HIGHLIGHT_TEXT));
            } else {
                lbl.setStyle("-fx-padding: 4; -fx-background-color: transparent; -fx-background-radius: 4;");
                lbl.setTextFill(Color.web(CODE_COLOR));
            }
        }
    }

    @FXML
    public void onBackClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}