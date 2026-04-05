package com.example.dsa_visual_lab.server;

import java.io.*;
import java.net.*;

public class AuthServer {
    private static final int PORT = 8080;
    private static FileAuthManager authManager = new FileAuthManager();

    public static void main(String[] args) {
        System.out.println("Starting Authentication Server on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running! Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClientRequest(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            if (request != null) {
                String[] parts = request.split(",");

                if (parts.length == 3) {
                    String action = parts[0];
                    String username = parts[1];
                    String password = parts[2];

                    if (action.equals("LOGIN")) {
                        out.println(authManager.authenticateUser(username, password) ? "SUCCESS" : "FAILURE");
                    } else if (action.equals("REGISTER")) {
                        out.println(authManager.registerUser(username, password) ? "SUCCESS" : "FAILURE");
                    }
                } else {
                    out.println("ERROR");
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}