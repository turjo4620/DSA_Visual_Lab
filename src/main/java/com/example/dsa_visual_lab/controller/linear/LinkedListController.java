package com.example.dsa_visual_lab.controller.linear;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.LinkedList;

public class LinkedListController {

    @FXML private Pane visualPane;
    @FXML private Label statusLabel;
    @FXML private TextField valueField, indexField;
    @FXML private Label complexityLabel;
    @FXML private VBox pseudoCodeBox;
    @FXML private HBox controlsBox;

    private final LinkedList<String> listData = new LinkedList<>();

    private static final double START_X = 50;
    private static final double START_Y = 150;
    private static final double NODE_WIDTH = 80;
    private static final double NODE_HEIGHT = 50;
    private static final double GAP = 60;

    private static final String CODE_COLOR = "#34D399";
    private static final String HIGHLIGHT_BG = "#374151";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    @FXML
    void onInsertHead(ActionEvent event) {
        String val = valueField.getText().trim();
        if (val.isEmpty()) { setStatus("Enter a value", true); return; }

        complexityLabel.setText("O(1) - Constant Time\nDirectly adjusts the head pointer.");
        String[] codeLines = {
                "insertHead(val):",
                "  newNode = new Node(val)",
                "  newNode.next = head",
                "  head = newNode"
        };
        setupPseudoCode(codeLines);
        controlsBox.setDisable(true);
        setStatus("Animating Insert Head...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> highlightLine(2));

        PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
        step3.setOnFinished(e -> {
            highlightLine(3);
            listData.addFirst(val);
            render();
            setStatus("Inserted " + val + " at Head", false);
            valueField.clear();
        });

        PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
        step4.setOnFinished(e -> {
            highlightLine(-1);
            controlsBox.setDisable(false);
        });

        step1.play(); step2.play(); step3.play(); step4.play();
    }

    @FXML
    void onInsertTail(ActionEvent event) {
        String val = valueField.getText().trim();
        if (val.isEmpty()) { setStatus("Enter a value", true); return; }

        complexityLabel.setText("O(N) or O(1)\nO(1) if a tail pointer is maintained.");
        String[] codeLines = {
                "insertTail(val):",
                "  newNode = new Node(val)",
                "  if head == null: head = newNode",
                "  else: tail.next = newNode",
                "  tail = newNode"
        };
        setupPseudoCode(codeLines);
        controlsBox.setDisable(true);
        setStatus("Animating Insert Tail...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> highlightLine(3));

        PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
        step3.setOnFinished(e -> {
            highlightLine(4);
            listData.addLast(val);
            render();
            setStatus("Inserted " + val + " at Tail", false);
            valueField.clear();
        });

        PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
        step4.setOnFinished(e -> {
            highlightLine(-1);
            controlsBox.setDisable(false);
        });

        step1.play(); step2.play(); step3.play(); step4.play();
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

            complexityLabel.setText("O(N) - Linear Time\nRequires traversal to the specified index.");
            String[] codeLines = {
                    "insertAtIndex(val, idx):",
                    "  traverse to node before idx",
                    "  newNode.next = temp.next",
                    "  temp.next = newNode"
            };
            setupPseudoCode(codeLines);
            controlsBox.setDisable(true);
            setStatus("Animating Insert at Index...", false);

            PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
            step1.setOnFinished(e -> highlightLine(1));

            PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
            step2.setOnFinished(e -> highlightLine(2));

            PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
            step3.setOnFinished(e -> {
                highlightLine(3);
                listData.add(idx, val);
                render();
                setStatus("Inserted " + val + " at index " + idx, false);
                valueField.clear();
                indexField.clear();
            });

            PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
            step4.setOnFinished(e -> {
                highlightLine(-1);
                controlsBox.setDisable(false);
            });

            step1.play(); step2.play(); step3.play(); step4.play();

        } catch (NumberFormatException e) {
            setStatus("Invalid Index", true);
        }
    }

    @FXML
    void onDeleteHead(ActionEvent event) {
        if (listData.isEmpty()) { setStatus("List is empty", true); return; }

        complexityLabel.setText("O(1) - Constant Time\nDirectly updates the head pointer.");
        String[] codeLines = {
                "deleteHead():",
                "  if head == null: return",
                "  temp = head",
                "  head = head.next",
                "  free(temp)"
        };
        setupPseudoCode(codeLines);
        controlsBox.setDisable(true);
        setStatus("Animating Delete Head...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(2));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> highlightLine(3));

        PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
        step3.setOnFinished(e -> {
            highlightLine(4);
            listData.removeFirst();
            render();
            setStatus("Deleted Head", false);
        });

        PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
        step4.setOnFinished(e -> {
            highlightLine(-1);
            controlsBox.setDisable(false);
        });

        step1.play(); step2.play(); step3.play(); step4.play();
    }

    @FXML
    void onDeleteTail(ActionEvent event) {
        if (listData.isEmpty()) { setStatus("List is empty", true); return; }

        complexityLabel.setText("O(N) - Linear Time\nRequires traversal to the second-to-last node.");
        String[] codeLines = {
                "deleteTail():",
                "  traverse to 2nd to last node",
                "  temp = tail",
                "  tail = 2nd to last node",
                "  tail.next = null"
        };
        setupPseudoCode(codeLines);
        controlsBox.setDisable(true);
        setStatus("Animating Delete Tail...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(1));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
        step2.setOnFinished(e -> highlightLine(3));

        PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
        step3.setOnFinished(e -> {
            highlightLine(4);
            listData.removeLast();
            render();
            setStatus("Deleted Tail", false);
        });

        PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
        step4.setOnFinished(e -> {
            highlightLine(-1);
            controlsBox.setDisable(false);
        });

        step1.play(); step2.play(); step3.play(); step4.play();
    }

    @FXML
    void onDeleteIndex(ActionEvent event) {
        try {
            int idx = Integer.parseInt(indexField.getText().trim());
            if (idx < 0 || idx >= listData.size()) {
                setStatus("Index out of bounds", true);
                return;
            }

            complexityLabel.setText("O(N) - Linear Time\nRequires traversal to the specified node.");
            String[] codeLines = {
                    "deleteAtIndex(idx):",
                    "  traverse to node before idx",
                    "  nodeToRemove = temp.next",
                    "  temp.next = nodeToRemove.next",
                    "  free(nodeToRemove)"
            };
            setupPseudoCode(codeLines);
            controlsBox.setDisable(true);
            setStatus("Animating Delete at Index...", false);

            PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
            step1.setOnFinished(e -> highlightLine(1));

            PauseTransition step2 = new PauseTransition(Duration.seconds(1.2));
            step2.setOnFinished(e -> highlightLine(3));

            PauseTransition step3 = new PauseTransition(Duration.seconds(1.9));
            step3.setOnFinished(e -> {
                highlightLine(4);
                listData.remove(idx);
                render();
                setStatus("Deleted at index " + idx, false);
                indexField.clear();
            });

            PauseTransition step4 = new PauseTransition(Duration.seconds(2.6));
            step4.setOnFinished(e -> {
                highlightLine(-1);
                controlsBox.setDisable(false);
            });

            step1.play(); step2.play(); step3.play(); step4.play();

        } catch (NumberFormatException e) {
            setStatus("Invalid Index", true);
        }
    }

    @FXML
    void onSearch(ActionEvent event) {
        String target = valueField.getText().trim();
        if (target.isEmpty()) { setStatus("Enter a value to search", true); return; }

        int idx = listData.indexOf(target);

        complexityLabel.setText("O(N) - Linear Time\nMust iterate through nodes to find value.");
        String[] codeLines = {
                "search(val):",
                "  temp = head",
                "  while temp != null:",
                "    if temp.data == val: return True",
                "    temp = temp.next",
                "  return False"
        };
        setupPseudoCode(codeLines);
        controlsBox.setDisable(true);
        setStatus("Animating Search...", false);

        PauseTransition step1 = new PauseTransition(Duration.seconds(0.5));
        step1.setOnFinished(e -> highlightLine(2));

        PauseTransition step2 = new PauseTransition(Duration.seconds(1.5));
        step2.setOnFinished(e -> {
            if (idx != -1) {
                highlightLine(3);
                setStatus("Found " + target + " at index " + idx, false);
                highlightNode(idx);
            } else {
                highlightLine(5);
                setStatus("Value not found", true);
            }
        });

        PauseTransition step3 = new PauseTransition(Duration.seconds(3.0));
        step3.setOnFinished(e -> {
            highlightLine(-1);
            controlsBox.setDisable(false);
        });

        step1.play(); step2.play(); step3.play();
    }

    private void render() {
        visualPane.getChildren().clear();
        double currentX = START_X;

        for (int i = 0; i < listData.size(); i++) {
            StackPane node = createNode(listData.get(i));
            node.setLayoutX(currentX);
            node.setLayoutY(START_Y);
            visualPane.getChildren().add(node);

            if (i < listData.size() - 1) {
                drawArrow(currentX + NODE_WIDTH, START_Y + NODE_HEIGHT / 2,
                        currentX + NODE_WIDTH + GAP, START_Y + NODE_HEIGHT / 2);
            } else {
                Text nullText = new Text("NULL");
                nullText.setFill(Color.web("#64748B"));
                nullText.setLayoutX(currentX + NODE_WIDTH + 15);
                nullText.setLayoutY(START_Y + NODE_HEIGHT / 2 + 5);
                visualPane.getChildren().add(nullText);
            }
            currentX += NODE_WIDTH + GAP;
        }
        visualPane.setMinWidth(currentX + 100);
    }

    private StackPane createNode(String val) {
        StackPane stack = new StackPane();

        Rectangle box = new Rectangle(NODE_WIDTH, NODE_HEIGHT);
        box.setFill(Color.web("#1E293B"));
        box.setStroke(Color.web("#818CF8"));
        box.setStrokeWidth(2);
        box.setArcWidth(10);
        box.setArcHeight(10);

        Line separator = new Line(NODE_WIDTH * 0.7, 0, NODE_WIDTH * 0.7, NODE_HEIGHT);
        separator.setStroke(Color.web("#818CF8"));

        Text text = new Text(val);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        text.setTranslateX(-8);

        stack.getChildren().addAll(box, separator, text);
        return stack;
    }

    private void drawArrow(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.web("#94A3B8"));
        line.setStrokeWidth(2);

        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(0.0, -5.0, 10.0, 0.0, 0.0, 5.0);
        arrowHead.setFill(Color.web("#94A3B8"));
        arrowHead.setLayoutX(endX - 10);
        arrowHead.setLayoutY(endY);

        visualPane.getChildren().addAll(line, arrowHead);
    }

    private void highlightNode(int index) {
        int childIndex = index * 3;
        if (childIndex < visualPane.getChildren().size()) {
            Node n = visualPane.getChildren().get(childIndex);
            if (n instanceof StackPane) {
                StackPane node = (StackPane) n;
                Rectangle box = (Rectangle) node.getChildren().get(0);

                box.setStroke(Color.web("#4ADE80"));
                box.setStrokeWidth(4);

                PauseTransition reset = new PauseTransition(Duration.seconds(1.5));
                reset.setOnFinished(e -> {
                    box.setStroke(Color.web("#818CF8"));
                    box.setStrokeWidth(2);
                });
                reset.play();
            }
        }
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
            lbl.setMaxWidth(Double.MAX_VALUE);
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
    void onBackClick(ActionEvent event) {
        try {
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