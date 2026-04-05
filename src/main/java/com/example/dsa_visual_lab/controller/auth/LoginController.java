package com.example.dsa_visual_lab.controller.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    @FXML
    public void handleLoginClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        String response = sendRequestToServer("LOGIN," + username + "," + password);

        if ("SUCCESS".equals(response)) {
            statusLabel.setText("Success! Loading application...");
            loadHomeScene(event);
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    public void handleRegisterClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        String response = sendRequestToServer("REGISTER," + username + "," + password);

        if ("SUCCESS".equals(response)) {
            statusLabel.setText("Registered! You can now log in.");
        } else {
            statusLabel.setText("Username taken or server error.");
        }
    }

    private String sendRequestToServer(String message) {
        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            return "ERROR";
        }
    }

    private void loadHomeScene(ActionEvent event) {
        try {
            // NOTE: Make sure this path points to your actual home-view.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading home screen.");
        }
    }
}