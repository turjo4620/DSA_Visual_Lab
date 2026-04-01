package com.example.dsa_visual_lab.controller.mst;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class MSTController {

    @FXML private Pane graphPane;
    @FXML private Label statusLabel;
    @FXML private Slider speedSlider; // NEW: speed slider
    @FXML private Label line1, line2, line3, line4, line5, line6;
    private Label[] pseudoLines;

    private List<Edge> edges = new ArrayList<>();
    private Map<Integer, Circle> nodes = new HashMap<>();
    private Map<Edge, Line> edgeLines = new HashMap<>();

    private int[] parent;
    private List<Runnable> steps = new ArrayList<>();

    class Edge {
        int u, v, w;
        Edge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; }
    }

    @FXML
    public void initialize() {
        pseudoLines = new Label[]{line1, line2, line3, line4, line5, line6};
        drawGraph();
    }

    private void drawGraph() {
        graphPane.getChildren().clear();
        nodes.clear();
        edges.clear();
        edgeLines.clear();

        double[][] pos = {{300,80},{500,200},{420,400},{180,400},{100,200}};
        for (int i=0;i<pos.length;i++){
            Circle c = new Circle(pos[i][0], pos[i][1], 20, Color.web("#38BDF8"));
            nodes.put(i,c);
            graphPane.getChildren().add(c);
        }

        addEdge(0,1,13); addEdge(0,2,19); addEdge(0,3,19); addEdge(0,4,13);
        addEdge(1,2,13); addEdge(1,3,22); addEdge(1,4,28);
        addEdge(2,3,14); addEdge(2,4,27);
        addEdge(3,4,13);
    }

    private void addEdge(int u, int v, int w){
        Edge e = new Edge(u,v,w);
        edges.add(e);

        Circle c1 = nodes.get(u);
        Circle c2 = nodes.get(v);

        Line line = new Line(c1.getCenterX(), c1.getCenterY(), c2.getCenterX(), c2.getCenterY());
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(2);

        Label weight = new Label(String.valueOf(w));
        weight.setTextFill(Color.GRAY);
        double midX = (c1.getCenterX()+c2.getCenterX()+25)/2;
        double midY = (c1.getCenterY()+c2.getCenterY()+25)/2;
        weight.setLayoutX(midX);
        weight.setLayoutY(midY);

        edgeLines.put(e,line);
        graphPane.getChildren().addAll(line, weight);
    }


    @FXML
    public void handleStart() {
        steps.clear();

        parent = new int[nodes.size()];
        for(int i=0;i<parent.length;i++) parent[i]=i;


        steps.add(() -> highlightPseudo(0)); // line1: Sort edges
        edges.sort(Comparator.comparingInt(e -> e.w));

        for (Edge e : edges) {

            steps.add(() -> {
                highlightEdge(e, Color.YELLOW, "Checking edge ("+e.u+","+e.v+")");
                highlightPseudo(1);
            });

            if(find(e.u)!=find(e.v)){
                union(e.u,e.v);
                steps.add(() -> {
                    highlightEdge(e, Color.LIMEGREEN, "Added to MST");
                    highlightPseudo(3);
                });
            } else {
                steps.add(() -> {
                    highlightEdge(e, Color.GRAY, "Cycle → Skipped");
                    highlightPseudo(4);
                });
            }
        }


        steps.add(() -> highlightPseudo(5));
        playSteps(0);
    }

    private int find(int x) {
        if(parent[x]==x) return x;
        return parent[x] = find(parent[x]);
    }

    private void union(int a,int b){
        parent[find(a)] = find(b);
    }


    private void highlightEdge(Edge e, Color color, String text){
        Line line = edgeLines.get(e);
        line.setStroke(color);
        line.setStrokeWidth(4);
        statusLabel.setText(text);
    }

    private void highlightPseudo(int activeIndex){
        for(int i=0;i<pseudoLines.length;i++){
            if(i==activeIndex){
                pseudoLines[i].setStyle("-fx-background-color:#374151; -fx-text-fill:#FCD34D; -fx-padding:2; -fx-background-radius:4; -fx-font-size:14px;");
            } else {
                pseudoLines[i].setStyle("-fx-background-color:transparent; -fx-text-fill:white; -fx-padding:2; -fx-font-size:14px;");
            }
        }
    }

    private Duration getStepDuration(){
        double multiplier = 100.0 / (speedSlider != null ? speedSlider.getValue() : 100.0);
        return Duration.millis(500 * multiplier); // base 500ms
    }

    private void playSteps(int i){
        if(i>=steps.size()) return;

        steps.get(i).run();

        PauseTransition pause = new PauseTransition(getStepDuration());
        pause.setOnFinished(e -> playSteps(i+1));
        pause.play();
    }


    @FXML
    public void handleReset(){
        drawGraph();
        statusLabel.setText("Reset done.");
    }

    @FXML
    public void handleBack(ActionEvent event) {
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