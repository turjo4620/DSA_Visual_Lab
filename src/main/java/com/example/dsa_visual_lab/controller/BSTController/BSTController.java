package com.example.dsa_visual_lab.controller.BSTController;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BSTController {

    @FXML private Pane visualPane;
    @FXML private TextField valueField;
    @FXML private Label statusLabel;
    @FXML private Label complexityLabel;
    @FXML private VBox pseudoCodeBox;
    @FXML private HBox controlsBox;
    @FXML private Slider speedSlider;
    @FXML private ComboBox<String> traversalDropdown;

    private TreeNode root;
    private TreeNode activeNode;
    private Color activeColor = Color.web("#FCD34D");

    private final double RADIUS = 22;
    private final double VERTICAL_GAP = 70;

    private static final String CODE_COLOR = "#34D399";
    private static final String HIGHLIGHT_BG = "#374151";
    private static final String HIGHLIGHT_TEXT = "#FCD34D";

    private class TreeNode {
        int value;
        int count;
        TreeNode left, right;

        TreeNode(int value) {
            this.value = value;
            this.count = 1;
        }
    }

    @FXML
    public void initialize() {
        if (complexityLabel != null) {
            complexityLabel.setText("Waiting for action...");
        }
        if (traversalDropdown != null) {
            traversalDropdown.getItems().addAll("Pre-order", "In-order", "Post-order");
            traversalDropdown.getSelectionModel().selectFirst();
        }
        setupPseudoCode(new String[]{""});

        Random rand = new Random();
        root = insertRec(root, 50);
        for (int i = 0; i < 6; i++) {
            root = insertRec(root, rand.nextInt(90) + 10);
        }

        setStatus("Random BST Initialized", false);
        Platform.runLater(this::render);
    }

    private TreeNode insertRec(TreeNode node, int value) {
        if (node == null) return new TreeNode(value);
        if (value < node.value) node.left = insertRec(node.left, value);
        else if (value > node.value) node.right = insertRec(node.right, value);
        else node.count++;
        return node;
    }

    private Duration getStepDuration() {
        double multiplier = 100.0 / (speedSlider != null ? speedSlider.getValue() : 100.0);
        return Duration.millis(500 * multiplier);
    }

    @FXML
    public void onInsert(ActionEvent event) {
        String valStr = valueField.getText().trim();
        if (valStr.isEmpty()) { setStatus("Enter a value", true); return; }

        try {
            int val = Integer.parseInt(valStr);
            valueField.clear();

            if (complexityLabel != null) complexityLabel.setText("O(log N) Avg / O(N) Worst\nInsertion requires traversing tree height.");
            String[] codeLines = {
                    "insert(val):",
                    "  if root == null: root = new Node(val)",
                    "  current = root",
                    "  while current != null:",
                    "    if val < current.val:",
                    "      if current.left == null: current.left = new Node(val); break",
                    "      current = current.left",
                    "    else if val > current.val:",
                    "      if current.right == null: current.right = new Node(val); break",
                    "      current = current.right",
                    "    else: current.count++; break"
            };
            setupPseudoCode(codeLines);

            if (controlsBox != null) controlsBox.setDisable(true);
            setStatus("Animating Insertion for " + val + "...", false);

            if (root == null) {
                PauseTransition step1 = new PauseTransition(getStepDuration());
                step1.setOnFinished(e -> highlightLine(1));

                PauseTransition step2 = new PauseTransition(getStepDuration().multiply(2));
                step2.setOnFinished(e -> {
                    root = new TreeNode(val);
                    activeNode = root;
                    activeColor = Color.web("#4ADE80");
                    render();
                    setStatus("Inserted " + val + " as root", false);
                });

                PauseTransition step3 = new PauseTransition(getStepDuration().multiply(3));
                step3.setOnFinished(e -> finishAnimation());

                step1.play(); step2.play(); step3.play();
            } else {
                PauseTransition step1 = new PauseTransition(getStepDuration());
                step1.setOnFinished(e -> highlightLine(2));

                PauseTransition step2 = new PauseTransition(getStepDuration().multiply(2));
                step2.setOnFinished(e -> {
                    activeNode = root;
                    activeColor = Color.web("#FCD34D");
                    render();
                    animateInsertLoop(root, val);
                });

                step1.play(); step2.play();
            }
        } catch (NumberFormatException e) {
            setStatus("Invalid input!", true);
        }
    }

    private void animateInsertLoop(TreeNode current, int val) {
        PauseTransition checkStep = new PauseTransition(getStepDuration());
        checkStep.setOnFinished(e -> {
            if (val < current.value) {
                highlightLine(4);
                PauseTransition leftCheck = new PauseTransition(getStepDuration());
                leftCheck.setOnFinished(ev -> {
                    if (current.left == null) {
                        highlightLine(5);
                        current.left = new TreeNode(val);
                        activeNode = current.left;
                        activeColor = Color.web("#4ADE80");
                        render();
                        setStatus("Inserted " + val + " to the left", false);
                        delayedFinish();
                    } else {
                        highlightLine(6);
                        activeNode = current.left;
                        render();
                        animateInsertLoop(current.left, val);
                    }
                });
                leftCheck.play();
            } else if (val > current.value) {
                highlightLine(7);
                PauseTransition rightCheck = new PauseTransition(getStepDuration());
                rightCheck.setOnFinished(ev -> {
                    if (current.right == null) {
                        highlightLine(8);
                        current.right = new TreeNode(val);
                        activeNode = current.right;
                        activeColor = Color.web("#4ADE80");
                        render();
                        setStatus("Inserted " + val + " to the right", false);
                        delayedFinish();
                    } else {
                        highlightLine(9);
                        activeNode = current.right;
                        render();
                        animateInsertLoop(current.right, val);
                    }
                });
                rightCheck.play();
            } else {
                highlightLine(10);
                current.count++;
                activeNode = current;
                activeColor = Color.web("#38BDF8");
                render();
                setStatus("Incremented count for " + val, false);
                delayedFinish();
            }
        });
        checkStep.play();
    }

    @FXML
    public void onSearch(ActionEvent event) {
        String valStr = valueField.getText().trim();
        if (valStr.isEmpty() || root == null) { setStatus("Enter value / Tree empty", true); return; }

        try {
            int val = Integer.parseInt(valStr);
            valueField.clear();

            if (complexityLabel != null) complexityLabel.setText("O(log N) Avg / O(N) Worst\nSearch halves the remaining tree each step.");
            String[] codeLines = {
                    "search(val):",
                    "  current = root",
                    "  while current != null:",
                    "    if val == current.val: return True",
                    "    if val < current.val: current = current.left",
                    "    else: current = current.right",
                    "  return False"
            };
            setupPseudoCode(codeLines);

            if (controlsBox != null) controlsBox.setDisable(true);
            setStatus("Searching for " + val + "...", false);

            PauseTransition step1 = new PauseTransition(getStepDuration());
            step1.setOnFinished(e -> {
                highlightLine(1);
                activeNode = root;
                activeColor = Color.web("#FCD34D");
                render();
                animateSearchLoop(root, val);
            });
            step1.play();
        } catch (NumberFormatException e) {
            setStatus("Invalid input!", true);
        }
    }

    private void animateSearchLoop(TreeNode current, int val) {
        if (current == null) {
            highlightLine(6);
            setStatus("Value " + val + " not found.", true);
            delayedFinish();
            return;
        }

        PauseTransition checkStep = new PauseTransition(getStepDuration());
        checkStep.setOnFinished(e -> {
            if (val == current.value) {
                highlightLine(3);
                activeNode = current;
                activeColor = Color.web("#4ADE80");
                render();
                setStatus("Found " + val + "!", false);
                delayedFinish();
            } else if (val < current.value) {
                highlightLine(4);
                PauseTransition move = new PauseTransition(getStepDuration());
                move.setOnFinished(ev -> {
                    activeNode = current.left;
                    render();
                    animateSearchLoop(current.left, val);
                });
                move.play();
            } else {
                highlightLine(5);
                PauseTransition move = new PauseTransition(getStepDuration());
                move.setOnFinished(ev -> {
                    activeNode = current.right;
                    render();
                    animateSearchLoop(current.right, val);
                });
                move.play();
            }
        });
        checkStep.play();
    }

    @FXML
    public void onRemove(ActionEvent event) {
        String valStr = valueField.getText().trim();
        if (valStr.isEmpty() || root == null) { setStatus("Enter value / Tree empty", true); return; }

        try {
            int val = Integer.parseInt(valStr);
            valueField.clear();

            if (complexityLabel != null) complexityLabel.setText("O(log N) Avg / O(N) Worst\nFinds node, then restructures links.");
            String[] codeLines = {
                    "remove(val):",
                    "  node = search(val)",
                    "  if not found: return",
                    "  if node has 2 children:",
                    "    succ = min(node.right)",
                    "    node.val = succ.val",
                    "    remove(succ)",
                    "  if node has 0 or 1 child:",
                    "    replace node with child"
            };
            setupPseudoCode(codeLines);

            if (controlsBox != null) controlsBox.setDisable(true);
            setStatus("Locating " + val + " for removal...", false);

            activeNode = root;
            activeColor = Color.web("#FCD34D");
            render();
            animateRemoveSearch(null, root, val);

        } catch (NumberFormatException e) {
            setStatus("Invalid input!", true);
        }
    }

    private void animateRemoveSearch(TreeNode parent, TreeNode current, int val) {
        if (current == null) {
            highlightLine(2);
            setStatus("Value " + val + " not found for removal.", true);
            delayedFinish();
            return;
        }

        PauseTransition checkStep = new PauseTransition(getStepDuration());
        checkStep.setOnFinished(e -> {
            if (val == current.value) {
                highlightLine(1);
                activeNode = current;
                activeColor = Color.web("#EF4444");
                render();
                setStatus("Found " + val + " to remove.", false);

                PauseTransition proceed = new PauseTransition(getStepDuration().multiply(1.5));
                proceed.setOnFinished(ev -> animateNodeRemoval(parent, current));
                proceed.play();

            } else {
                highlightLine(1);
                activeNode = (val < current.value) ? current.left : current.right;
                activeColor = Color.web("#FCD34D");
                render();
                animateRemoveSearch(current, activeNode, val);
            }
        });
        checkStep.play();
    }

    private void animateNodeRemoval(TreeNode parent, TreeNode target) {
        if (target.count > 1) {
            target.count--;
            setStatus("Decremented count for " + target.value, false);
            finishAnimation();
            return;
        }

        if (target.left != null && target.right != null) {
            highlightLine(3);
            setStatus("Node has 2 children. Finding inorder successor...", false);

            PauseTransition p = new PauseTransition(getStepDuration().multiply(1.5));
            p.setOnFinished(e -> {
                highlightLine(4);
                findSuccessorAndRemove(target, target, target.right);
            });
            p.play();

        } else {
            highlightLine(7);
            setStatus("Node has 0 or 1 child. Replacing...", false);

            PauseTransition p = new PauseTransition(getStepDuration().multiply(1.5));
            p.setOnFinished(e -> {
                highlightLine(8);
                TreeNode child = (target.left != null) ? target.left : target.right;
                if (parent == null) {
                    root = child;
                } else if (parent.left == target) {
                    parent.left = child;
                } else {
                    parent.right = child;
                }
                setStatus("Node removed.", false);
                finishAnimation();
            });
            p.play();
        }
    }

    private void findSuccessorAndRemove(TreeNode targetToReplace, TreeNode parentOfCurr, TreeNode curr) {
        activeNode = curr;
        activeColor = Color.web("#A78BFA");
        render();

        PauseTransition p = new PauseTransition(getStepDuration());
        p.setOnFinished(e -> {
            if (curr.left != null) {
                findSuccessorAndRemove(targetToReplace, curr, curr.left);
            } else {
                setStatus("Found successor: " + curr.value, false);
                highlightLine(5);

                PauseTransition copyP = new PauseTransition(getStepDuration().multiply(1.5));
                copyP.setOnFinished(ev -> {
                    targetToReplace.value = curr.value;
                    targetToReplace.count = curr.count;
                    setStatus("Copied successor value " + curr.value + " to target node.", false);

                    activeNode = targetToReplace;
                    activeColor = Color.web("#34D399");
                    render();

                    PauseTransition removeP = new PauseTransition(getStepDuration().multiply(1.5));
                    removeP.setOnFinished(ev2 -> {
                        highlightLine(6);
                        TreeNode child = curr.right;
                        if (parentOfCurr.left == curr) {
                            parentOfCurr.left = child;
                        } else {
                            parentOfCurr.right = child;
                        }
                        setStatus("Successor node removed.", false);
                        finishAnimation();
                    });
                    removeP.play();
                });
                copyP.play();
            }
        });
        p.play();
    }

    @FXML
    public void onTraverse(ActionEvent event) {
        if (root == null) { setStatus("Tree is empty", true); return; }
        if (traversalDropdown == null || traversalDropdown.getValue() == null) { setStatus("Select traversal type", true); return; }

        String type = traversalDropdown.getValue();
        List<TreeNode> path = new ArrayList<>();

        if (complexityLabel != null) complexityLabel.setText("O(N) Time / O(H) Space\nVisits exactly every node in the tree once.");

        String[] codeLines;
        if (type.equals("Pre-order")) {
            getPreOrder(root, path);
            codeLines = new String[]{"preOrder(node):", "  if node == null: return", "  visit(node)", "  preOrder(node.left)", "  preOrder(node.right)"};
        } else if (type.equals("In-order")) {
            getInOrder(root, path);
            codeLines = new String[]{"inOrder(node):", "  if node == null: return", "  inOrder(node.left)", "  visit(node)", "  inOrder(node.right)"};
        } else {
            getPostOrder(root, path);
            codeLines = new String[]{"postOrder(node):", "  if node == null: return", "  postOrder(node.left)", "  postOrder(node.right)", "  visit(node)"};
        }

        setupPseudoCode(codeLines);
        if (controlsBox != null) controlsBox.setDisable(true);
        setStatus("Starting " + type + " Traversal...", false);

        StringBuilder result = new StringBuilder();
        animateTraversalStep(path, 0, result);
    }

    private void getPreOrder(TreeNode node, List<TreeNode> path) {
        if (node == null) return;
        path.add(node);
        getPreOrder(node.left, path);
        getPreOrder(node.right, path);
    }

    private void getInOrder(TreeNode node, List<TreeNode> path) {
        if (node == null) return;
        getInOrder(node.left, path);
        path.add(node);
        getInOrder(node.right, path);
    }

    private void getPostOrder(TreeNode node, List<TreeNode> path) {
        if (node == null) return;
        getPostOrder(node.left, path);
        getPostOrder(node.right, path);
        path.add(node);
    }

    private void animateTraversalStep(List<TreeNode> path, int index, StringBuilder result) {
        if (index >= path.size()) {
            setStatus("Traversal Complete: " + result.toString(), false);
            delayedFinish();
            return;
        }

        TreeNode node = path.get(index);
        activeNode = node;
        activeColor = Color.web("#A78BFA");
        render();

        result.append(node.value).append(" ");
        setStatus("Result: " + result.toString(), false);

        PauseTransition pause = new PauseTransition(getStepDuration());
        pause.setOnFinished(e -> animateTraversalStep(path, index + 1, result));
        pause.play();
    }

    private void delayedFinish() {
        PauseTransition end = new PauseTransition(getStepDuration().multiply(1.5));
        end.setOnFinished(e -> finishAnimation());
        end.play();
    }

    private void finishAnimation() {
        highlightLine(-1);
        activeNode = null;
        render();
        if (controlsBox != null) controlsBox.setDisable(false);
    }

    private void render() {
        visualPane.getChildren().clear();
        if (root != null) {
            double paneWidth = visualPane.getWidth();
            if (paneWidth < 100) paneWidth = 900;

            double startX = paneWidth / 2;
            double initialHGap = 200;
            renderRecursive(root, startX, 50, initialHGap);
        }
    }

    private void renderRecursive(TreeNode node, double x, double y, double hGap) {
        if (node.left != null) {
            boolean highlightEdge = (node.left == activeNode);
            drawEdge(x, y, x - hGap, y + VERTICAL_GAP, highlightEdge);
            renderRecursive(node.left, x - hGap, y + VERTICAL_GAP, hGap / 2);
        }
        if (node.right != null) {
            boolean highlightEdge = (node.right == activeNode);
            drawEdge(x, y, x + hGap, y + VERTICAL_GAP, highlightEdge);
            renderRecursive(node.right, x + hGap, y + VERTICAL_GAP, hGap / 2);
        }

        Circle circle = new Circle(x, y, RADIUS);
        circle.setFill(Color.web("#1E293B"));

        if (node == activeNode) {
            circle.setStroke(activeColor);
            circle.setStrokeWidth(4);
        } else {
            circle.setStroke(Color.web("#34D399"));
            circle.setStrokeWidth(2);
        }

        Text valText = new Text(String.valueOf(node.value));
        valText.setFont(Font.font("System", FontWeight.BOLD, 14));
        valText.setFill(Color.WHITE);

        StackPane nodeStack = new StackPane(circle, valText);
        nodeStack.setLayoutX(x - RADIUS);
        nodeStack.setLayoutY(y - RADIUS);
        visualPane.getChildren().add(nodeStack);

        if (node.count > 1) {
            drawBadge(x, y, node.count);
        }
    }

    private void drawEdge(double x1, double y1, double x2, double y2, boolean highlight) {
        Line line = new Line(x1, y1, x2, y2);
        if (highlight) {
            line.setStroke(activeColor);
            line.setStrokeWidth(4);
        } else {
            line.setStroke(Color.web("#64748B"));
            line.setStrokeWidth(2);
        }
        visualPane.getChildren().add(0, line);
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
        activeNode = null;
        visualPane.getChildren().clear();
        setStatus("Tree Cleared", false);
        setupPseudoCode(new String[]{""});
        if (complexityLabel != null) complexityLabel.setText("Cleared");
    }

    private void setStatus(String msg, boolean isError) {
        if (statusLabel == null) return;
        statusLabel.setText(msg);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #F87171; -fx-background-color: #334155; -fx-padding: 10 25; -fx-background-radius: 12; -fx-border-color: #475569; -fx-border-radius: 12;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #FCD34D; -fx-background-color: #334155; -fx-padding: 10 25; -fx-background-radius: 12; -fx-border-color: #475569; -fx-border-radius: 12;");
        }
    }

    private void setupPseudoCode(String[] lines) {
        if (pseudoCodeBox == null) return;
        pseudoCodeBox.getChildren().clear();
        for (String line : lines) {
            Label lbl = new Label(line);
            lbl.setTextFill(Color.web(CODE_COLOR));
            lbl.setFont(Font.font("Consolas", 14));
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setWrapText(true);
            lbl.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
            lbl.setStyle("-fx-padding: 4; -fx-background-radius: 4;");
            pseudoCodeBox.getChildren().add(lbl);
        }
    }

    private void highlightLine(int index) {
        if (pseudoCodeBox == null) return;
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