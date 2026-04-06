package com.example.dsa_visual_lab.controller.linear;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    @FXML private Label statusLabel, complexityLabel;
    @FXML private TextField valueField, indexField, initValuesField;
    @FXML private VBox pseudoCodeBox;
    @FXML private Node controlsBox;

    private final LinkedList<String> listData = new LinkedList<>();

    private static final double START_X = 50;
    private static final double START_Y = 150;
    private static final double NODE_WIDTH = 80;
    private static final double NODE_HEIGHT = 50;
    private static final double GAP = 60;

    private static final String CODE_COLOR = "#34D399";
    private static final String HIGHLIGHT_BG = "#334155";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    private static final String COLOR_NEW_NODE  = "#F59E0B";
    private static final String COLOR_ACTIVE    = "#38BDF8";
    private static final String COLOR_POINTER   = "#A78BFA";
    private static final String COLOR_DELETE    = "#F87171";
    private static final String COLOR_FOUND     = "#FCD34D";

    private void setControlsDisabled(boolean disabled) {
        if (controlsBox != null) {
            controlsBox.setDisable(disabled);
        }
    }

    @FXML
    void onInsertHead(ActionEvent event) {
        String val = valueField.getText().trim();
        if (val.isEmpty()) { setStatus("Enter a value", true); return; }

        setControlsDisabled(true);
        complexityLabel.setText("O(1) - Constant Time\nDirectly adjusts the head pointer.");
        String[] codeLines = {
                "insertHead(val):",
                "  newNode = new Node(val)",
                "  newNode.next = head",
                "  head = newNode"
        };
        setupPseudoCode(codeLines);
        setStatus("Step 1: Creating new node...", false);

        PauseTransition s1 = pause(0.0, () -> {
            highlightLine(1);
            setStatus("Step 1: Creating new node \"" + val + "\"", false);
            showPendingNode(val, START_X, START_Y - 90, COLOR_NEW_NODE, false);
        });

        PauseTransition s2 = pause(1.0, () -> {
            highlightLine(2);
            setStatus("Step 2: newNode.next → current head", false);
            showPendingNode(val, START_X, START_Y - 90, COLOR_NEW_NODE, true);
        });

        PauseTransition s3 = pause(1.9, () -> {
            highlightLine(3);
            setStatus("Step 3: head pointer updated ✓", false);
            listData.addFirst(val);
            renderWithHighlight(0, COLOR_NEW_NODE);
            valueField.clear();
        });

        PauseTransition s4 = pause(3.0, () -> {
            highlightLine(-1);
            render();
            setStatus("Inserted \"" + val + "\" at head ✓", false);
            setControlsDisabled(false);
        });

        s1.play(); s2.play(); s3.play(); s4.play();
    }

    @FXML
    void onInsertTail(ActionEvent event) {
        String val = valueField.getText().trim();
        if (val.isEmpty()) { setStatus("Enter a value", true); return; }

        setControlsDisabled(true);
        complexityLabel.setText("O(N) or O(1)\nO(1) if a tail pointer is maintained.");
        String[] codeLines = {
                "insertTail(val):",
                "  newNode = new Node(val)",
                "  if head == null: head = newNode",
                "  else: tail.next = newNode",
                "  tail = newNode"
        };
        setupPseudoCode(codeLines);

        double newNodeX = START_X + (listData.size()) * (NODE_WIDTH + GAP);

        PauseTransition s1 = pause(0.0, () -> {
            highlightLine(1);
            setStatus("Step 1: Creating new node \"" + val + "\"", false);
            showPendingNode(val, newNodeX, START_Y - 90, COLOR_NEW_NODE, false);
        });

        PauseTransition s2 = pause(1.0, () -> {
            highlightLine(3);
            setStatus("Step 2: tail.next → new node", false);
            showPendingNode(val, newNodeX, START_Y - 90, COLOR_NEW_NODE, true);
        });

        PauseTransition s3 = pause(1.9, () -> {
            highlightLine(4);
            setStatus("Step 3: tail pointer updated ✓", false);
            listData.addLast(val);
            renderWithHighlight(listData.size() - 1, COLOR_NEW_NODE);
            valueField.clear();
        });

        PauseTransition s4 = pause(3.0, () -> {
            highlightLine(-1);
            render();
            setStatus("Inserted \"" + val + "\" at tail ✓", false);
            setControlsDisabled(false);
        });

        s1.play(); s2.play(); s3.play(); s4.play();
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

            setControlsDisabled(true);
            complexityLabel.setText("O(N) - Linear Time\nRequires traversal to the specified index.");
            String[] codeLines = {
                    "insertAtIndex(val, idx):",
                    "  traverse to node before idx",
                    "  newNode.next = temp.next",
                    "  temp.next = newNode"
            };
            setupPseudoCode(codeLines);

            PauseTransition s1 = pause(0.0, () -> {
                highlightLine(1);
                setStatus("Step 1: Traversing to index " + idx + "...", false);
                animateTraversal(idx == 0 ? 0 : idx - 1, () -> {});
            });

            double pendingX = START_X + idx * (NODE_WIDTH + GAP);

            PauseTransition s2 = pause(0.6 + idx * 0.5, () -> {
                highlightLine(2);
                setStatus("Step 2: Creating node, linking to next", false);
                showPendingNode(val, pendingX, START_Y - 90, COLOR_NEW_NODE, true);
            });

            PauseTransition s3 = pause(0.6 + idx * 0.5 + 1.0, () -> {
                highlightLine(3);
                setStatus("Step 3: Inserting at index " + idx + " ✓", false);
                listData.add(idx, val);
                renderWithHighlight(idx, COLOR_NEW_NODE);
                valueField.clear();
                indexField.clear();
            });

            PauseTransition s4 = pause(0.6 + idx * 0.5 + 2.2, () -> {
                highlightLine(-1);
                render();
                setStatus("Inserted \"" + val + "\" at index " + idx + " ✓", false);
                setControlsDisabled(false);
            });

            s1.play(); s2.play(); s3.play(); s4.play();

        } catch (NumberFormatException e) {
            setStatus("Invalid Index", true);
        }
    }

    @FXML
    void onDeleteHead(ActionEvent event) {
        if (listData.isEmpty()) { setStatus("List is empty", true); return; }

        setControlsDisabled(true);
        complexityLabel.setText("O(1) - Constant Time\nDirectly updates the head pointer.");
        String[] codeLines = {
                "deleteHead():",
                "  if head == null: return",
                "  temp = head",
                "  head = head.next",
                "  free(temp)"
        };
        setupPseudoCode(codeLines);

        PauseTransition s1 = pause(0.0, () -> {
            highlightLine(2);
            setStatus("Step 1: Mark head node for removal", false);
            renderWithHighlight(0, COLOR_DELETE);
        });

        PauseTransition s2 = pause(0.9, () -> {
            highlightLine(3);
            setStatus("Step 2: head pointer → head.next", false);
            if (listData.size() > 1) renderWithHighlight(1, COLOR_POINTER);
        });

        PauseTransition s3 = pause(1.8, () -> {
            highlightLine(4);
            setStatus("Step 3: Free old head ✓", false);
            animateFadeOutNode(0, () -> {
                listData.removeFirst();
                render();
            });
        });

        PauseTransition s4 = pause(3.2, () -> {
            highlightLine(-1);
            setStatus("Deleted head ✓", false);
            setControlsDisabled(false);
        });

        s1.play(); s2.play(); s3.play(); s4.play();
    }

    @FXML
    void onDeleteTail(ActionEvent event) {
        if (listData.isEmpty()) { setStatus("List is empty", true); return; }

        setControlsDisabled(true);
        complexityLabel.setText("O(N) - Linear Time\nRequires traversal to the second-to-last node.");
        String[] codeLines = {
                "deleteTail():",
                "  traverse to 2nd to last node",
                "  temp = tail",
                "  tail = 2nd to last node",
                "  tail.next = null"
        };
        setupPseudoCode(codeLines);

        int tailIdx = listData.size() - 1;

        PauseTransition s1 = pause(0.0, () -> {
            highlightLine(1);
            setStatus("Step 1: Traversing to second-to-last...", false);
            animateTraversal(Math.max(0, tailIdx - 1), () -> {});
        });

        PauseTransition s2 = pause(0.6 + tailIdx * 0.5, () -> {
            highlightLine(2);
            setStatus("Step 2: Mark tail for removal", false);
            renderWithHighlight(tailIdx, COLOR_DELETE);
        });

        PauseTransition s3 = pause(0.6 + tailIdx * 0.5 + 0.9, () -> {
            highlightLine(4);
            setStatus("Step 3: new tail.next = null ✓", false);
            animateFadeOutNode(tailIdx, () -> {
                listData.removeLast();
                render();
            });
        });

        PauseTransition s4 = pause(0.6 + tailIdx * 0.5 + 2.2, () -> {
            highlightLine(-1);
            setStatus("Deleted tail ✓", false);
            setControlsDisabled(false);
        });

        s1.play(); s2.play(); s3.play(); s4.play();
    }

    @FXML
    void onDeleteIndex(ActionEvent event) {
        try {
            int idx = Integer.parseInt(indexField.getText().trim());
            if (idx < 0 || idx >= listData.size()) {
                setStatus("Index out of bounds", true);
                return;
            }

            setControlsDisabled(true);
            complexityLabel.setText("O(N) - Linear Time\nRequires traversal to the specified node.");
            String[] codeLines = {
                    "deleteAtIndex(idx):",
                    "  traverse to node before idx",
                    "  nodeToRemove = temp.next",
                    "  temp.next = nodeToRemove.next",
                    "  free(nodeToRemove)"
            };
            setupPseudoCode(codeLines);

            PauseTransition s1 = pause(0.0, () -> {
                highlightLine(1);
                setStatus("Step 1: Traversing to index " + idx + "...", false);
                animateTraversal(Math.max(0, idx - 1), () -> {});
            });

            PauseTransition s2 = pause(0.6 + idx * 0.5, () -> {
                highlightLine(2);
                setStatus("Step 2: Mark node at index " + idx + " for removal", false);
                renderWithHighlight(idx, COLOR_DELETE);
            });

            PauseTransition s3 = pause(0.6 + idx * 0.5 + 0.9, () -> {
                highlightLine(3);
                setStatus("Step 3: prev.next → node.next", false);
                if (idx + 1 < listData.size()) renderWithHighlight2(idx, COLOR_DELETE, idx + 1, COLOR_POINTER);
            });

            PauseTransition s4 = pause(0.6 + idx * 0.5 + 1.8, () -> {
                highlightLine(4);
                setStatus("Step 4: Free node ✓", false);
                animateFadeOutNode(idx, () -> {
                    listData.remove(idx);
                    render();
                });
                indexField.clear();
            });

            PauseTransition s5 = pause(0.6 + idx * 0.5 + 3.2, () -> {
                highlightLine(-1);
                setStatus("Deleted at index " + idx + " ✓", false);
                setControlsDisabled(false);
            });

            s1.play(); s2.play(); s3.play(); s4.play(); s5.play();

        } catch (NumberFormatException e) {
            setStatus("Invalid Index", true);
        }
    }

    @FXML
    void onSearch(ActionEvent event) {
        String target = valueField.getText().trim();
        if (target.isEmpty()) { setStatus("Enter a value to search", true); return; }

        int idx = listData.indexOf(target);

        setControlsDisabled(true);
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
        setStatus("Searching for \"" + target + "\"...", false);

        int stopAt = (idx == -1) ? listData.size() - 1 : idx;

        PauseTransition s1 = pause(0.0, () -> {
            highlightLine(1);
            setStatus("Step 1: Start at head", false);
        });

        for (int i = 0; i <= stopAt; i++) {
            final int nodeIdx = i;
            final boolean isTarget = (nodeIdx == idx);
            PauseTransition step = pause(0.4 + nodeIdx * 0.7, () -> {
                highlightLine(isTarget ? 3 : 4);
                setStatus("Checking index " + nodeIdx + ": \"" + listData.get(nodeIdx) + "\"", false);
                renderWithHighlight(nodeIdx, isTarget ? COLOR_FOUND : COLOR_POINTER);
            });
            step.play();
        }

        double totalTime = 0.4 + (stopAt + 1) * 0.7 + 0.5;
        PauseTransition finish = pause(totalTime, () -> {
            if (idx != -1) {
                highlightLine(3);
                setStatus("Found \"" + target + "\" at index " + idx + " ✓", false);
                renderWithHighlight(idx, COLOR_FOUND);
            } else {
                highlightLine(5);
                setStatus("\"" + target + "\" not found in list", true);
                render();
            }
        });

        PauseTransition cleanup = pause(totalTime + 1.8, () -> {
            highlightLine(-1);
            render();
            setControlsDisabled(false);
        });

        s1.play(); finish.play(); cleanup.play();
    }

    @FXML
    void onCreateList(ActionEvent event) {
        String valStr = initValuesField.getText().trim();
        listData.clear();
        if (!valStr.isEmpty()) {
            for (String s : valStr.split(",")) listData.add(s.trim());
            setStatus("List initialized with " + listData.size() + " elements.", false);
        } else {
            setStatus("Empty list created.", false);
        }
        render();
        pseudoCodeBox.getChildren().clear();
        complexityLabel.setText("Ready");
    }

    private void render() {
        visualPane.getChildren().clear();
        double currentX = START_X;
        for (int i = 0; i < listData.size(); i++) {
            StackPane node = createNode(listData.get(i), COLOR_ACTIVE);
            node.setLayoutX(currentX);
            node.setLayoutY(START_Y);
            visualPane.getChildren().add(node);

            if (i < listData.size() - 1) {
                drawArrow(currentX + NODE_WIDTH, START_Y + NODE_HEIGHT / 2,
                        currentX + NODE_WIDTH + GAP, START_Y + NODE_HEIGHT / 2, false);
            } else {
                addNullLabel(currentX + NODE_WIDTH + 15, START_Y + NODE_HEIGHT / 2 + 5);
            }
            currentX += NODE_WIDTH + GAP;
        }
        visualPane.setMinWidth(currentX + 100);
    }

    private void renderWithHighlight(int highlightIdx, String highlightColor) {
        visualPane.getChildren().clear();
        double currentX = START_X;
        for (int i = 0; i < listData.size(); i++) {
            String color = (i == highlightIdx) ? highlightColor : COLOR_ACTIVE;
            StackPane node = createNode(listData.get(i), color);
            node.setLayoutX(currentX);
            node.setLayoutY(START_Y);

            if (i == highlightIdx) {
                node.setScaleX(1.12);
                node.setScaleY(1.12);
            }
            visualPane.getChildren().add(node);

            if (i < listData.size() - 1) {
                drawArrow(currentX + NODE_WIDTH, START_Y + NODE_HEIGHT / 2,
                        currentX + NODE_WIDTH + GAP, START_Y + NODE_HEIGHT / 2, false);
            } else {
                addNullLabel(currentX + NODE_WIDTH + 15, START_Y + NODE_HEIGHT / 2 + 5);
            }
            currentX += NODE_WIDTH + GAP;
        }
        visualPane.setMinWidth(currentX + 100);
    }

    private void renderWithHighlight2(int idx1, String color1, int idx2, String color2) {
        visualPane.getChildren().clear();
        double currentX = START_X;
        for (int i = 0; i < listData.size(); i++) {
            String color = COLOR_ACTIVE;
            if (i == idx1) color = color1;
            else if (i == idx2) color = color2;
            StackPane node = createNode(listData.get(i), color);
            node.setLayoutX(currentX);
            node.setLayoutY(START_Y);
            visualPane.getChildren().add(node);

            if (i < listData.size() - 1) {
                drawArrow(currentX + NODE_WIDTH, START_Y + NODE_HEIGHT / 2,
                        currentX + NODE_WIDTH + GAP, START_Y + NODE_HEIGHT / 2, false);
            } else {
                addNullLabel(currentX + NODE_WIDTH + 15, START_Y + NODE_HEIGHT / 2 + 5);
            }
            currentX += NODE_WIDTH + GAP;
        }
        visualPane.setMinWidth(currentX + 100);
    }

    private void showPendingNode(String val, double x, double y, String color, boolean showArrow) {
        render();

        StackPane pending = createNode(val, color);
        pending.setLayoutX(x);
        pending.setLayoutY(y);

        pending.setScaleX(0.1);
        pending.setScaleY(0.1);
        ScaleTransition popIn = new ScaleTransition(Duration.millis(350), pending);
        popIn.setToX(1.0);
        popIn.setToY(1.0);

        visualPane.getChildren().add(pending);

        Text label = new Text("new node");
        label.setFill(Color.web(color));
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        label.setLayoutX(x + 5);
        label.setLayoutY(y - 8);
        visualPane.getChildren().add(label);

        if (showArrow) {
            drawArrow(x + NODE_WIDTH / 2, y + NODE_HEIGHT,
                    x + NODE_WIDTH / 2, START_Y - 5, true);
        }

        popIn.play();
    }

    private void animateTraversal(int targetIdx, Runnable onDone) {
        if (listData.isEmpty()) { onDone.run(); return; }
        for (int i = 0; i <= targetIdx; i++) {
            final int ni = i;
            PauseTransition t = new PauseTransition(Duration.seconds(0.5 * ni));
            t.setOnFinished(e -> renderWithHighlight(ni, COLOR_POINTER));
            t.play();
        }
        PauseTransition done = new PauseTransition(Duration.seconds(0.5 * (targetIdx + 1)));
        done.setOnFinished(e -> { render(); onDone.run(); });
        done.play();
    }

    private void animateFadeOutNode(int idx, Runnable onDone) {
        int childIdx = idx * 2;
        if (childIdx < visualPane.getChildren().size()) {
            Node n = visualPane.getChildren().get(childIdx);
            FadeTransition ft = new FadeTransition(Duration.millis(600), n);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> onDone.run());
            ft.play();
        } else {
            onDone.run();
        }
    }

    private StackPane createNode(String val, String strokeColor) {
        StackPane stack = new StackPane();

        Rectangle box = new Rectangle(NODE_WIDTH, NODE_HEIGHT);
        box.setFill(Color.web("#1E293B"));
        box.setStroke(Color.web(strokeColor));
        box.setStrokeWidth(2.5);
        box.setArcWidth(8);
        box.setArcHeight(8);

        Line separator = new Line(NODE_WIDTH * 0.7, 0, NODE_WIDTH * 0.7, NODE_HEIGHT);
        separator.setStroke(Color.web(strokeColor));
        separator.setOpacity(0.5);

        Text text = new Text(val);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        text.setTranslateX(-8);

        stack.getChildren().addAll(box, separator, text);
        return stack;
    }

    private void drawArrow(double startX, double startY, double endX, double endY, boolean dashed) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.web(dashed ? "#FCD34D" : "#94A3B8"));
        line.setStrokeWidth(dashed ? 1.5 : 2);
        if (dashed) line.getStrokeDashArray().addAll(6.0, 4.0);

        double dx = endX - startX;
        double dy = endY - startY;
        double len = Math.sqrt(dx * dx + dy * dy);
        double ux = dx / len, uy = dy / len;

        Polygon arrowHead = new Polygon();
        double tipX = endX, tipY = endY;
        double baseX = tipX - ux * 10, baseY = tipY - uy * 10;
        double perpX = -uy * 5, perpY = ux * 5;
        arrowHead.getPoints().addAll(
                tipX, tipY,
                baseX + perpX, baseY + perpY,
                baseX - perpX, baseY - perpY
        );
        arrowHead.setFill(Color.web(dashed ? "#FCD34D" : "#94A3B8"));

        visualPane.getChildren().addAll(line, arrowHead);
    }

    private void addNullLabel(double x, double y) {
        Text nullText = new Text("NULL");
        nullText.setFill(Color.web("#94A3B8"));
        nullText.setStyle("-fx-font-size: 16px;");
        nullText.setLayoutX(x);
        nullText.setLayoutY(y);
        visualPane.getChildren().add(nullText);
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(Color.web(isError ? "#F87171" : "#FCD34D"));
    }

    private void setupPseudoCode(String[] lines) {
        pseudoCodeBox.getChildren().clear();
        for (String line : lines) {
            Label lbl = new Label(line);
            lbl.setTextFill(Color.web(CODE_COLOR));
            lbl.setFont(Font.font("Consolas", 16));
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setStyle("-fx-padding: 2;");
            pseudoCodeBox.getChildren().add(lbl);
        }
    }

    private void highlightLine(int index) {
        for (int i = 0; i < pseudoCodeBox.getChildren().size(); i++) {
            Label lbl = (Label) pseudoCodeBox.getChildren().get(i);
            if (i == index) {
                lbl.setStyle("-fx-padding: 2; -fx-background-color: " + HIGHLIGHT_BG +
                        "; -fx-background-radius: 2;");
                lbl.setTextFill(Color.web(HIGHLIGHT_TEXT));
            } else {
                lbl.setStyle("-fx-padding: 2; -fx-background-color: transparent;");
                lbl.setTextFill(Color.web(CODE_COLOR));
            }
        }
    }

    private PauseTransition pause(double seconds, Runnable action) {
        PauseTransition pt = new PauseTransition(Duration.seconds(seconds));
        pt.setOnFinished(e -> action.run());
        return pt;
    }

    @FXML
    void onBackClick(ActionEvent event) {
        try {
            String path = "/com/example/dsa_visual_lab/view/home/linear-dataStructures.fxml";
            if (getClass().getResource(path) == null) {
                path = "/com/example/dsa_visual_lab/view/Linear-DataStructure/linear-dataStructures.fxml";
            }
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}